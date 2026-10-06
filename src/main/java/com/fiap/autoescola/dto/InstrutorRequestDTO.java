package com.fiap.autoescola.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload para criacao/atualizacao de instrutor")
public class InstrutorRequestDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 3, max = 120)
    @Schema(example = "Carlos Eduardo")
    private String nome;

    @NotBlank(message = "CPF e obrigatorio")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF invalido")
    @Schema(example = "123.456.789-00")
    private String cpf;

    @NotBlank(message = "CNH e obrigatoria")
    @Schema(example = "01234567890")
    private String cnh;

    @NotNull(message = "Data de nascimento e obrigatoria")
    @Past(message = "Data de nascimento deve estar no passado")
    @Schema(example = "1985-05-10")
    private LocalDate dataNascimento;

    @Valid
    private EnderecoDTO endereco;
}
