package cl.duoc.emterreno.ordenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orden_trabajo")
public class OrdenTrabajo {

    @Id
    @Column(name = "id_servicio", length = 40)
    private String id;

    @Column(name = "id_tecnico", nullable = false, length = 40)
    private String tecnicoId;

    @Column(name = "id_sucursal", nullable = false, length = 40)
    private String sucursalId;

    @Column(name = "fecha_programada", nullable = false)
    private Long fechaProgramada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoServicio estado;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "actividades_requeridas", nullable = false, length = 1000)
    private String actividadesRequeridas;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 20)
    private Prioridad prioridad;

    @Column(name = "foto_inicial", length = 300)
    private String fotoInicial;

    @Column(name = "foto_final", length = 300)
    private String fotoFinal;

    @Column(name = "hora_inicio")
    private Long horaInicio;

    @Column(name = "hora_termino")
    private Long horaTermino;

    @Column(name = "aprobada", nullable = false)
    private boolean aprobada;

    @Column(name = "creada_en", nullable = false)
    private Long creadaEn;

    @Column(name = "actualizado_en", nullable = false)
    private Long actualizadoEn;

    public OrdenTrabajo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(String tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(String sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Long fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActividadesRequeridas() {
        return actividadesRequeridas;
    }

    public void setActividadesRequeridas(String actividadesRequeridas) {
        this.actividadesRequeridas = actividadesRequeridas;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public String getFotoInicial() {
        return fotoInicial;
    }

    public void setFotoInicial(String fotoInicial) {
        this.fotoInicial = fotoInicial;
    }

    public String getFotoFinal() {
        return fotoFinal;
    }

    public void setFotoFinal(String fotoFinal) {
        this.fotoFinal = fotoFinal;
    }

    public Long getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Long horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Long getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(Long horaTermino) {
        this.horaTermino = horaTermino;
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public void setAprobada(boolean aprobada) {
        this.aprobada = aprobada;
    }

    public Long getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(Long creadaEn) {
        this.creadaEn = creadaEn;
    }

    public Long getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Long actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
