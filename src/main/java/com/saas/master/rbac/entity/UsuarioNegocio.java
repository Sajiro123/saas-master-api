package com.saas.master.rbac.entity;

import com.saas.master.tenants.entity.Negocio;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios_negocios")
public class UsuarioNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "pin_seguridad")
    private String pinSeguridad;

    @Column(name = "es_master")
    private Boolean esMaster = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "ultimo_acceso")
    private ZonedDateTime ultimoAcceso;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    @Column(name = "actualizado_en")
    private ZonedDateTime actualizadoEn = ZonedDateTime.now();

    public UsuarioNegocio() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPinSeguridad() { return pinSeguridad; }
    public void setPinSeguridad(String pinSeguridad) { this.pinSeguridad = pinSeguridad; }
    public Boolean getEsMaster() { return esMaster != null ? esMaster : false; }
    public void setEsMaster(Boolean esMaster) { this.esMaster = esMaster; }
    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
    public ZonedDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(ZonedDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
    public ZonedDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(ZonedDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
