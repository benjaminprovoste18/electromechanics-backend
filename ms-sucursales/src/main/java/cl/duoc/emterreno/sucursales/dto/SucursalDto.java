package cl.duoc.emterreno.sucursales.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SucursalDto(
        String id,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre del cliente debe tener entre 3 y 80 caracteres")
        String nombreCliente,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(min = 5, max = 120, message = "La dirección debe tener entre 5 y 120 caracteres")
        String direccion,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-56.0", message = "La latitud debe estar dentro de Chile")
        @DecimalMax(value = "-17.0", message = "La latitud debe estar dentro de Chile")
        Double latitud,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-76.0", message = "La longitud debe estar dentro de Chile")
        @DecimalMax(value = "-66.0", message = "La longitud debe estar dentro de Chile")
        Double longitud,

        Long actualizadoEn
) {
}
