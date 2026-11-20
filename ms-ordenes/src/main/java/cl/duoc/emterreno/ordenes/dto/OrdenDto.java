package cl.duoc.emterreno.ordenes.dto;

import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.model.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrdenDto(
        String id,

        @NotBlank(message = "El técnico asignado es obligatorio")
        String tecnicoId,

        @NotBlank(message = "La sucursal del cliente es obligatoria")
        String sucursalId,

        @NotNull(message = "La fecha programada es obligatoria")
        Long fechaProgramada,

        EstadoServicio estado,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 15, max = 300, message = "La descripción debe tener entre 15 y 300 caracteres")
        String descripcion,

        @NotBlank(message = "Las actividades requeridas son obligatorias")
        @Size(min = 10, max = 500, message = "Las actividades requeridas deben tener entre 10 y 500 caracteres")
        String actividadesRequeridas,

        @NotNull(message = "La prioridad es obligatoria")
        Prioridad prioridad,

        String fotoInicial,
        String fotoFinal,
        Long horaInicio,
        Long horaTermino,
        Boolean aprobada,
        Long creadaEn,
        Long actualizadoEn
) {
}
