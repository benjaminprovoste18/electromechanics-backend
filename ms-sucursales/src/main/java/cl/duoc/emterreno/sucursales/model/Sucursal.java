package cl.duoc.emterreno.sucursales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @Column(name = "id_sucursal", length = 40)
    private String id;

    @Column(name = "nombre_cliente", nullable = false, length = 80)
    private String nombreCliente;

    @Column(name = "direccion", nullable = false, length = 120)
    private String direccion;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "actualizado_en", nullable = false)
    private Long actualizadoEn;

    public Sucursal() {
    }

    public Sucursal(String id, String nombreCliente, String direccion, Double latitud, Double longitud) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.actualizadoEn = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Long getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Long actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
