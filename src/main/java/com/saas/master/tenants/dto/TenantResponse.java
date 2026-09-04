package com.saas.master.tenants.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TenantResponse {
    private UUID id;
    private String subdominio;
    private String razonSocial;
    private String nombreComercial;
    private String numeroIdentificacion;
    private String verticalId;
    private String verticalNombre;
    private String planId;
    private String planNombre;
    private String estado;
    private String emailContacto;
    private String telefonoContacto;
    private String dbHost;
    private ZonedDateTime creadoEn;

    public TenantResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSubdominio() { return subdominio; }
    public void setSubdominio(String subdominio) { this.subdominio = subdominio; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }
    public String getVerticalId() { return verticalId; }
    public void setVerticalId(String verticalId) { this.verticalId = verticalId; }
    public String getVerticalNombre() { return verticalNombre; }
    public void setVerticalNombre(String verticalNombre) { this.verticalNombre = verticalNombre; }
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
    public String getDbHost() { return dbHost; }
    public void setDbHost(String dbHost) { this.dbHost = dbHost; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
