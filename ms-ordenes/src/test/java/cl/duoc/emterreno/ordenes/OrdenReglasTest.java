package cl.duoc.emterreno.ordenes;

import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.service.OrdenReglas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdenReglasTest {

    @Test
    void permiteAvanzarDeEstado() {
        assertDoesNotThrow(() -> OrdenReglas.validarTransicion(EstadoServicio.PENDIENTE, EstadoServicio.EN_PROCESO));
        assertDoesNotThrow(() -> OrdenReglas.validarTransicion(EstadoServicio.EN_PROCESO, EstadoServicio.FINALIZADO));
        assertDoesNotThrow(() -> OrdenReglas.validarTransicion(EstadoServicio.PENDIENTE, EstadoServicio.FINALIZADO));
    }

    @Test
    void noPermiteRetrocederDeEstado() {
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarTransicion(EstadoServicio.FINALIZADO, EstadoServicio.PENDIENTE));
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarTransicion(EstadoServicio.EN_PROCESO, EstadoServicio.PENDIENTE));
    }

    @Test
    void ordenEnProcesoSinHoraDeInicioEsInvalida() {
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarCoherencia(EstadoServicio.EN_PROCESO, null, null, false));
    }

    @Test
    void ordenFinalizadaNecesitaHoraDeTerminoPosteriorAlInicio() {
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarCoherencia(EstadoServicio.FINALIZADO, 1000L, null, false));
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarCoherencia(EstadoServicio.FINALIZADO, 2000L, 1000L, false));
        assertDoesNotThrow(
                () -> OrdenReglas.validarCoherencia(EstadoServicio.FINALIZADO, 1000L, 2000L, true));
    }

    @Test
    void soloSeApruebanOrdenesFinalizadas() {
        assertThrows(ReglaNegocioException.class,
                () -> OrdenReglas.validarCoherencia(EstadoServicio.EN_PROCESO, 1000L, null, true));
    }

    @Test
    void actividadesSoloEnOrdenesIniciadas() {
        assertFalse(OrdenReglas.puedeRegistrarActividad(EstadoServicio.PENDIENTE));
        assertTrue(OrdenReglas.puedeRegistrarActividad(EstadoServicio.EN_PROCESO));
        assertTrue(OrdenReglas.puedeRegistrarActividad(EstadoServicio.FINALIZADO));
    }

    @Test
    void soloSeEliminanOrdenesPendientes() {
        assertTrue(OrdenReglas.puedeEliminarse(EstadoServicio.PENDIENTE));
        assertFalse(OrdenReglas.puedeEliminarse(EstadoServicio.EN_PROCESO));
        assertFalse(OrdenReglas.puedeEliminarse(EstadoServicio.FINALIZADO));
    }
}
