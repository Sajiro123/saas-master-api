package com.saas.master.tenants.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SaveSubscriptionRequest {
    public UUID negocioId;
    public String planId;
    public LocalDate fechaInicio;
    public LocalDate fechaFin;
    public String estado; // ACTIVA, SUSPENDIDA, VENCIDA, CANCELADA
    public BigDecimal montoPago;
    public String metodoPago;
    public String codigoTransaccion;
}
