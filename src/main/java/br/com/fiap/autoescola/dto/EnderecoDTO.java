package br.com.fiap.autoescola.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoDTO {
    @NotBlank
    @Size(max = 150)
    private String logradouro;

    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @NotBlank
    @Size(max = 100)
    private String bairro;

    @NotBlank
    @Size(max = 100)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    private String uf;

    @NotBlank
    @Pattern(regexp = "\\d{5}-?\\d{3}")
    private String cep;
}