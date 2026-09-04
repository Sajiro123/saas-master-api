package com.saas.master.tenants.dto;

import java.util.UUID;

public class TenantDbCredentialsDTO {
    private UUID tenantId;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String sslMode;
    private Integer poolMin;
    private Integer poolMax;

    public TenantDbCredentialsDTO() {}

    public TenantDbCredentialsDTO(UUID tenantId, String host, Integer port, String database, String username, String password, String sslMode, Integer poolMin, Integer poolMax) {
        this.tenantId = tenantId;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sslMode = sslMode;
        this.poolMin = poolMin;
        this.poolMax = poolMax;
    }

    public UUID getTenantId() { return tenantId; }
    public String getHost() { return host; }
    public Integer getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSslMode() { return sslMode; }
    public Integer getPoolMin() { return poolMin; }
    public Integer getPoolMax() { return poolMax; }
}
