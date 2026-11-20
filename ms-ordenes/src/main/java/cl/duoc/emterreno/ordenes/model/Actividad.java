package cl.duoc.emterreno.ordenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_actividad")
public class Actividad {

    @Id
    @Column(name = "id_actividad", length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private OrdenTrabajo orden;

    @Column(name = "detalle", nullable = false, length = 500)
    private String detalle;

    @Column(name = "repuestos", length = 300)
    private String repuestos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_equipo", nullable = false, length = 30)
    private EstadoEquipo estadoEquipo;

    @Column(name = "hora_registro", nullable = false)
    private Long horaRegistro;

    @Column(name = "comentarios", length = 500)
    private String comentarios;

    @Column(name = "actualizado_en", nullable = false)
    private Long actualizadoEn;

    public Actividad() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrdenTrabajo getOrden() {
        return orden;
    }

    public void setOrden(OrdenTrabajo orden) {
        this.orden = orden;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(String repuestos) {
        this.repuestos = repuestos;
    }

    public EstadoEquipo getEstadoEquipo() {
        return estadoEquipo;
    }

    public void setEstadoEquipo(EstadoEquipo estadoEquipo) {
        this.estadoEquipo = estadoEquipo;
    }

    public Long getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(Long horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Long getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Long actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
