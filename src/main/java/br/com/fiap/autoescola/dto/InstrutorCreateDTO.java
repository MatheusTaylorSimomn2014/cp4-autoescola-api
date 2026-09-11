package br.com.fiap.autoescola.dto;

import br.com.fiap.autoescola.entity.Especialidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrutorCreateDTO {
    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}")
    private String telefone;

    @NotBlank
    @Size(max = 20)
    private String cnh;

    @NotNull
    private Especialidade especialidade;

    @NotNull
    @Valid
    private EnderecoDTO endereco;
}