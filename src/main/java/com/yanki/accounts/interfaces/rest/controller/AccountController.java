package com.yanki.accounts.interfaces.rest.controller;

import com.yanki.accounts.domain.service.AccountSchedulerService;
import com.yanki.accounts.domain.service.AccountService;
import com.yanki.accounts.domain.service.AccountValidationResult;
import com.yanki.accounts.domain.service.DepositCommand;
import com.yanki.accounts.domain.service.MovementFilterCriteria;
import com.yanki.accounts.domain.service.WithdrawalCommand;
import com.yanki.accounts.interfaces.rest.dto.AccountRequest;
import com.yanki.accounts.interfaces.rest.dto.AccountResponse;
import com.yanki.accounts.interfaces.rest.dto.AccountValidationResponse;
import com.yanki.accounts.interfaces.rest.dto.BalanceResponse;
import com.yanki.accounts.interfaces.rest.dto.DepositRequest;
import com.yanki.accounts.interfaces.rest.dto.MovementResponse;
import com.yanki.accounts.interfaces.rest.dto.PageResponse;
import com.yanki.accounts.interfaces.rest.dto.WithdrawalRequest;
import com.yanki.accounts.interfaces.rest.dto.ApiError;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST para gestión de cuentas bancarias.
 *
 * <p>Endpoints resourceful según CONVENTIONS.md §4:</p>
 * <ul>
 *   <li>POST /api/accounts - Abrir cuenta</li>
 *   <li>GET /api/accounts - Listar cuentas con filtros (patrón Strategy)</li>
 *   <li>GET /api/accounts/{id} - Obtener cuenta por ID</li>
 *   <li>GET /api/accounts/{id}/balance - Consultar saldo (caché Redis)</li>
 *   <li>GET /api/accounts/{id}/movements - Movimientos con paginación</li>
 *   <li>POST /api/accounts/{id}/deposits - Depósito</li>
 *   <li>POST /api/accounts/{id}/withdrawals - Retiro</li>
 *   <li>DELETE /api/accounts/{id} - Cerrar cuenta</li>
 * </ul>
 *
 * <p>Los filtros en {@code GET /api/accounts} soportan cualquier combinación de
 * {@code customerId}, {@code type} y {@code status} mediante el patrón Strategy.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Gestión de cuentas bancarias: ahorro, corriente, plazo fijo")
public class AccountController {

