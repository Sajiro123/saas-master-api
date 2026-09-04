package com.saas.master.tenants.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTenantRequest {

    @NotBlank(message = "El subdominio es obligatorio")
    @Size(min = 3, max = 63)
    private String subdominio;

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    private String nombreComercial;

    @NotBlank(message = "El número de identificación (RUC) es obligatorio")
    private String numeroIdentificacion;

    @NotBlank(message = "La vertical es obligatoria ('FARMACIA', 'RETAIL', 'RESTAURANTE')")
    private String verticalId;

    @NotBlank(message = "El plan es obligatorio")
    private String planId;

    @NotBlank(message = "El email de contacto es obligatorio")
    @Email
    private String emailContacto;

    private String telefonoContacto;

    // Datos iniciales de la base de datos Supabase del cliente
    private String dbHost;
    private Integer dbPort = 5432;
    private String dbPassword;

    public CreateTenantRequest() {}

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
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
    public String getDbHost() { return dbHost; }
    public void setDbHost(String dbHost) { this.dbHost = dbHost; }
    public Integer getDbPort() { return dbPort; }
    public void setDbPort(Integer dbPort) { this.dbPort = dbPort; }
    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
}
