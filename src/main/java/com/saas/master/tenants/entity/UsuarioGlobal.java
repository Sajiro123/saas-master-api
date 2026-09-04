package com.saas.master.tenants.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios_globales")
public class UsuarioGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(name = "es_propietario")
    private Boolean esPropietario = false;

    @Column(name = "es_superadmin")
    private Boolean esSuperadmin = false;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "ultimo_inicio_sesion")
    private ZonedDateTime ultimoInicioSesion;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public UsuarioGlobal() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }
    public Boolean getEsPropietario() { return esPropietario; }
    public void setEsPropietario(Boolean esPropietario) { this.esPropietario = esPropietario; }
    public Boolean getEsSuperadmin() { return esSuperadmin; }
    public void setEsSuperadmin(Boolean esSuperadmin) { this.esSuperadmin = esSuperadmin; }
    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
    public ZonedDateTime getUltimoInicioSesion() { return ultimoInicioSesion; }
    public void setUltimoInicioSesion(ZonedDateTime ultimoInicioSesion) { this.ultimoInicioSesion = ultimoInicioSesion; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
