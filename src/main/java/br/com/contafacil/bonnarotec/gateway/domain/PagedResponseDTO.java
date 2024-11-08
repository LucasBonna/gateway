package br.com.contafacil.bonnarotec.gateway.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {

    @Schema(description = "Conteudo da pagina atual")
    private List<T> content;

    @Schema(description = "Numero da pagina atual", example = "0")
    private int pageNumber;

    @Schema(description = "Tamanho da página", example = "10")
    private int pageSize;

    @Schema(description = "Número total de elementos", example = "50")
    private long totalElements;

    @Schema(description = "Número total de páginas", example = "5")
    private int totalPages;

    @Schema(description = "Se é a última página", example = "false")
    private boolean last;
}
