package br.edu.exemplo.jogoenigma.dto;
import jakarta.validation.constraints.*;
public record ParticipanteRequest(@NotBlank String nome,@NotBlank @Email String email,@Min(0) @Max(100) Integer pontos) {}
