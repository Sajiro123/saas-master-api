package com.saas.master.tenants.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class SubscriptionResponse {
    private UUID id;
    private UUID tenantId;
    private String tenantNombreComercial;
    private String tenantSubdominio;
    private String verticalId;
    private String planId;
    private String planNombre;
    private BigDecimal montoPago;
    private String metodoPago;
    private String codigoTransaccion;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private ZonedDateTime creadoEn;

    public SubscriptionResponse() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getTenantNombreComercial() { return tenantNombreComercial; }
    public void setTenantNombreComercial(String tenantNombreComercial) { this.tenantNombreComercial = tenantNombreComercial; }
    public String getTenantSubdominio() { return tenantSubdominio; }
    public void setTenantSubdominio(String tenantSubdominio) { this.tenantSubdominio = tenantSubdominio; }
    public String getVerticalId() { return verticalId; }
    public void setVerticalId(String verticalId) { this.verticalId = verticalId; }
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
    public BigDecimal getMontoPago() { return montoPago; }
    public void setMontoPago(BigDecimal montoPago) { this.montoPago = montoPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getCodigoTransaccion() { return codigoTransaccion; }
    public void setCodigoTransaccion(String codigoTransaccion) { this.codigoTransaccion = codigoTransaccion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
