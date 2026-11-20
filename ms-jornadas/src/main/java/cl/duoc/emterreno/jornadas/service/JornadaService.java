package cl.duoc.emterreno.jornadas.service;

import cl.duoc.emterreno.jornadas.client.SucursalClient;
import cl.duoc.emterreno.jornadas.client.SucursalRef;
import cl.duoc.emterreno.jornadas.dto.JornadaDto;
import cl.duoc.emterreno.jornadas.dto.ResumenHorasDto;
import cl.duoc.emterreno.jornadas.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.jornadas.exception.ReglaNegocioException;
import cl.duoc.emterreno.jornadas.model.RegistroJornada;
import cl.duoc.emterreno.jornadas.model.TipoMarcacion;
import cl.duoc.emterreno.jornadas.repository.RegistroJornadaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JornadaService {

    private static final Logger log = LoggerFactory.getLogger(JornadaService.class);
    private static final ZoneId ZONA_CHILE = ZoneId.of("America/Santiago");

    private final RegistroJornadaRepository repository;
    private final SucursalClient sucursalClient;

    public JornadaService(RegistroJornadaRepository repository, SucursalClient sucursalClient) {
        this.repository = repository;
        this.sucursalClient = sucursalClient;
    }

    @Transactional(readOnly = true)
    public List<JornadaDto> listar(String empleadoId) {
        List<RegistroJornada> registros = (empleadoId == null || empleadoId.isBlank())
                ? repository.findAllByOrderByFechaHoraDesc()
                : repository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId);
        return registros.stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public JornadaDto buscar(String id) {
        return repository.findById(id)
                .map(this::aDto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el registro de jornada " + id));
    }

    @Transactional
    public JornadaDto crear(JornadaDto dto) {
        String id = (dto.id() == null || dto.id().isBlank()) ? UUID.randomUUID().toString() : dto.id().trim();
        if (repository.existsById(id)) {
            throw new ReglaNegocioException("Ya existe un registro de jornada con el id " + id);
        }
        return guardar(id, dto);
    }

    @Transactional
    public JornadaDto guardar(String id, JornadaDto dto) {
        Optional<RegistroJornada> existente = repository.findById(id);
        RegistroJornada registro = existente.orElseGet(RegistroJornada::new);
        boolean aprobado = Boolean.TRUE.equals(dto.aprobado()) || existente.map(RegistroJornada::isAprobado).orElse(false);

        registro.setId(id);
        registro.setEmpleadoId(dto.empleadoId().trim());
        registro.setTipo(dto.tipo());
        registro.setFechaHora(dto.fechaHora());
        registro.setLatitud(dto.latitud());
        registro.setLongitud(dto.longitud());
        registro.setPrecisionMetros(dto.precisionMetros());
        registro.setFotoRespaldo(dto.fotoRespaldo());
        registro.setUbicacionSimulada(Boolean.TRUE.equals(dto.ubicacionSimulada()));
        registro.setAprobado(aprobado);
        calcularSucursalCercana(registro, dto);
        registro.setActualizadoEn(System.currentTimeMillis());
        return aDto(repository.save(registro));
    }

    @Transactional(readOnly = true)
    public List<ResumenHorasDto> resumen(LocalDate fecha) {
        long desde = fecha.atStartOfDay(ZONA_CHILE).toInstant().toEpochMilli();
        long hasta = fecha.plusDays(1).atStartOfDay(ZONA_CHILE).toInstant().toEpochMilli() - 1;
        long ahora = System.currentTimeMillis();
        Map<String, List<RegistroJornada>> porEmpleado = repository
                .findByFechaHoraBetweenOrderByFechaHoraAsc(desde, hasta)
                .stream()
                .collect(Collectors.groupingBy(RegistroJornada::getEmpleadoId));
        return porEmpleado.entrySet().stream()
                .map(e -> {
                    List<RegistroJornada> lista = e.getValue();
                    RegistroJornada ultima = lista.get(lista.size() - 1);
                    return new ResumenHorasDto(
                            e.getKey(),
                            JornadaReglas.horasTrabajadas(lista, Math.min(ahora, hasta)),
                            lista.size(),
                            (int) lista.stream().filter(r -> !r.isDentroDeRadio()).count(),
                            ultima.getTipo() == TipoMarcacion.ENTRADA
                    );
                })
                .sorted(Comparator.comparingLong(ResumenHorasDto::horasMillis).reversed())
                .toList();
    }

    private void calcularSucursalCercana(RegistroJornada registro, JornadaDto dto) {
        List<SucursalRef> sucursales;
        try {
            sucursales = sucursalClient.listar();
        } catch (RestClientException e) {
            log.warn("ms-sucursales no respondió, se usan los datos enviados por el dispositivo: {}", e.getMessage());
            sucursales = List.of();
        }

        Optional<SucursalRef> cercana = sucursales.stream()
                .filter(s -> s.latitud() != null && s.longitud() != null)
                .min(Comparator.comparingDouble(s ->
                        JornadaReglas.distanciaMetros(dto.latitud(), dto.longitud(), s.latitud(), s.longitud())));

        if (cercana.isPresent()) {
            SucursalRef s = cercana.get();
            double distancia = JornadaReglas.distanciaMetros(dto.latitud(), dto.longitud(), s.latitud(), s.longitud());
            registro.setSucursalCercanaId(s.id());
            registro.setDistanciaMetros(Math.round(distancia * 10) / 10.0);
            registro.setDentroDeRadio(JornadaReglas.dentroDeRadio(distancia));
        } else {
            registro.setSucursalCercanaId(dto.sucursalCercanaId());
            registro.setDistanciaMetros(dto.distanciaMetros());
            registro.setDentroDeRadio(dto.dentroDeRadio() != null
                    ? dto.dentroDeRadio()
                    : JornadaReglas.dentroDeRadio(dto.distanciaMetros()));
        }
    }

    private JornadaDto aDto(RegistroJornada r) {
        return new JornadaDto(
                r.getId(),
                r.getEmpleadoId(),
                r.getTipo(),
                r.getFechaHora(),
                r.getLatitud(),
                r.getLongitud(),
                r.getPrecisionMetros(),
                r.getFotoRespaldo(),
                r.getSucursalCercanaId(),
                r.getDistanciaMetros(),
                r.isDentroDeRadio(),
                r.isUbicacionSimulada(),
                r.isAprobado(),
                r.getActualizadoEn()
        );
    }
}
