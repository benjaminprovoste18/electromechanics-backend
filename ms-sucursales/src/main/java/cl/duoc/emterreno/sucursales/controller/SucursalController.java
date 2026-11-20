package cl.duoc.emterreno.sucursales.controller;

import cl.duoc.emterreno.sucursales.dto.SucursalDto;
import cl.duoc.emterreno.sucursales.service.SucursalService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final SucursalService service;

    public SucursalController(SucursalService service) {
        this.service = service;
    }

    @GetMapping
    public List<SucursalDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public SucursalDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<SucursalDto> crear(@Valid @RequestBody SucursalDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public SucursalDto guardar(@PathVariable String id, @Valid @RequestBody SucursalDto dto) {
        return service.guardar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
