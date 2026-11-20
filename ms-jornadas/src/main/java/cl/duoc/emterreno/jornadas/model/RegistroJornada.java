package cl.duoc.emterreno.jornadas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_jornada")
public class RegistroJornada {

    @Id
    @Column(name = "id_registro", length = 40)
    private String id;

    @Column(name = "id_empleado", nullable = false, length = 40)
    private String empleadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_marcacion", nullable = false, length = 10)
    private TipoMarcacion tipo;

    @Column(name = "fecha_hora", nullable = false)
    private Long fechaHora;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "precision_metros", nullable = false)
    private Double precisionMetros;

    @Column(name = "foto_respaldo", length = 300)
    private String fotoRespaldo;

    @Column(name = "id_sucursal_cercana", length = 40)
    private String sucursalCercanaId;

    @Column(name = "distancia_metros")
    private Double distanciaMetros;

    @Column(name = "dentro_de_radio", nullable = false)
    private boolean dentroDeRadio;

    @Column(name = "ubicacion_simulada", nullable = false)
    private boolean ubicacionSimulada;

    @Column(name = "aprobado", nullable = false)
    private boolean aprobado;

    @Column(name = "actualizado_en", nullable = false)
    private Long actualizadoEn;

    public RegistroJornada() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(String empleadoId) {
        this.empleadoId = empleadoId;
    }

    public TipoMarcacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoMarcacion tipo) {
        this.tipo = tipo;
    }

    public Long getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Long fechaHora) {
        this.fechaHora = fechaHora;
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

    public Double getPrecisionMetros() {
        return precisionMetros;
    }

    public void setPrecisionMetros(Double precisionMetros) {
        this.precisionMetros = precisionMetros;
    }

    public String getFotoRespaldo() {
        return fotoRespaldo;
    }

    public void setFotoRespaldo(String fotoRespaldo) {
        this.fotoRespaldo = fotoRespaldo;
    }

    public String getSucursalCercanaId() {
        return sucursalCercanaId;
    }

    public void setSucursalCercanaId(String sucursalCercanaId) {
        this.sucursalCercanaId = sucursalCercanaId;
    }

    public Double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(Double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public boolean isDentroDeRadio() {
        return dentroDeRadio;
    }

    public void setDentroDeRadio(boolean dentroDeRadio) {
        this.dentroDeRadio = dentroDeRadio;
    }

    public boolean isUbicacionSimulada() {
        return ubicacionSimulada;
    }

    public void setUbicacionSimulada(boolean ubicacionSimulada) {
        this.ubicacionSimulada = ubicacionSimulada;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Long getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Long actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
