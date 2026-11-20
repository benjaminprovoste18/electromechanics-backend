package cl.duoc.emterreno.jornadas.dto;

import cl.duoc.emterreno.jornadas.model.TipoMarcacion;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record JornadaDto(
        String id,

        @NotBlank(message = "El empleado es obligatorio")
        String empleadoId,

        @NotNull(message = "El tipo de marcación es obligatorio (ENTRADA o SALIDA)")
        TipoMarcacion tipo,

        @NotNull(message = "La fecha y hora de la marcación es obligatoria")
        Long fechaHora,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
        @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
        Double latitud,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
        @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
        Double longitud,

        @NotNull(message = "La precisión del GPS es obligatoria")
        @PositiveOrZero(message = "La precisión no puede ser negativa")
        Double precisionMetros,

        String fotoRespaldo,
        String sucursalCercanaId,
        Double distanciaMetros,
        Boolean dentroDeRadio,
        Boolean ubicacionSimulada,
        Boolean aprobado,
        Long actualizadoEn
) {
}
