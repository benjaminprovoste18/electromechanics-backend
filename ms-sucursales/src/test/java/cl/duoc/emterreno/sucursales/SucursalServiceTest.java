package cl.duoc.emterreno.sucursales;

import cl.duoc.emterreno.sucursales.dto.SucursalDto;
import cl.duoc.emterreno.sucursales.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.sucursales.exception.ReglaNegocioException;
import cl.duoc.emterreno.sucursales.model.Sucursal;
import cl.duoc.emterreno.sucursales.repository.SucursalRepository;
import cl.duoc.emterreno.sucursales.service.SucursalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository repository;

    @InjectMocks
    private SucursalService service;

    private SucursalDto dtoValido(String id) {
        return new SucursalDto(id, "Cliente Demo Z", "Calle Prueba 123, Maipú", -33.51, -70.75, null);
    }

    @Test
    void listarDevuelveLasSucursalesComoDto() {
        when(repository.findAllByOrderByNombreClienteAsc()).thenReturn(List.of(
                new Sucursal("SUC-01", "Cliente A", "Calle 1, Maipú", -33.5, -70.7),
                new Sucursal("SUC-02", "Cliente B", "Calle 2, Renca", -33.4, -70.7)
        ));

        List<SucursalDto> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("SUC-01", resultado.get(0).id());
        assertEquals("Cliente B", resultado.get(1).nombreCliente());
    }

    @Test
    void crearSinIdGeneraUnIdNuevo() {
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.existsByNombreClienteIgnoreCaseAndIdNot(anyString(), anyString())).thenReturn(false);
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Sucursal.class))).thenAnswer(inv -> inv.getArgument(0));

        SucursalDto creada = service.crear(dtoValido(null));

        assertNotNull(creada.id());
        assertFalse(creada.id().isBlank());
        assertNotNull(creada.actualizadoEn());
    }

    @Test
    void crearConIdExistenteLanzaReglaDeNegocio() {
        when(repository.existsById("SUC-01")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(dtoValido("SUC-01")));
        verify(repository, never()).save(any());
    }

    @Test
    void noPermiteDosSucursalesConElMismoNombre() {
        when(repository.existsByNombreClienteIgnoreCaseAndIdNot("Cliente Demo Z", "SUC-09")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.guardar("SUC-09", dtoValido("SUC-09")));
        verify(repository, never()).save(any());
    }

    @Test
    void guardarActualizaLaSucursalExistente() {
        Sucursal existente = new Sucursal("SUC-01", "Nombre antiguo", "Dirección antigua 1", -33.0, -70.0);
        when(repository.existsByNombreClienteIgnoreCaseAndIdNot(anyString(), anyString())).thenReturn(false);
        when(repository.findById("SUC-01")).thenReturn(Optional.of(existente));
        when(repository.save(any(Sucursal.class))).thenAnswer(inv -> inv.getArgument(0));

        SucursalDto resultado = service.guardar("SUC-01", dtoValido("SUC-01"));

        assertEquals("Cliente Demo Z", resultado.nombreCliente());
        assertEquals(-33.51, resultado.latitud());
    }

    @Test
    void buscarUnaSucursalInexistenteLanzaNoEncontrado() {
        when(repository.findById("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscar("NO-EXISTE"));
    }

    @Test
    void eliminarUnaSucursalInexistenteLanzaNoEncontrado() {
        when(repository.existsById("NO-EXISTE")).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar("NO-EXISTE"));
        verify(repository, never()).deleteById(anyString());
    }
}
