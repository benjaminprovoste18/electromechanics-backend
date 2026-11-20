package cl.duoc.emterreno.ordenes.controller;

import cl.duoc.emterreno.ordenes.dto.ActividadDto;
import cl.duoc.emterreno.ordenes.dto.OrdenDto;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.service.ActividadService;
import cl.duoc.emterreno.ordenes.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final ActividadService actividadService;

    public OrdenController(OrdenService ordenService, ActividadService actividadService) {
        this.ordenService = ordenService;
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<OrdenDto> listar(
            @RequestParam(required = false) String tecnicoId,
            @RequestParam(required = false) EstadoServicio estado
    ) {
        return ordenService.listar(tecnicoId, estado);
    }

    @GetMapping("/{id}")
    public OrdenDto buscar(@PathVariable String id) {
        return ordenService.buscar(id);
    }

    @GetMapping("/{id}/actividades")
    public List<ActividadDto> actividades(@PathVariable String id) {
        ordenService.buscar(id);
        return actividadService.listar(id);
    }

    @PostMapping
    public ResponseEntity<OrdenDto> crear(@Valid @RequestBody OrdenDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(dto));
    }

    @PutMapping("/{id}")
    public OrdenDto guardar(@PathVariable String id, @Valid @RequestBody OrdenDto dto) {
        return ordenService.guardar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        ordenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
