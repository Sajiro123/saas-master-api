package com.saas.master.tenants.dto;

public class ConnectionTestResultDTO {
    private boolean success;
    private int latencyMs;
    private String dbVersion;
    private String message;

    public ConnectionTestResultDTO() {}

    public ConnectionTestResultDTO(boolean success, int latencyMs, String dbVersion, String message) {
        this.success = success;
        this.latencyMs = latencyMs;
        this.dbVersion = dbVersion;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public int getLatencyMs() { return latencyMs; }
    public void setLatencyMs(int latencyMs) { this.latencyMs = latencyMs; }
    public String getDbVersion() { return dbVersion; }
    public void setDbVersion(String dbVersion) { this.dbVersion = dbVersion; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
