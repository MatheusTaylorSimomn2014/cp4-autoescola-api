package com.fiap.autoescola.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de endereco do aluno/instrutor")
public class EnderecoDTO {

    @NotBlank(message = "CEP e obrigatorio")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato 00000-000")
    @Schema(example = "01310-100")
    private String cep;

    @Schema(example = "Avenida Paulista")
    private String logradouro;

    @Schema(example = "1000")
    private String numero;

    private String complemento;

    @Schema(example = "Bela Vista")
    private String bairro;

    @Schema(example = "Sao Paulo")
    private String cidade;

    @Schema(example = "SP")
    private String uf;
}
