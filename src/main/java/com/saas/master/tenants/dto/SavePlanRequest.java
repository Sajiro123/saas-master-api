package com.saas.master.tenants.dto;

import java.math.BigDecimal;

public class SavePlanRequest {
    public String id;
    public String verticalId; // FARMACIA, RETAIL
    public String nombre;
    public Integer maxSucursales;
    public Integer maxUsuarios;
    public BigDecimal precioMensual;
    public String caracteristicas;
    public Boolean estaActivo = true;
}