    private final AccountService accountService;
    private final AccountSchedulerService accountSchedulerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Abre una nueva cuenta bancaria",
        description = "Crea una nueva cuenta bancaria (ahorro, corriente o plazo fijo) para un cliente existente. " +
            "Valida que el cliente no tenga deuda vencida, que no exista ya una cuenta del mismo tipo " +
            "(para SAVINGS) y aplica las reglas de negocio según el tipo de cliente y cuenta solicitada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Cuenta creada exitosamente. Se genera un número de cuenta único y se publica el evento account.opened.v1.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de solicitud inválidos. Verifique los campos obligatorios y los formatos.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Regla de negocio violada. Por ejemplo: cliente con deuda vencida, cuenta de ahorro ya existente, o tipo de cuenta no permitido para el perfil del cliente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos para abrir una nueva cuenta bancaria.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AccountRequest.class),
            examples = {
                @ExampleObject(name = "Cuenta de ahorro personal", summary = "Apertura de cuenta SAVINGS para cliente PERSONAL",
                    value = "{\"customerId\":\"CUST001\",\"accountType\":\"SAVINGS\",\"currency\":\"PEN\",\"initialAmount\":500.00,\"channel\":\"WEB\"}"),
                @ExampleObject(name = "Cuenta a plazo fijo", summary = "Apertura de cuenta TERM_FIXED con plazo de 90 días",
                    value = "{\"customerId\":\"CUST001\",\"accountType\":\"TERM_FIXED\",\"currency\":\"USD\",\"initialAmount\":10000.00,\"termDays\":90,\"allowedMovementDay\":15,\"channel\":\"BRANCH\"}")
            }
        )
    )
    public Mono<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        log.info("[REST] Solicitud para abrir cuenta: customerId={}, type={}, amount={}, channel={}",
                request.getCustomerId(), request.getAccountType(), request.getInitialAmount(), request.getChannel());
        try {
            return Mono.fromFuture(() -> singleToCompletableFuture(
                    accountService.openAccount(request.toCommand())))
                    .map(result -> {
                        log.info("[REST] Cuenta creada exitosamente: accountId={}, customerId={}",
                                result.getAccountId(), result.getCustomerId());
                        return AccountResponse.from(result);
                    })
                    .doOnError(throwable -> log.error("[REST] Error al crear cuenta: customerId={}, type={} - {}",
                            request.getCustomerId(), request.getAccountType(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al crear cuenta: customerId={} - {}",
                    request.getCustomerId(), e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping
    @Operation(
        summary = "Lista cuentas con filtros y paginación",
        description = "Retorna una lista paginada de cuentas bancarias aplicando los filtros proporcionados. " +
            "Los filtros se combinan mediante el patrón Strategy y soportan cualquier combinación de customerId, type y status. " +
            "Si no se proporcionan filtros, retorna todas las cuentas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cuentas encontradas con metadatos de paginación.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        )
    })
    public Mono<PageResponse<AccountResponse>> findAll(
            @Parameter(description = "Filtrar por identificador del cliente propietario.", example = "CUST001")
            @RequestParam(value = "customerId", required = false) String customerId,
            @Parameter(description = "Filtrar por tipo de cuenta.", example = "SAVINGS", schema = @Schema(allowableValues = {"SAVINGS", "CURRENT", "TERM_FIXED"}))
            @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Filtrar por estado de la cuenta.", example = "OPEN", schema = @Schema(allowableValues = {"OPEN", "CLOSED", "BLOCKED"}))
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Número de página (base 0).", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (máximo 100).", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.debug("[REST] Listar cuentas: customerId={}, type={}, status={}, page={}, size={}",
                customerId, type, status, page, size);

        try {
            if (size > 100) {
                log.debug("[REST] Tamaño de página ajustado de {} a 100", size);
                size = 100;
            }

            com.yanki.accounts.domain.service.AccountFilterCriteria criteria =
                    com.yanki.accounts.domain.service.AccountFilterCriteria.builder()
                            .customerId(customerId)
                            .type(type)
                            .status(status)
                            .build();

            int pageSize = size;
            return Mono.fromFuture(() -> flowableToListCompletableFuture(
                            accountService.findByFilters(criteria)))
                    .map(list -> {
                        log.debug("[REST] Cuentas encontradas: {} resultados", list.size());
                        return PageResponse.<AccountResponse>builder()
                                .content(list.stream().map(AccountResponse::from).collect(java.util.stream.Collectors.toList()))
                                .page(page)
                                .size(pageSize)
                                .totalElements(list.size())
                                .totalPages((int) Math.ceil((double) list.size() / pageSize))
                                .first(page == 0)
                                .last(page >= (int) Math.ceil((double) list.size() / pageSize) - 1)
                                .build();
                    })
                    .doOnError(throwable -> log.error("[REST] Error al listar cuentas - {}", throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al listar cuentas - {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtiene una cuenta por ID",
        description = "Retorna la información completa de una cuenta bancaria identificada por su ID único, " +
            "incluyendo saldo, moneda, estado y, para cuentas empresariales, la lista de titulares y firmantes."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cuenta encontrada con todos sus detalles.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<AccountResponse> findById(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id) {
        log.debug("[REST] Buscar cuenta por ID: {}", id);
        try {
            return Mono.fromFuture(() -> singleToCompletableFuture(accountService.findById(id)))
                    .map(response -> {
                        log.debug("[REST] Cuenta encontrada: id={}, type={}", id, response.getAccountType());
                        return AccountResponse.from(response);
                    })
                    .doOnError(throwable -> log.error("[REST] Error al buscar cuenta por ID: {} - {}", id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al buscar cuenta por ID: {} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping("/{id}/balance")
    @Operation(
        summary = "Consulta el saldo disponible de una cuenta",
        description = "Retorna el saldo actual de una cuenta bancaria. El valor se obtiene desde caché Redis " +
            "si está disponible, o directamente desde MongoDB si no. El campo 'cached' indica la fuente del dato."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Saldo consultado exitosamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<BalanceResponse> getBalance(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id) {
        log.debug("[REST] Consultar saldo: accountId={}", id);
        try {
            return Mono.fromFuture(() -> singleToCompletableFuture(accountService.getBalance(id)))
                    .map(balanceResult -> {
                        log.debug("[REST] Saldo consultado: accountId={}, cached={}", id, balanceResult.isCached());
                        return BalanceResponse.builder()
                                .accountId(id)
                                .balance(balanceResult.getBalance())
                                .currency("PEN")
                                .cached(balanceResult.isCached())
                                .build();
                    })
                    .doOnError(throwable -> log.error("[REST] Error al consultar saldo: accountId={} - {}", id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al consultar saldo: accountId={} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping("/{id}/validate-transfer")
    @Operation(
        summary = "Valida una cuenta para transferencias inter-servicio",
        description = "Endpoint utilizado por transfers-service para validar que una cuenta existe, " +
            "está operativa (OPEN) y cumple los requisitos antes de iniciar una transferencia. " +
            "Incluye saldo, tipo de producto, límites de movimientos y estado de deuda vencida del cliente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cuenta validada exitosamente. El campo 'valid' indica si la cuenta puede usarse en transferencias.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountValidationResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<AccountValidationResponse> validateForTransfer(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id) {
        log.debug("[REST] Validar cuenta para transferencia: accountId={}", id);
        try {
            return Mono.fromFuture(() -> singleToCompletableFuture(accountService.validateForTransfer(id)))
                    .map(result -> {
                        log.debug("[REST] Validación completada: accountId={}, valid={}, status={}",
                                id, result.isValid(), result.getStatus());
                        return AccountValidationResponse.builder()
                                .valid(result.isValid())
                                .accountId(result.getAccountId())
                                .customerId(result.getCustomerId())
                                .status(result.getStatus())
                                .balance(result.getBalance())
                                .productType(result.getProductType())
                                .maxFreeMovements(result.getMaxFreeMovements())
                                .commissionPerExcessMovement(result.getCommissionPerExcessMovement())
                                .hasOverdueDebt(result.isHasOverdueDebt())
                                .build();
                    })
                    .doOnError(throwable -> log.error("[REST] Error al validar cuenta para transferencia: accountId={} - {}",
                            id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al validar cuenta para transferencia: accountId={} - {}",
                    id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping("/{id}/movements")
    @Operation(
        summary = "Lista movimientos de una cuenta con paginación y filtros",
        description = "Retorna una lista paginada de movimientos bancarios de una cuenta específica. " +
            "Soporta filtros por rango de fechas y tipo de movimiento. Los tipos incluyen DEPOSIT, WITHDRAWAL, " +
            "TRANSFER_IN, TRANSFER_OUT, COMMISSION, PAYMENT e INTEREST."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de movimientos encontrados con metadatos de paginación.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<PageResponse<MovementResponse>> getMovements(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Fecha de inicio del filtro (inclusive). Formato ISO 8601.", example = "2026-01-01T00:00:00")
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @Parameter(description = "Fecha de fin del filtro (inclusive). Formato ISO 8601.", example = "2026-11-14T23:59:59")
            @RequestParam(value = "to", required = false) LocalDateTime to,
            @Parameter(description = "Filtrar por tipo de movimiento.", example = "DEPOSIT", schema = @Schema(allowableValues = {"DEPOSIT", "WITHDRAWAL", "TRANSFER_IN", "TRANSFER_OUT", "COMMISSION", "PAYMENT", "INTEREST"}))
            @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Número de página (base 0).", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (máximo 100).", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.debug("[REST] Listar movimientos: accountId={}, from={}, to={}, type={}, page={}, size={}",
                id, from, to, type, page, size);

        try {
            if (size > 100) {
                log.debug("[REST] Tamaño de página ajustado de {} a 100", size);
                size = 100;
            }

            MovementFilterCriteria criteria = MovementFilterCriteria.builder()
                    .from(from)
                    .to(to)
                    .type(type)
                    .build();

            int pageSize = size;
            return Mono.fromFuture(() -> flowableToListCompletableFuture(
                            accountService.findMovements(id, criteria)))
                    .map(list -> {
                        log.debug("[REST] Movimientos encontrados: accountId={}, count={}", id, list.size());
                        return PageResponse.<MovementResponse>builder()
                                .content(list.stream().map(MovementResponse::fromMovement)
                                        .collect(java.util.stream.Collectors.toList()))
                                .page(page)
                                .size(pageSize)
                                .totalElements(list.size())
                                .totalPages((int) Math.ceil((double) list.size() / pageSize))
                                .first(page == 0)
                                .last(page >= (int) Math.ceil((double) list.size() / pageSize) - 1)
                                .build();
                    })
                    .doOnError(throwable -> log.error("[REST] Error al listar movimientos: accountId={} - {}",
                            id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al listar movimientos: accountId={} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @PostMapping("/{id}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Realiza un depósito en la cuenta",
        description = "Acredita un monto a la cuenta especificada. El depósito incrementa el saldo disponible " +
            "inmediatamente y genera un movimiento de tipo DEPOSIT. Se publica el evento account.deposited.v1."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Depósito realizado exitosamente. El saldo se actualiza inmediatamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "La cuenta está bloqueada o cerrada y no puede recibir depósitos.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del depósito a realizar.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DepositRequest.class),
            examples = {
                @ExampleObject(name = "Depósito web", summary = "Depósito desde banca en línea",
                    value = "{\"amount\":500.00,\"description\":\"Depósito de sueldo\",\"channel\":\"WEB\",\"referenceId\":\"REF001\"}")
            }
        )
    )
    public Mono<MovementResponse> deposit(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id,
            @Valid @RequestBody DepositRequest request) {
        log.info("[REST] Depósito solicitado: accountId={}, amount={}, channel={}",
                id, request.getAmount(), request.getChannel());
        try {
            DepositCommand command = DepositCommand.builder()
                    .accountId(id)
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .channel(request.getChannel())
                    .referenceId(request.getReferenceId())
                    .build();
            return Mono.fromFuture(() -> singleToCompletableFuture(accountService.deposit(command)))
                    .map(response -> {
                        log.info("[REST] Depósito exitoso: accountId={}, amount={}, balanceAfter={}",
                                id, request.getAmount(), response.getBalanceAfter());
                        return MovementResponse.from(response);
                    })
                    .doOnError(throwable -> log.error("[REST] Error en depósito: accountId={}, amount={} - {}",
                            id, request.getAmount(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado en depósito: accountId={} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @PostMapping("/{id}/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Realiza un retiro de la cuenta",
        description = "Debita un monto de la cuenta especificada. Valida saldo suficiente, límites diarios/mensuales, " +
            "reglas de movimientos excedentes y comisiones aplicables. Genera un movimiento de tipo WITHDRAWAL. " +
            "Se publica el evento account.withdrawn.v1."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Retiro realizado exitosamente. El saldo se actualiza inmediatamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Saldo insuficiente, límites excedidos, cuenta bloqueada o reglas de negocio violadas.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del retiro a realizar.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = WithdrawalRequest.class),
            examples = {
                @ExampleObject(name = "Retiro en cajero", summary = "Retiro desde cajero automático",
                    value = "{\"amount\":200.00,\"description\":\"Retiro en cajero\",\"channel\":\"ATM\",\"referenceId\":\"REF002\"}")
            }
        )
    )
    public Mono<MovementResponse> withdrawal(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id,
            @Valid @RequestBody WithdrawalRequest request) {
        log.info("[REST] Retiro solicitado: accountId={}, amount={}, channel={}",
                id, request.getAmount(), request.getChannel());
        try {
            WithdrawalCommand command = WithdrawalCommand.builder()
                    .accountId(id)
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .channel(request.getChannel())
                    .referenceId(request.getReferenceId())
                    .build();
            return Mono.fromFuture(() -> singleToCompletableFuture(accountService.withdrawal(command)))
                    .map(response -> {
                        log.info("[REST] Retiro exitoso: accountId={}, amount={}, balanceAfter={}",
                                id, request.getAmount(), response.getBalanceAfter());
                        return MovementResponse.from(response);
                    })
                    .doOnError(throwable -> log.error("[REST] Error en retiro: accountId={}, amount={} - {}",
                            id, request.getAmount(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado en retiro: accountId={} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Cierra una cuenta (saldo debe ser 0)",
        description = "Cierra una cuenta bancaria activa. El saldo debe ser exactamente 0 para poder cerrar la cuenta. " +
            "Al cerrar, el estado cambia a CLOSED, se registra la fecha de cierre y se publica el evento account.closed.v1."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Cuenta cerrada exitosamente. El estado cambia a CLOSED."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "El saldo de la cuenta no es 0. Realice operaciones para vaciar el saldo antes de cerrar.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<Void> closeAccount(
            @Parameter(description = "Identificador único de la cuenta a cerrar.", example = "ACC001", required = true)
            @PathVariable("id") String id) {
        log.info("[REST] Cierre de cuenta solicitado: accountId={}", id);
        try {
            return Mono.fromFuture(() -> completableToCompletableFuture(accountService.closeAccount(id)))
                    .doOnSuccess(v -> log.info("[REST] Cuenta cerrada exitosamente: accountId={}", id))
                    .doOnError(throwable -> log.error("[REST] Error al cerrar cuenta: accountId={} - {}", id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al cerrar cuenta: accountId={} - {}", id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    // ==================== Endpoints Admin ====================

    @PostMapping("/jobs/maintenance-commission")
    @Operation(
        summary = "Ejecuta manualmente el job de comisiones de mantenimiento",
        description = "Dispara la ejecución inmediata del job que aplica las comisiones de mantenimiento " +
            "mensual a todas las cuentas corriente con maintenanceCommission > 0. " +
            "Usado por administradores para pruebas y recuperación."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Job de comisiones de mantenimiento ejecutado exitosamente."
        )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> triggerMaintenanceCommission() {
        log.info("[REST] Trigger manual: job de comisiones de mantenimiento");
        try {
            accountSchedulerService.triggerMaintenanceCommission();
            log.info("[REST] Job de comisiones de mantenimiento ejecutado");
            return Mono.empty();
        } catch (Exception e) {
            log.error("[REST] Error ejecutando job de comisiones de mantenimiento - {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @PostMapping("/jobs/vip-average-check")
    @Operation(
        summary = "Ejecuta manualmente el job de verificación de promedio VIP",
        description = "Dispara la ejecución inmediata del job que verifica el promedio diario del mes anterior " +
            "para cuentas VIP. Si el promedio está por debajo del mínimo, aplica la penalidad correspondiente. " +
            "Usado por administradores para pruebas y recuperación."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Job de verificación de promedio VIP ejecutado exitosamente."
        )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> triggerVipAverageCheck() {
        log.info("[REST] Trigger manual: job de verificación de promedio VIP");
        try {
            accountSchedulerService.triggerVipAverageCheck();
            log.info("[REST] Job de verificación de promedio VIP ejecutado");
            return Mono.empty();
        } catch (Exception e) {
            log.error("[REST] Error ejecutando job de verificación VIP - {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @GetMapping("/{id}/movement-count")
    @Operation(
        summary = "Obtiene el contador de movimientos del mes actual",
        description = "Retorna la cantidad de movimientos realizados en la cuenta durante el mes en curso. " +
            "Útil para verificar el límite de movimientos gratuitos antes de realizar una operación."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contador de movimientos del mes actual.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovementCountResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró una cuenta con el ID proporcionado.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
        )
    })
    public Mono<MovementCountResponse> getMovementCount(
            @Parameter(description = "Identificador único de la cuenta.", example = "ACC001", required = true)
            @PathVariable("id") String id) {
        log.debug("[REST] Consultar contador de movimientos: accountId={}", id);
        try {
            return Mono.fromFuture(() -> singleToCompletableFuture(accountSchedulerService.getMovementCount(id)))
                    .map(count -> {
                        log.debug("[REST] Contador de movimientos: accountId={}, count={}", id, count);
                        return MovementCountResponse.builder()
                                .accountId(id)
                                .movementCount(count)
                                .month(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                                .build();
                    })
                    .doOnError(throwable -> log.error("[REST] Error al consultar contador de movimientos: accountId={} - {}",
                            id, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[REST] Error inesperado al consultar contador de movimientos: accountId={} - {}",
                    id, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    /**
     * DTO de respuesta para el contador de movimientos.
     */
    @lombok.Data
    @lombok.Builder
    public static class MovementCountResponse {
        @Schema(description = "Identificador único de la cuenta.", example = "ACC001")
        private String accountId;

        @Schema(description = "Cantidad de movimientos del mes actual.", example = "5")
        private Integer movementCount;

        @Schema(description = "Mes al que corresponde el contador (formato yyyy-MM).", example = "2026-11")
        private String month;
    }

    // Helper methods to bridge RxJava to Reactor without blocking

    private static <T> CompletableFuture<T> singleToCompletableFuture(Single<T> single) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        single.subscribe(cf::complete, cf::completeExceptionally);
        return cf;
    }

    private static <T> CompletableFuture<java.util.List<T>> flowableToListCompletableFuture(
            io.reactivex.rxjava3.core.Flowable<T> flowable) {
        CompletableFuture<java.util.List<T>> cf = new CompletableFuture<>();
        flowable.toList().subscribe(cf::complete, cf::completeExceptionally);
        return cf;
    }

    private static CompletableFuture<Void> completableToCompletableFuture(Completable completable) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        completable.subscribe(
                () -> cf.complete(null),
                cf::completeExceptionally
        );
        return cf;
    }
}