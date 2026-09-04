package com.saas.master.tenants.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_eventos")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_global_id")
    private UsuarioGlobal usuarioGlobal;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalles_json", columnDefinition = "jsonb")
    private String detallesJson;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public AuditoriaEvento() {}

    public AuditoriaEvento(Negocio negocio, UsuarioGlobal usuarioGlobal, String tipoEvento, String descripcion, String detallesJson, String direccionIp) {
        this.negocio = negocio;
        this.usuarioGlobal = usuarioGlobal;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.detallesJson = detallesJson;
        this.direccionIp = direccionIp;
        this.creadoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }
    public UsuarioGlobal getUsuarioGlobal() { return usuarioGlobal; }
    public void setUsuarioGlobal(UsuarioGlobal usuarioGlobal) { this.usuarioGlobal = usuarioGlobal; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDetallesJson() { return detallesJson; }
    public void setDetallesJson(String detallesJson) { this.detallesJson = detallesJson; }
    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
