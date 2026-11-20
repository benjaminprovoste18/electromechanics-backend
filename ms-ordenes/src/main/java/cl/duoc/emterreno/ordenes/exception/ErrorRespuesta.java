package cl.duoc.emterreno.ordenes.exception;

import java.util.Map;

public record ErrorRespuesta(int estado, String mensaje, Map<String, String> errores, long fecha) {

    public static ErrorRespuesta de(int estado, String mensaje) {
        return new ErrorRespuesta(estado, mensaje, Map.of(), System.currentTimeMillis());
    }
}
