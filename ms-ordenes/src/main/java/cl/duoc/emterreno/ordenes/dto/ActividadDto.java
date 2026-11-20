package cl.duoc.emterreno.ordenes.dto;

import cl.duoc.emterreno.ordenes.model.EstadoEquipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActividadDto(
        String id,

        @NotBlank(message = "La orden de trabajo es obligatoria")
        String ordenId,

        @NotBlank(message = "El detalle de la tarea es obligatorio")
        @Size(min = 10, max = 500, message = "El detalle debe tener entre 10 y 500 caracteres")
        String detalle,

        @Size(max = 300, message = "Los repuestos no pueden superar 300 caracteres")
        String repuestos,

        @NotNull(message = "El estado del equipo es obligatorio")
        EstadoEquipo estadoEquipo,

        @NotNull(message = "La hora de registro es obligatoria")
        Long horaRegistro,

        @Size(max = 500, message = "Los comentarios no pueden superar 500 caracteres")
        String comentarios,

        Long actualizadoEn
) {
}
