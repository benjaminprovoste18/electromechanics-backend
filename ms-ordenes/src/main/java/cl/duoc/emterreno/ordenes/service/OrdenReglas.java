package cl.duoc.emterreno.ordenes.service;

import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;

public final class OrdenReglas {

    private OrdenReglas() {
    }

    public static void validarTransicion(EstadoServicio actual, EstadoServicio nuevo) {
        if (nuevo.ordinal() < actual.ordinal()) {
            throw new ReglaNegocioException(
                    "Una orden " + actual + " no puede volver al estado " + nuevo
            );
        }
    }

    public static void validarCoherencia(EstadoServicio estado, Long horaInicio, Long horaTermino, boolean aprobada) {
        if (estado != EstadoServicio.PENDIENTE && horaInicio == null) {
            throw new ReglaNegocioException("Una orden " + estado + " debe tener hora de inicio");
        }
        if (estado == EstadoServicio.FINALIZADO) {
            if (horaTermino == null) {
                throw new ReglaNegocioException("Una orden FINALIZADO debe tener hora de término");
            }
            if (horaTermino < horaInicio) {
                throw new ReglaNegocioException("La hora de término no puede ser anterior a la hora de inicio");
            }
        }
        if (aprobada && estado != EstadoServicio.FINALIZADO) {
            throw new ReglaNegocioException("Solo se pueden aprobar órdenes finalizadas");
        }
    }

    public static boolean puedeRegistrarActividad(EstadoServicio estado) {
        return estado == EstadoServicio.EN_PROCESO || estado == EstadoServicio.FINALIZADO;
    }

    public static boolean puedeEliminarse(EstadoServicio estado) {
        return estado == EstadoServicio.PENDIENTE;
    }
}
