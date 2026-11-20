package cl.duoc.emterreno.jornadas;

import cl.duoc.emterreno.jornadas.client.SucursalClient;
import cl.duoc.emterreno.jornadas.client.SucursalRef;
import cl.duoc.emterreno.jornadas.dto.JornadaDto;
import cl.duoc.emterreno.jornadas.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.jornadas.model.RegistroJornada;
import cl.duoc.emterreno.jornadas.model.TipoMarcacion;
import cl.duoc.emterreno.jornadas.repository.RegistroJornadaRepository;
import cl.duoc.emterreno.jornadas.service.JornadaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JornadaServiceTest {

    @Mock
    private RegistroJornadaRepository repository;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private JornadaService service;

    private JornadaDto marcacion(double lat, double lon, Boolean dentroSegunDispositivo, Boolean aprobado) {
        return new JornadaDto(
                "REG-1", "EMP-333333333", TipoMarcacion.ENTRADA, 1_700_000_000_000L,
                lat, lon, 12.0, null, "SUC-99", 5.0, dentroSegunDispositivo, false, aprobado, null
        );
    }

    @Test
    void elServidorRecalculaLaSucursalMasCercana() {
        when(repository.findById("REG-1")).thenReturn(Optional.empty());
        when(repository.save(any(RegistroJornada.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sucursalClient.listar()).thenReturn(List.of(
                new SucursalRef("SUC-01", "Planta Maipú", -33.5110, -70.7580),
                new SucursalRef("SUC-03", "Planta Quilicura", -33.3600, -70.7300)
        ));

        JornadaDto resultado = service.guardar("REG-1", marcacion(-33.5112, -70.7581, true, false));

        assertEquals("SUC-01", resultado.sucursalCercanaId());
        assertTrue(resultado.dentroDeRadio());
        assertTrue(resultado.distanciaMetros() < 50);
    }

    @Test
    void siLaMarcacionEstaLejosQuedaFueraDeRadioAunqueElTelefonoDigaLoContrario() {
        when(repository.findById("REG-1")).thenReturn(Optional.empty());
        when(repository.save(any(RegistroJornada.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sucursalClient.listar()).thenReturn(List.of(new SucursalRef("SUC-01", "Planta Maipú", -33.5110, -70.7580)));

        JornadaDto resultado = service.guardar("REG-1", marcacion(-33.4500, -70.6500, true, false));

        assertFalse(resultado.dentroDeRadio());
    }

    @Test
    void siMsSucursalesNoRespondeSeUsanLosDatosDelDispositivo() {
        when(repository.findById("REG-1")).thenReturn(Optional.empty());
        when(repository.save(any(RegistroJornada.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sucursalClient.listar()).thenThrow(new ResourceAccessException("Connection refused"));

        JornadaDto resultado = service.guardar("REG-1", marcacion(-33.5112, -70.7581, true, false));

        assertEquals("SUC-99", resultado.sucursalCercanaId());
        assertTrue(resultado.dentroDeRadio());
    }

    @Test
    void laAprobacionDelSupervisorNoSePierde() {
        RegistroJornada existente = new RegistroJornada();
        existente.setId("REG-1");
        existente.setAprobado(true);
        when(repository.findById("REG-1")).thenReturn(Optional.of(existente));
        when(repository.save(any(RegistroJornada.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sucursalClient.listar()).thenReturn(List.of());

        JornadaDto resultado = service.guardar("REG-1", marcacion(-33.5112, -70.7581, true, false));

        assertTrue(resultado.aprobado());
    }

    @Test
    void buscarUnRegistroInexistenteLanzaNoEncontrado() {
        when(repository.findById("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscar("NO-EXISTE"));
    }
}
