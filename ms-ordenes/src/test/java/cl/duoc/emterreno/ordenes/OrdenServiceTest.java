package cl.duoc.emterreno.ordenes;

import cl.duoc.emterreno.ordenes.dto.ActividadDto;
import cl.duoc.emterreno.ordenes.dto.OrdenDto;
import cl.duoc.emterreno.ordenes.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.Actividad;
import cl.duoc.emterreno.ordenes.model.EstadoEquipo;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.model.OrdenTrabajo;
import cl.duoc.emterreno.ordenes.model.Prioridad;
import cl.duoc.emterreno.ordenes.repository.ActividadRepository;
import cl.duoc.emterreno.ordenes.repository.OrdenTrabajoRepository;
import cl.duoc.emterreno.ordenes.service.ActividadService;
import cl.duoc.emterreno.ordenes.service.OrdenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenTrabajoRepository ordenRepository;

    @Mock
    private ActividadRepository actividadRepository;

    private OrdenService ordenService;
    private ActividadService actividadService;

    @BeforeEach
    void setUp() {
        ordenService = new OrdenService(ordenRepository, actividadRepository);
        actividadService = new ActividadService(actividadRepository, ordenRepository);
    }

    private OrdenDto dto(EstadoServicio estado, Long inicio, Long termino, Boolean aprobada) {
        return new OrdenDto(
                "OT-100", "EMP-333333333", "SUC-01", 1_700_000_000_000L, estado,
                "Mantención preventiva de tablero eléctrico", "Reapriete de conexiones y limpieza",
                Prioridad.NORMAL, null, null, inicio, termino, aprobada, null, null
        );
    }

    private OrdenTrabajo ordenExistente(EstadoServicio estado, boolean aprobada) {
        OrdenTrabajo o = new OrdenTrabajo();
        o.setId("OT-100");
        o.setTecnicoId("EMP-333333333");
        o.setSucursalId("SUC-01");
        o.setFechaProgramada(1_700_000_000_000L);
        o.setEstado(estado);
        o.setDescripcion("Mantención preventiva de tablero eléctrico");
        o.setActividadesRequeridas("Reapriete de conexiones y limpieza");
        o.setPrioridad(Prioridad.NORMAL);
        o.setAprobada(aprobada);
        o.setCreadaEn(1L);
        return o;
    }

    @Test
    void crearSiempreDejaLaOrdenPendienteYSinAprobar() {
        when(ordenRepository.existsById(anyString())).thenReturn(false);
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenDto creada = ordenService.crear(dto(EstadoServicio.FINALIZADO, 1L, 2L, true));

        assertEquals(EstadoServicio.PENDIENTE, creada.estado());
        assertEquals(Boolean.FALSE, creada.aprobada());
        assertNotNull(creada.creadaEn());
    }

    @Test
    void guardarNoPermiteRetrocederUnaOrdenFinalizada() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.of(ordenExistente(EstadoServicio.FINALIZADO, false)));

        assertThrows(ReglaNegocioException.class,
                () -> ordenService.guardar("OT-100", dto(EstadoServicio.PENDIENTE, null, null, false)));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void guardarConservaLaAprobacionDelSupervisor() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.of(ordenExistente(EstadoServicio.FINALIZADO, true)));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenDto resultado = ordenService.guardar("OT-100", dto(EstadoServicio.FINALIZADO, 1000L, 2000L, false));

        assertTrue(resultado.aprobada());
    }

    @Test
    void guardarCreaLaOrdenSiNoExiste() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.empty());
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenDto resultado = ordenService.guardar("OT-100", dto(EstadoServicio.EN_PROCESO, 1000L, null, false));

        assertEquals("OT-100", resultado.id());
        assertEquals(EstadoServicio.EN_PROCESO, resultado.estado());
    }

    @Test
    void noSeEliminaUnaOrdenEnProceso() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.of(ordenExistente(EstadoServicio.EN_PROCESO, false)));

        assertThrows(ReglaNegocioException.class, () -> ordenService.eliminar("OT-100"));
        verify(ordenRepository, never()).delete(any());
    }

    @Test
    void listarFiltraPorTecnicoYEstado() {
        OrdenTrabajo pendiente = ordenExistente(EstadoServicio.PENDIENTE, false);
        OrdenTrabajo finalizada = ordenExistente(EstadoServicio.FINALIZADO, false);
        finalizada.setId("OT-101");
        when(ordenRepository.findByTecnicoIdOrderByFechaProgramadaAsc("EMP-333333333"))
                .thenReturn(List.of(pendiente, finalizada));

        List<OrdenDto> resultado = ordenService.listar("EMP-333333333", EstadoServicio.FINALIZADO);

        assertEquals(1, resultado.size());
        assertEquals("OT-101", resultado.get(0).id());
    }

    @Test
    void noSeRegistraActividadEnUnaOrdenPendiente() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.of(ordenExistente(EstadoServicio.PENDIENTE, false)));
        ActividadDto actividad = new ActividadDto(
                "ACT-1", "OT-100", "Cambio de rodamientos del motor", null,
                EstadoEquipo.OPERATIVO, 1_700_000_100_000L, "Sin novedades", null
        );

        assertThrows(ReglaNegocioException.class, () -> actividadService.guardar("ACT-1", actividad));
        verify(actividadRepository, never()).save(any());
    }

    @Test
    void registrarActividadEnOrdenInexistenteLanzaNoEncontrado() {
        when(ordenRepository.findById("OT-999")).thenReturn(Optional.empty());
        ActividadDto actividad = new ActividadDto(
                "ACT-1", "OT-999", "Cambio de rodamientos del motor", null,
                EstadoEquipo.OPERATIVO, 1_700_000_100_000L, "", null
        );

        assertThrows(RecursoNoEncontradoException.class, () -> actividadService.guardar("ACT-1", actividad));
    }

    @Test
    void actividadSinRepuestosQuedaConTextoPorDefecto() {
        when(ordenRepository.findById("OT-100")).thenReturn(Optional.of(ordenExistente(EstadoServicio.EN_PROCESO, false)));
        when(actividadRepository.findById("ACT-1")).thenReturn(Optional.empty());
        when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));
        ActividadDto actividad = new ActividadDto(
                "ACT-1", "OT-100", "Cambio de rodamientos del motor", "  ",
                EstadoEquipo.CON_OBSERVACIONES, 1_700_000_100_000L, null, null
        );

        ActividadDto guardada = actividadService.guardar("ACT-1", actividad);

        assertEquals("Sin repuestos", guardada.repuestos());
        assertEquals("OT-100", guardada.ordenId());
    }
}
