package cl.duoc.emterreno.ordenes.service;

import cl.duoc.emterreno.ordenes.dto.ActividadDto;
import cl.duoc.emterreno.ordenes.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.Actividad;
import cl.duoc.emterreno.ordenes.model.OrdenTrabajo;
import cl.duoc.emterreno.ordenes.repository.ActividadRepository;
import cl.duoc.emterreno.ordenes.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final OrdenTrabajoRepository ordenRepository;

    public ActividadService(ActividadRepository actividadRepository, OrdenTrabajoRepository ordenRepository) {
        this.actividadRepository = actividadRepository;
        this.ordenRepository = ordenRepository;
    }

    @Transactional(readOnly = true)
    public List<ActividadDto> listar(String ordenId) {
        List<Actividad> actividades = (ordenId == null || ordenId.isBlank())
                ? actividadRepository.findAllByOrderByHoraRegistroDesc()
                : actividadRepository.findByOrdenIdOrderByHoraRegistroDesc(ordenId);
        return actividades.stream().map(this::aDto).toList();
    }

    @Transactional
    public ActividadDto crear(ActividadDto dto) {
        String id = (dto.id() == null || dto.id().isBlank()) ? UUID.randomUUID().toString() : dto.id().trim();
        if (actividadRepository.existsById(id)) {
            throw new ReglaNegocioException("Ya existe una actividad con el id " + id);
        }
        return guardar(id, dto);
    }

    @Transactional
    public ActividadDto guardar(String id, ActividadDto dto) {
        OrdenTrabajo orden = ordenRepository.findById(dto.ordenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden de trabajo " + dto.ordenId()));
        if (!OrdenReglas.puedeRegistrarActividad(orden.getEstado())) {
            throw new ReglaNegocioException("Solo se registran actividades en órdenes en proceso o finalizadas");
        }
        Actividad actividad = actividadRepository.findById(id).orElseGet(Actividad::new);
        actividad.setId(id);
        actividad.setOrden(orden);
        actividad.setDetalle(dto.detalle().trim());
        actividad.setRepuestos(dto.repuestos() == null || dto.repuestos().isBlank() ? "Sin repuestos" : dto.repuestos().trim());
        actividad.setEstadoEquipo(dto.estadoEquipo());
        actividad.setHoraRegistro(dto.horaRegistro());
        actividad.setComentarios(dto.comentarios() == null ? "" : dto.comentarios().trim());
        actividad.setActualizadoEn(System.currentTimeMillis());
        return aDto(actividadRepository.save(actividad));
    }

    private ActividadDto aDto(Actividad a) {
        return new ActividadDto(
                a.getId(),
                a.getOrden().getId(),
                a.getDetalle(),
                a.getRepuestos(),
                a.getEstadoEquipo(),
                a.getHoraRegistro(),
                a.getComentarios(),
                a.getActualizadoEn()
        );
    }
}
