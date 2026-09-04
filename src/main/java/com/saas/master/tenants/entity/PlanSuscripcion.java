package com.saas.master.tenants.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "planes_suscripcion")
public class PlanSuscripcion {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vertical_id", nullable = false)
    private Vertical vertical;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "max_sucursales")
    private Integer maxSucursales = 1;

    @Column(name = "max_usuarios")
    private Integer maxUsuarios = 3;

    @Column(name = "precio_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioMensual;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "caracteristicas", columnDefinition = "jsonb")
    private String caracteristicas;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en")
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public PlanSuscripcion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Vertical getVertical() { return vertical; }
    public void setVertical(Vertical vertical) { this.vertical = vertical; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getMaxSucursales() { return maxSucursales; }
    public void setMaxSucursales(Integer maxSucursales) { this.maxSucursales = maxSucursales; }
    public Integer getMaxUsuarios() { return maxUsuarios; }
    public void setMaxUsuarios(Integer maxUsuarios) { this.maxUsuarios = maxUsuarios; }
    public BigDecimal getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }
    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }
    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
