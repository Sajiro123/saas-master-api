package com.saas.master.tenants.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "credenciales_bd_negocio")
public class CredencialBdNegocio {

    @Id
    @Column(name = "negocio_id", nullable = false)
    private UUID negocioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;

    @Column(name = "host_bd", nullable = false)
    private String hostBd;

    @Column(name = "puerto_bd")
    private Integer puertoBd = 5432;

    @Column(name = "nombre_bd", nullable = false, length = 100)
    private String nombreBd = "postgres";

    @Column(name = "usuario_bd", nullable = false, length = 100)
    private String usuarioBd = "postgres";

    @Column(name = "password_bd_cifrado", nullable = false, columnDefinition = "TEXT")
    private String passwordBdCifrado;

    @Column(name = "modo_ssl", length = 20)
    private String modoSsl = "require";

    @Column(name = "pool_min_conexiones")
    private Integer poolMinConexiones = 2;

    @Column(name = "pool_max_conexiones")
    private Integer poolMaxConexiones = 10;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    @Column(name = "actualizado_en")
    private ZonedDateTime actualizadoEn = ZonedDateTime.now();

    public CredencialBdNegocio() {}

    public UUID getNegocioId() { return negocioId; }
    public void setNegocioId(UUID negocioId) { this.negocioId = negocioId; }
    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }
    public String getHostBd() { return hostBd; }
    public void setHostBd(String hostBd) { this.hostBd = hostBd; }
    public Integer getPuertoBd() { return puertoBd; }
    public void setPuertoBd(Integer puertoBd) { this.puertoBd = puertoBd; }
    public String getNombreBd() { return nombreBd; }
    public void setNombreBd(String nombreBd) { this.nombreBd = nombreBd; }
    public String getUsuarioBd() { return usuarioBd; }
    public void setUsuarioBd(String usuarioBd) { this.usuarioBd = usuarioBd; }
    public String getPasswordBdCifrado() { return passwordBdCifrado; }
    public void setPasswordBdCifrado(String passwordBdCifrado) { this.passwordBdCifrado = passwordBdCifrado; }
    public String getModoSsl() { return modoSsl; }
    public void setModoSsl(String modoSsl) { this.modoSsl = modoSsl; }
    public Integer getPoolMinConexiones() { return poolMinConexiones; }
    public void setPoolMinConexiones(Integer poolMinConexiones) { this.poolMinConexiones = poolMinConexiones; }
    public Integer getPoolMaxConexiones() { return poolMaxConexiones; }
    public void setPoolMaxConexiones(Integer poolMaxConexiones) { this.poolMaxConexiones = poolMaxConexiones; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
    public ZonedDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(ZonedDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
