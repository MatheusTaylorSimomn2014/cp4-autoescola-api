package br.com.fiap.autoescola.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoUpdateDTO {
    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}")
    private String telefone;

    @NotNull
    @Valid
    private EnderecoDTO endereco;
}