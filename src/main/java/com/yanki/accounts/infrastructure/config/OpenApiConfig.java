package com.yanki.accounts.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 (springdoc) para documentación de la API.
 *
 * <p>Define el título, versión, descripción y esquema de seguridad Bearer JWT
 * utilizado en los endpoints REST del servicio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Yanki Accounts Service API")
                        .version("1.0.0")
                        .description(
                                "API REST para la gestión de cuentas bancarias del ecosistema Yanki.\n\n"
                                + "**Funcionalidades principales:**\n"
                                + "- Apertura y cierre de cuentas (ahorro, corriente, plazo fijo)\n"
                                + "- Consulta de saldos con caché Redis\n"
                                + "- Depósitos y retiros con validación de reglas de negocio\n"
                                + "- Consulta de movimientos con filtros y paginación\n"
                                + "- Liquidación de pagos por débito automático y transferencias\n\n"
                                + "**Tipos de cuenta:**\n"
                                + "- **SAVINGS**: Cuenta de ahorro para clientes PERSONAL\n"
                                + "- **CURRENT**: Cuenta corriente para clientes PERSONAL y ENTERPRISE\n"
                                + "- **TERM_FIXED**: Cuenta a plazo fijo con tasa y vencimiento\n\n"
                                + "**Monedas soportadas:** PEN (Sol), USD (Dólar)\n\n"
                                + "**Seguridad:** Todos los endpoints requieren autenticación Bearer JWT."
                        )
                        .contact(new Contact()
                                .name("Equipo Yanki Platform")
                                .email("semorale@emeal.nttdata.com")
                                .url("https://yanki.bank"))
                        .license(new License().name("Proprietary").url("https://yanki.bank/license")))
                .addTagsItem(new Tag()
                        .name("Accounts")
                        .description(
                                "Operaciones CRUD y transaccionales de cuentas bancarias.\n\n"
                                + "Incluye apertura, consulta, depósitos, retiros, cierre y consulta de movimientos.\n"
                                + "Los filtros en GET /api/accounts soportan combinaciones de customerId, type y status."
                        ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint de autenticación.")));
    }
}