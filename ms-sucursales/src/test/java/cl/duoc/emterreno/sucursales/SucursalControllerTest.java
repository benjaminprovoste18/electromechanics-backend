package cl.duoc.emterreno.sucursales;

import cl.duoc.emterreno.sucursales.controller.SucursalController;
import cl.duoc.emterreno.sucursales.dto.SucursalDto;
import cl.duoc.emterreno.sucursales.exception.RecursoNoEncontradoException;
import cl.duoc.emterreno.sucursales.service.SucursalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SucursalController.class)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalService service;

    @Test
    void getListaDevuelve200ConLasSucursales() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new SucursalDto("SUC-01", "Cliente Demo A", "Calle Ficticia 100, Maipú", -33.511, -70.758, 1L)
        ));

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SUC-01"))
                .andExpect(jsonPath("$[0].nombreCliente").value("Cliente Demo A"));
    }

    @Test
    void getPorIdInexistenteDevuelve404() throws Exception {
        when(service.buscar("NO-EXISTE")).thenThrow(new RecursoNoEncontradoException("No existe la sucursal NO-EXISTE"));

        mockMvc.perform(get("/api/sucursales/NO-EXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la sucursal NO-EXISTE"));
    }

    @Test
    void postValidoDevuelve201() throws Exception {
        when(service.crear(any(SucursalDto.class))).thenReturn(
                new SucursalDto("SUC-07", "Cliente Nuevo", "Av. Prueba 500, Renca", -33.4, -70.72, 1L)
        );
        String json = """
                {"nombreCliente":"Cliente Nuevo","direccion":"Av. Prueba 500, Renca","latitud":-33.4,"longitud":-70.72}
                """;

        mockMvc.perform(post("/api/sucursales").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SUC-07"));
    }

    @Test
    void postConDatosInvalidosDevuelve400ConErroresPorCampo() throws Exception {
        String json = """
                {"nombreCliente":"","direccion":"Av","latitud":10.0,"longitud":-70.72}
                """;

        mockMvc.perform(post("/api/sucursales").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombreCliente").exists())
                .andExpect(jsonPath("$.errores.direccion").exists())
                .andExpect(jsonPath("$.errores.latitud").exists());
    }
}
