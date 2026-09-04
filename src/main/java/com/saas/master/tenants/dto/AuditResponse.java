package com.saas.master.tenants.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditResponse {
    private UUID id;
    private String tenantNombre;
    private String usuarioEmail;
    private String tipoEvento;
    private String descripcion;
    private String detallesJson;
    private String direccionIp;
    private ZonedDateTime creadoEn;

    public AuditResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenantNombre() { return tenantNombre; }
    public void setTenantNombre(String tenantNombre) { this.tenantNombre = tenantNombre; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
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
