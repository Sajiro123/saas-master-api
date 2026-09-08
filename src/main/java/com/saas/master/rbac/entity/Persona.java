package com.saas.master.rbac.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento = "DNI";

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "nro_colegiatura")
    private String nroColegiatura; // CQFP o CMP

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negocio_id")
    private com.saas.master.tenants.entity.Negocio negocio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;

    @Column(name = "fechanacimiento")
    private java.time.LocalDate fechanacimiento;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    @Column(name = "actualizado_en")
    private ZonedDateTime actualizadoEn = ZonedDateTime.now();

    public Persona() {}

    public com.saas.master.tenants.entity.Negocio getNegocio() { return negocio; }
    public void setNegocio(com.saas.master.tenants.entity.Negocio negocio) { this.negocio = negocio; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public java.time.LocalDate getFechanacimiento() { return fechanacimiento; }
    public void setFechanacimiento(java.time.LocalDate fechanacimiento) { this.fechanacimiento = fechanacimiento; }
    public Boolean getEstaActivo() { return estaActivo != null ? estaActivo : true; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getNroColegiatura() { return nroColegiatura; }
    public void setNroColegiatura(String nroColegiatura) { this.nroColegiatura = nroColegiatura; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
    public ZonedDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(ZonedDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
