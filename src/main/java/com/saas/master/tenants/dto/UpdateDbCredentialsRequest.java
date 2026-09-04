package com.saas.master.tenants.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class UpdateDbCredentialsRequest {
    @NotBlank(message = "El host es obligatorio")
    private String hostBd;
    private Integer puertoBd = 5432;
    private String nombreBd = "postgres";
    private String usuarioBd = "postgres";
    private String passwordBd;
    private String modoSsl = "require";
    private Integer poolMin = 2;
    private Integer poolMax = 10;

    public UpdateDbCredentialsRequest() {}

    public String getHostBd() { return hostBd; }
    public void setHostBd(String hostBd) { this.hostBd = hostBd; }
    public Integer getPuertoBd() { return puertoBd; }
    public void setPuertoBd(Integer puertoBd) { this.puertoBd = puertoBd; }
    public String getNombreBd() { return nombreBd; }
    public void setNombreBd(String nombreBd) { this.nombreBd = nombreBd; }
    public String getUsuarioBd() { return usuarioBd; }
    public void setUsuarioBd(String usuarioBd) { this.usuarioBd = usuarioBd; }
    public String getPasswordBd() { return passwordBd; }
    public void setPasswordBd(String passwordBd) { this.passwordBd = passwordBd; }
    public String getModoSsl() { return modoSsl; }
    public void setModoSsl(String modoSsl) { this.modoSsl = modoSsl; }
    public Integer getPoolMin() { return poolMin; }
    public void setPoolMin(Integer poolMin) { this.poolMin = poolMin; }
    public Integer getPoolMax() { return poolMax; }
    public void setPoolMax(Integer poolMax) { this.poolMax = poolMax; }
}
