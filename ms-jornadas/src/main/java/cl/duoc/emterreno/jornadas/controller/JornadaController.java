package cl.duoc.emterreno.jornadas.controller;

import cl.duoc.emterreno.jornadas.dto.JornadaDto;
import cl.duoc.emterreno.jornadas.dto.ResumenHorasDto;
import cl.duoc.emterreno.jornadas.service.JornadaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final JornadaService service;

    public JornadaController(JornadaService service) {
        this.service = service;
    }

    @GetMapping
    public List<JornadaDto> listar(@RequestParam(required = false) String empleadoId) {
        return service.listar(empleadoId);
    }

    @GetMapping("/resumen")
    public List<ResumenHorasDto> resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return service.resumen(fecha != null ? fecha : LocalDate.now(ZoneId.of("America/Santiago")));
    }

    @GetMapping("/{id}")
    public JornadaDto buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<JornadaDto> crear(@Valid @RequestBody JornadaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public JornadaDto guardar(@PathVariable String id, @Valid @RequestBody JornadaDto dto) {
        return service.guardar(id, dto);
    }
}
