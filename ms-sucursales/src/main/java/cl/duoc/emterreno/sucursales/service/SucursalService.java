package cl.duoc.emterreno.sucursales.service;

import cl.duoc.emterreno.sucursales.dto.SucursalDto;
import cl.duoc.emterreno.sucursales.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.sucursales.exception.ReglaNegocioException;
import cl.duoc.emterreno.sucursales.model.Sucursal;
import cl.duoc.emterreno.sucursales.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SucursalService {

    private final SucursalRepository repository;

    public SucursalService(SucursalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SucursalDto> listar() {
        return repository.findAllByOrderByNombreClienteAsc().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public SucursalDto buscar(String id) {
        return aDto(obtener(id));
    }

    @Transactional
    public SucursalDto crear(SucursalDto dto) {
        String id = (dto.id() == null || dto.id().isBlank()) ? UUID.randomUUID().toString() : dto.id().trim();
        if (repository.existsById(id)) {
            throw new ReglaNegocioException("Ya existe una sucursal con el id " + id);
        }
        return guardar(id, dto);
    }

    @Transactional
    public SucursalDto guardar(String id, SucursalDto dto) {
        String nombre = dto.nombreCliente().trim();
        if (repository.existsByNombreClienteIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra sucursal con el nombre " + nombre);
        }
        Sucursal sucursal = repository.findById(id).orElseGet(Sucursal::new);
        sucursal.setId(id);
        sucursal.setNombreCliente(nombre);
        sucursal.setDireccion(dto.direccion().trim());
        sucursal.setLatitud(dto.latitud());
        sucursal.setLongitud(dto.longitud());
        sucursal.setActualizadoEn(System.currentTimeMillis());
        return aDto(repository.save(sucursal));
    }

    @Transactional
    public void eliminar(String id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe la sucursal " + id);
        }
        repository.deleteById(id);
    }

    private Sucursal obtener(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sucursal " + id));
    }

    private SucursalDto aDto(Sucursal s) {
        return new SucursalDto(
                s.getId(),
                s.getNombreCliente(),
                s.getDireccion(),
                s.getLatitud(),
                s.getLongitud(),
                s.getActualizadoEn()
        );
    }
}
