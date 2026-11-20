package cl.duoc.emterreno.jornadas;

import cl.duoc.emterreno.jornadas.model.RegistroJornada;
import cl.duoc.emterreno.jornadas.model.TipoMarcacion;
import cl.duoc.emterreno.jornadas.service.JornadaReglas;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JornadaReglasTest {

    private static final long HORA = 60 * 60 * 1000L;

    private RegistroJornada marca(TipoMarcacion tipo, long hora) {
        RegistroJornada r = new RegistroJornada();
        r.setTipo(tipo);
        r.setFechaHora(hora);
        return r;
    }

    @Test
    void laDistanciaEntreElMismoPuntoEsCero() {
        assertEquals(0.0, JornadaReglas.distanciaMetros(-33.511, -70.758, -33.511, -70.758), 0.001);
    }

    @Test
    void calculaLaDistanciaConHaversine() {
        double distancia = JornadaReglas.distanciaMetros(-33.5110, -70.7580, -33.4950, -70.7570);
        assertEquals(1781, distancia, 15);
    }

    @Test
    void elRadioValidoEsDeTrescientosMetros() {
        assertTrue(JornadaReglas.dentroDeRadio(120.0));
        assertTrue(JornadaReglas.dentroDeRadio(300.0));
        assertFalse(JornadaReglas.dentroDeRadio(300.1));
        assertFalse(JornadaReglas.dentroDeRadio(null));
    }

    @Test
    void sumaLasHorasEntreEntradaYSalida() {
        List<RegistroJornada> marcas = List.of(
                marca(TipoMarcacion.ENTRADA, 8 * HORA),
                marca(TipoMarcacion.SALIDA, 13 * HORA),
                marca(TipoMarcacion.ENTRADA, 14 * HORA),
                marca(TipoMarcacion.SALIDA, 18 * HORA)
        );
        assertEquals(9 * HORA, JornadaReglas.horasTrabajadas(marcas, 20 * HORA));
    }

    @Test
    void unaJornadaAbiertaCuentaHastaAhora() {
        List<RegistroJornada> marcas = List.of(marca(TipoMarcacion.ENTRADA, 8 * HORA));
        assertEquals(2 * HORA, JornadaReglas.horasTrabajadas(marcas, 10 * HORA));
    }

    @Test
    void unaSalidaSinEntradaNoSuma() {
        List<RegistroJornada> marcas = List.of(marca(TipoMarcacion.SALIDA, 9 * HORA));
        assertEquals(0, JornadaReglas.horasTrabajadas(marcas, 10 * HORA));
    }
}
