package cl.duoc.emterreno.jornadas.dto;

public record ResumenHorasDto(
        String empleadoId,
        long horasMillis,
        int marcaciones,
        int fueraDeRadio,
        boolean enJornada
) {
}
