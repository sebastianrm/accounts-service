package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response paginado genérico.
 *
 * <p>Contiene la página actual, tamaño, contenido, total de elementos
 * y total de páginas, así como indicadores de primera y última página.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PageResponse", description = "Respuesta paginada genérica que contiene una página de resultados y metadatos de paginación.")
public class PageResponse<T> {

    @Schema(description = "Lista de elementos en la página actual.")
    private List<T> content;

    @Schema(description = "Número de página actual (base 0).", example = "0")
    private int page;

    @Schema(description = "Tamaño de la página (número de elementos por página).", example = "20")
    private int size;

    @Schema(description = "Total de elementos disponibles en todas las páginas.", example = "150")
    private long totalElements;

    @Schema(description = "Total de páginas disponibles.", example = "8")
    private int totalPages;

    @Schema(description = "Indica si es la primera página.", example = "true")
    private boolean first;

    @Schema(description = "Indica si es la última página.", example = "false")
    private boolean last;
}