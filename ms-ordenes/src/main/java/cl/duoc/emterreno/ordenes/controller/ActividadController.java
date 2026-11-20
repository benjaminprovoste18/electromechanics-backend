package cl.duoc.emterreno.ordenes.controller;

import cl.duoc.emterreno.ordenes.dto.ActividadDto;
import cl.duoc.emterreno.ordenes.service.ActividadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService service;

    public ActividadController(ActividadService service) {
        this.service = service;
    }

    @GetMapping
    public List<ActividadDto> listar(@RequestParam(required = false) String ordenId) {
        return service.listar(ordenId);
    }

    @PostMapping
    public ResponseEntity<ActividadDto> crear(@Valid @RequestBody ActividadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ActividadDto guardar(@PathVariable String id, @Valid @RequestBody ActividadDto dto) {
        return service.guardar(id, dto);
    }
}
