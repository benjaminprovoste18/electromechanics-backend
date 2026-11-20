package cl.duoc.emterreno.jornadas;

import cl.duoc.emterreno.jornadas.controller.JornadaController;
import cl.duoc.emterreno.jornadas.dto.JornadaDto;
import cl.duoc.emterreno.jornadas.dto.ResumenHorasDto;
import cl.duoc.emterreno.jornadas.model.TipoMarcacion;
import cl.duoc.emterreno.jornadas.service.JornadaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JornadaController.class)
class JornadaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JornadaService service;

    @Test
    void getPorEmpleadoDevuelveSusMarcaciones() throws Exception {
        when(service.listar("EMP-333333333")).thenReturn(List.of(new JornadaDto(
                "REG-1", "EMP-333333333", TipoMarcacion.ENTRADA, 1L, -33.51, -70.75, 10.0,
                null, "SUC-01", 20.0, true, false, false, 1L
        )));

        mockMvc.perform(get("/api/jornadas").param("empleadoId", "EMP-333333333"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"))
                .andExpect(jsonPath("$[0].dentroDeRadio").value(true));
    }

    @Test
    void resumenDelDiaDevuelveLasHorasPorEmpleado() throws Exception {
        when(service.resumen(LocalDate.of(2026, 11, 20))).thenReturn(List.of(
                new ResumenHorasDto("EMP-333333333", 8 * 3_600_000L, 2, 0, false)
        ));

        mockMvc.perform(get("/api/jornadas/resumen").param("fecha", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horasMillis").value(28_800_000))
                .andExpect(jsonPath("$[0].marcaciones").value(2));
    }

    @Test
    void postSinTipoNiCoordenadasDevuelve400() throws Exception {
        String json = """
                {"empleadoId":"EMP-333333333","fechaHora":1700000000000,"latitud":120.0,"precisionMetros":10}
                """;

        mockMvc.perform(post("/api/jornadas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.tipo").exists())
                .andExpect(jsonPath("$.errores.latitud").exists())
                .andExpect(jsonPath("$.errores.longitud").exists());
    }

    @Test
    void postValidoDevuelve201() throws Exception {
        when(service.crear(any(JornadaDto.class))).thenReturn(new JornadaDto(
                "REG-2", "EMP-333333333", TipoMarcacion.SALIDA, 1L, -33.51, -70.75, 10.0,
                null, "SUC-01", 20.0, true, false, false, 1L
        ));
        String json = """
                {"empleadoId":"EMP-333333333","tipo":"SALIDA","fechaHora":1700000000000,
                 "latitud":-33.51,"longitud":-70.75,"precisionMetros":10}
                """;

        mockMvc.perform(post("/api/jornadas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("REG-2"));
    }
}
