package cl.duoc.emterreno.ordenes;

import cl.duoc.emterreno.ordenes.controller.OrdenController;
import cl.duoc.emterreno.ordenes.dto.OrdenDto;
import cl.duoc.emterreno.ordenes.exception.ReglaNegocioException;
import cl.duoc.emterreno.ordenes.model.EstadoServicio;
import cl.duoc.emterreno.ordenes.model.Prioridad;
import cl.duoc.emterreno.ordenes.service.ActividadService;
import cl.duoc.emterreno.ordenes.service.OrdenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdenController.class)
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdenService ordenService;

    @MockitoBean
    private ActividadService actividadService;

    private OrdenDto orden(String id, EstadoServicio estado) {
        return new OrdenDto(
                id, "EMP-333333333", "SUC-01", 1_700_000_000_000L, estado,
                "Mantención preventiva de tablero eléctrico", "Reapriete de conexiones y limpieza",
                Prioridad.URGENTE, null, null, null, null, false, 1L, 1L
        );
    }

    @Test
    void getFiltraPorTecnico() throws Exception {
        when(ordenService.listar("EMP-333333333", null)).thenReturn(List.of(orden("OT-001", EstadoServicio.PENDIENTE)));

        mockMvc.perform(get("/api/ordenes").param("tecnicoId", "EMP-333333333"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("OT-001"))
                .andExpect(jsonPath("$[0].prioridad").value("URGENTE"));
    }

    @Test
    void postValidoDevuelve201() throws Exception {
        when(ordenService.crear(any(OrdenDto.class))).thenReturn(orden("OT-050", EstadoServicio.PENDIENTE));
        String json = """
                {"tecnicoId":"EMP-333333333","sucursalId":"SUC-01","fechaProgramada":1700000000000,
                 "descripcion":"Mantención preventiva de tablero eléctrico",
                 "actividadesRequeridas":"Reapriete de conexiones y limpieza","prioridad":"NORMAL"}
                """;

        mockMvc.perform(post("/api/ordenes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void postConDescripcionCortaDevuelve400() throws Exception {
        String json = """
                {"tecnicoId":"EMP-333333333","sucursalId":"SUC-01","fechaProgramada":1700000000000,
                 "descripcion":"Corta","actividadesRequeridas":"Reapriete de conexiones","prioridad":"NORMAL"}
                """;

        mockMvc.perform(post("/api/ordenes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.descripcion").exists());
    }

    @Test
    void putQueRompeUnaReglaDevuelve409() throws Exception {
        when(ordenService.guardar(eq("OT-001"), any(OrdenDto.class)))
                .thenThrow(new ReglaNegocioException("Una orden FINALIZADO no puede volver al estado PENDIENTE"));
        String json = """
                {"tecnicoId":"EMP-333333333","sucursalId":"SUC-01","fechaProgramada":1700000000000,"estado":"PENDIENTE",
                 "descripcion":"Mantención preventiva de tablero eléctrico",
                 "actividadesRequeridas":"Reapriete de conexiones y limpieza","prioridad":"NORMAL"}
                """;

        mockMvc.perform(put("/api/ordenes/OT-001").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Una orden FINALIZADO no puede volver al estado PENDIENTE"));
    }
}
