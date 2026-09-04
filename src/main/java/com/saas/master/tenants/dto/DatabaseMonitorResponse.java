package com.saas.master.tenants.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DatabaseMonitorResponse {
    private UUID tenantId;
    private String tenantNombre;
    private String subdominio;
    private String verticalId;
    private String hostBd;
    private Integer puertoBd;
    private String usuarioBd;
    private Integer poolMin;
    private Integer poolMax;
    private String status; // 'ONLINE' | 'STANDBY'
    private Integer latencyMs;
    private ZonedDateTime actualizadoEn;

    public DatabaseMonitorResponse() {}

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getTenantNombre() { return tenantNombre; }
    public void setTenantNombre(String tenantNombre) { this.tenantNombre = tenantNombre; }
    public String getSubdominio() { return subdominio; }
    public void setSubdominio(String subdominio) { this.subdominio = subdominio; }
    public String getVerticalId() { return verticalId; }
    public void setVerticalId(String verticalId) { this.verticalId = verticalId; }
    public String getHostBd() { return hostBd; }
    public void setHostBd(String hostBd) { this.hostBd = hostBd; }
    public Integer getPuertoBd() { return puertoBd; }
    public void setPuertoBd(Integer puertoBd) { this.puertoBd = puertoBd; }
    public String getUsuarioBd() { return usuarioBd; }
    public void setUsuarioBd(String usuarioBd) { this.usuarioBd = usuarioBd; }
    public Integer getPoolMin() { return poolMin; }
    public void setPoolMin(Integer poolMin) { this.poolMin = poolMin; }
    public Integer getPoolMax() { return poolMax; }
    public void setPoolMax(Integer poolMax) { this.poolMax = poolMax; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
    public ZonedDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(ZonedDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
