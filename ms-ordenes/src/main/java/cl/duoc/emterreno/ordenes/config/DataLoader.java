package cl.duoc.emterreno.ordenes.config;

import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.model.OrdenTrabajo;
import cl.duoc.emterreno.ordenes.model.Prioridad;
import cl.duoc.emterreno.ordenes.repository.OrdenTrabajoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final ZoneId ZONA_CHILE = ZoneId.of("America/Santiago");

    private static final List<String> TECNICOS = List.of(
            "EMP-333333333", "EMP-555555555", "EMP-666666666", "EMP-777777777",
            "EMP-888888888", "EMP-999999999", "EMP-101010104", "EMP-121212129",
            "EMP-131313136", "EMP-141414143", "EMP-151515150", "EMP-161616168"
    );

    private static final List<String> CLIENTES = List.of("SUC-01", "SUC-02", "SUC-03", "SUC-04", "SUC-05");

    private static final List<String[]> TRABAJOS = List.of(
            new String[]{"Mantención preventiva de tablero eléctrico general", "Inspección termográfica, reapriete de conexiones, limpieza de barras"},
            new String[]{"Reparación correctiva de motor de correa transportadora", "Diagnóstico de rodamientos, cambio de repuestos, prueba en vacío"},
            new String[]{"Revisión de sistema de iluminación de nave industrial", "Medición de luminarias, reemplazo de equipos defectuosos"},
            new String[]{"Mantención de compresor de aire", "Cambio de filtros y aceite, revisión de válvulas de seguridad"},
            new String[]{"Calibración de sensores de temperatura", "Contraste con patrón, ajuste de transmisores, registro de valores"}
    );

    private final OrdenTrabajoRepository repository;

    public DataLoader(OrdenTrabajoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        long hoy = LocalDate.now(ZONA_CHILE).atStartOfDay(ZONA_CHILE).toInstant().toEpochMilli();
        long manana = hoy + 24L * 60 * 60 * 1000;
        long ahora = System.currentTimeMillis();

        List<OrdenTrabajo> ordenes = new ArrayList<>();
        int contador = 1;
        for (int i = 0; i < TECNICOS.size(); i++) {
            int cantidad = (i == 0) ? 3 : 1;
            for (int j = 0; j < cantidad; j++) {
                String[] trabajo = TRABAJOS.get((i + j) % TRABAJOS.size());
                OrdenTrabajo orden = new OrdenTrabajo();
                orden.setId(String.format("OT-%03d", contador));
                orden.setTecnicoId(TECNICOS.get(i));
                orden.setSucursalId(CLIENTES.get((i + j) % CLIENTES.size()));
                orden.setFechaProgramada(j == 2 ? manana : hoy);
                orden.setEstado(EstadoServicio.PENDIENTE);
                orden.setDescripcion(trabajo[0]);
                orden.setActividadesRequeridas(trabajo[1]);
                orden.setPrioridad(j == 1 ? Prioridad.URGENTE : Prioridad.NORMAL);
                orden.setAprobada(false);
                orden.setCreadaEn(ahora + contador);
                orden.setActualizadoEn(ahora);
                ordenes.add(orden);
                contador++;
            }
        }
        repository.saveAll(ordenes);
        log.info("Se cargaron {} órdenes de trabajo de prueba", ordenes.size());
    }
}
