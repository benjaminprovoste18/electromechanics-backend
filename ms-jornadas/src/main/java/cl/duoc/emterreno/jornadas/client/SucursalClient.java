package cl.duoc.emterreno.jornadas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class SucursalClient {

    private final RestClient restClient;

    public SucursalClient(RestClient.Builder builder, @Value("${servicios.sucursales.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);
        this.restClient = builder.baseUrl(baseUrl).requestFactory(factory).build();
    }

    public List<SucursalRef> listar() {
        SucursalRef[] respuesta = restClient.get()
                .uri("/api/sucursales")
                .retrieve()
                .body(SucursalRef[].class);
        return respuesta == null ? List.of() : Arrays.asList(respuesta);
    }
}
