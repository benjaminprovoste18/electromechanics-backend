package cl.duoc.emterreno.ordenes.service;

import cl.duoc.emterreno.ordenes.dto.OrdenDto;
import cl.duoc.emterreno.ordenes.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.model.OrdenTrabajo;
import cl.duoc.emterreno.ordenes.repository.ActividadRepository;
import cl.duoc.emterreno.ordenes.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrdenService {

    private final OrdenTrabajoRepository ordenRepository;
    private final ActividadRepository actividadRepository;

    public OrdenService(OrdenTrabajoRepository ordenRepository, ActividadRepository actividadRepository) {
        this.ordenRepository = ordenRepository;
        this.actividadRepository = actividadRepository;
    }

    @Transactional(readOnly = true)
    public List<OrdenDto> listar(String tecnicoId, EstadoServicio estado) {
        List<OrdenTrabajo> ordenes = (tecnicoId == null || tecnicoId.isBlank())
                ? ordenRepository.findAllByOrderByFechaProgramadaDesc()
                : ordenRepository.findByTecnicoIdOrderByFechaProgramadaAsc(tecnicoId);
        return ordenes.stream()
                .filter(o -> estado == null || o.getEstado() == estado)
                .map(this::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenDto buscar(String id) {
        return aDto(obtener(id));
    }

    @Transactional
    public OrdenDto crear(OrdenDto dto) {
        String id = (dto.id() == null || dto.id().isBlank()) ? UUID.randomUUID().toString() : dto.id().trim();
        if (ordenRepository.existsById(id)) {
            throw new ReglaNegocioException("Ya existe una orden con el id " + id);
        }
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setId(id);
        orden.setEstado(EstadoServicio.PENDIENTE);
        orden.setAprobada(false);
        orden.setCreadaEn(dto.creadaEn() != null ? dto.creadaEn() : System.currentTimeMillis());
        copiarDatosBase(orden, dto);
        orden.setActualizadoEn(System.currentTimeMillis());
        return aDto(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenDto guardar(String id, OrdenDto dto) {
        Optional<OrdenTrabajo> existente = ordenRepository.findById(id);
        OrdenTrabajo orden = existente.orElseGet(OrdenTrabajo::new);

        EstadoServicio nuevoEstado = dto.estado() != null ? dto.estado() : EstadoServicio.PENDIENTE;
        boolean aprobada = Boolean.TRUE.equals(dto.aprobada());
        if (existente.isPresent()) {
            OrdenReglas.validarTransicion(orden.getEstado(), nuevoEstado);
            aprobada = aprobada || orden.isAprobada();
        }
        OrdenReglas.validarCoherencia(nuevoEstado, dto.horaInicio(), dto.horaTermino(), aprobada);

        orden.setId(id);
        copiarDatosBase(orden, dto);
        orden.setEstado(nuevoEstado);
        orden.setFotoInicial(dto.fotoInicial());
        orden.setFotoFinal(dto.fotoFinal());
        orden.setHoraInicio(dto.horaInicio());
        orden.setHoraTermino(dto.horaTermino());
        orden.setAprobada(aprobada);
        if (orden.getCreadaEn() == null) {
            orden.setCreadaEn(dto.creadaEn() != null ? dto.creadaEn() : System.currentTimeMillis());
        }
        orden.setActualizadoEn(System.currentTimeMillis());
        return aDto(ordenRepository.save(orden));
    }

    @Transactional
    public void eliminar(String id) {
        OrdenTrabajo orden = obtener(id);
        if (!OrdenReglas.puedeEliminarse(orden.getEstado())) {
            throw new ReglaNegocioException("Solo se pueden eliminar órdenes pendientes");
        }
        actividadRepository.deleteByOrdenId(id);
        ordenRepository.delete(orden);
    }

    OrdenTrabajo obtener(String id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden de trabajo " + id));
    }

    private void copiarDatosBase(OrdenTrabajo orden, OrdenDto dto) {
        orden.setTecnicoId(dto.tecnicoId().trim());
        orden.setSucursalId(dto.sucursalId().trim());
        orden.setFechaProgramada(dto.fechaProgramada());
        orden.setDescripcion(dto.descripcion().trim());
        orden.setActividadesRequeridas(dto.actividadesRequeridas().trim());
        orden.setPrioridad(dto.prioridad());
    }

    private OrdenDto aDto(OrdenTrabajo o) {
        return new OrdenDto(
                o.getId(),
                o.getTecnicoId(),
                o.getSucursalId(),
                o.getFechaProgramada(),
                o.getEstado(),
                o.getDescripcion(),
                o.getActividadesRequeridas(),
                o.getPrioridad(),
                o.getFotoInicial(),
                o.getFotoFinal(),
                o.getHoraInicio(),
                o.getHoraTermino(),
                o.isAprobada(),
                o.getCreadaEn(),
                o.getActualizadoEn()
        );
    }
}
