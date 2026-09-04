package com.saas.master.auth.dto;

import java.util.List;
import java.util.UUID;

public class AuthResponse {
    private String token;
    private String tipoToken = "Bearer";
    private UUID usuarioId;
    private String email;
    private UUID tenantId;
    private String subdominio;
    private String nombreComercial;
    private String verticalId;
    private String planId;
    private boolean esPropietario;
    private boolean esSuperadmin;
    private String dbHost;

    // RBAC and Personal Info
    private String rolCodigo;
    private String rolNombre;
    private String nombreCompleto;
    private String nroColegiatura;
    private String pinSeguridad;
    private List<String> acciones;

    public AuthResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipoToken() { return tipoToken; }
    public void setTipoToken(String tipoToken) { this.tipoToken = tipoToken; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getSubdominio() { return subdominio; }
    public void setSubdominio(String subdominio) { this.subdominio = subdominio; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getVerticalId() { return verticalId; }
    public void setVerticalId(String verticalId) { this.verticalId = verticalId; }
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public boolean isEsPropietario() { return esPropietario; }
    public void setEsPropietario(boolean esPropietario) { this.esPropietario = esPropietario; }
    public boolean isEsSuperadmin() { return esSuperadmin; }
    public void setEsSuperadmin(boolean esSuperadmin) { this.esSuperadmin = esSuperadmin; }
    public String getDbHost() { return dbHost; }
    public void setDbHost(String dbHost) { this.dbHost = dbHost; }

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getNroColegiatura() { return nroColegiatura; }
    public void setNroColegiatura(String nroColegiatura) { this.nroColegiatura = nroColegiatura; }
    public String getPinSeguridad() { return pinSeguridad; }
    public void setPinSeguridad(String pinSeguridad) { this.pinSeguridad = pinSeguridad; }
    public List<String> getAcciones() { return acciones; }
    public void setAcciones(List<String> acciones) { this.acciones = acciones; }
}
