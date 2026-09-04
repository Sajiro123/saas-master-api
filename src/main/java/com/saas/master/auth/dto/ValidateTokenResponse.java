package com.saas.master.auth.dto;

public class ValidateTokenResponse {
    private boolean valid;
    private String email;
    private String tenantId;
    private String vertical;
    private String subdominio;

    public ValidateTokenResponse(boolean valid, String email, String tenantId, String vertical, String subdominio) {
        this.valid = valid;
        this.email = email;
        this.tenantId = tenantId;
        this.vertical = vertical;
        this.subdominio = subdominio;
    }

    public boolean isValid() { return valid; }
    public String getEmail() { return email; }
    public String getTenantId() { return tenantId; }
    public String getVertical() { return vertical; }
    public String getSubdominio() { return subdominio; }
}
