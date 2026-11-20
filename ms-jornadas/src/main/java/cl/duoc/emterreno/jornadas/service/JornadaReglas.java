package cl.duoc.emterreno.jornadas.service;

import cl.duoc.emterreno.jornadas.model.RegistroJornada;
import cl.duoc.emterreno.jornadas.model.TipoMarcacion;

import java.util.List;

public final class JornadaReglas {

    public static final double RADIO_VALIDO_METROS = 300.0;
    private static final double RADIO_TIERRA_METROS = 6_371_000.0;

    private JornadaReglas() {
    }

    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_METROS * c;
    }

    public static boolean dentroDeRadio(Double distanciaMetros) {
        return distanciaMetros != null && distanciaMetros <= RADIO_VALIDO_METROS;
    }

    public static long horasTrabajadas(List<RegistroJornada> ordenadosPorHora, long ahora) {
        long total = 0;
        Long entradaAbierta = null;
        for (RegistroJornada r : ordenadosPorHora) {
            if (r.getTipo() == TipoMarcacion.ENTRADA) {
                if (entradaAbierta == null) {
                    entradaAbierta = r.getFechaHora();
                }
            } else if (entradaAbierta != null) {
                total += Math.max(0, r.getFechaHora() - entradaAbierta);
                entradaAbierta = null;
            }
        }
        if (entradaAbierta != null) {
            total += Math.max(0, ahora - entradaAbierta);
        }
        return total;
    }
}
