package cl.duoc.emterreno.sucursales.config;

import cl.duoc.emterreno.sucursales.model.Sucursal;
import cl.duoc.emterreno.sucursales.repository.SucursalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final SucursalRepository repository;

    public DataLoader(SucursalRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        List<Sucursal> sucursales = List.of(
                new Sucursal("SUC-01", "Cliente Demo A - Planta Maipú", "Calle Ficticia 100, Maipú", -33.5110, -70.7580),
                new Sucursal("SUC-02", "Cliente Demo B - Centro Logístico Pudahuel", "Av. Simulada 2200, Pudahuel", -33.4440, -70.7700),
                new Sucursal("SUC-03", "Cliente Demo C - Planta Quilicura", "Pasaje Prueba 45, Quilicura", -33.3600, -70.7300),
                new Sucursal("SUC-04", "Cliente Demo D - Bodega San Bernardo", "Camino Ejemplo 900, San Bernardo", -33.5920, -70.7000),
                new Sucursal("SUC-05", "Cliente Demo E - Industria Renca", "Calle Modelo 310, Renca", -33.4050, -70.7280),
                new Sucursal("SUC-06", "Oficina Técnica Electromechanics (demo)", "Av. Referencia 1500, Maipú", -33.4950, -70.7570)
        );
        repository.saveAll(sucursales);
        log.info("Se cargaron {} sucursales de prueba", sucursales.size());
    }
}
