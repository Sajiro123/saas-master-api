package com.saas.master.tenants.controller;

import com.saas.master.common.dto.ApiResponse;
import com.saas.master.tenants.dto.CreateTenantRequest;
import com.saas.master.tenants.dto.TenantDbCredentialsDTO;
import com.saas.master.tenants.dto.TenantResponse;
import com.saas.master.tenants.dto.UpdateTenantRequest;
import com.saas.master.tenants.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Control Plane - Negocios (Tenants)", description = "Gestión completa de inquilinos, CRUD, resolución y credenciales")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los negocios", description = "Retorna el catálogo de tenants registrados en el SaaS.")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants() {
        return ResponseEntity.ok(ApiResponse.ok("Listado de negocios", tenantService.getAllTenants()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener negocio por ID", description = "Consulta el detalle de un tenant específico por su UUID.")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Negocio encontrado", tenantService.getTenantById(id)));
    }

    @GetMapping("/subdomain/{subdomain}")
    @Operation(summary = "Resolver negocio por subdominio", description = "Permite a los frontends identificar el tenant mediante su subdominio.")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantBySubdomain(@PathVariable String subdomain) {
        return ResponseEntity.ok(ApiResponse.ok("Subdominio resuelto", tenantService.getTenantBySubdomain(subdomain)));
    }

    @GetMapping("/{id}/credentials")
    @Operation(summary = "Obtener credenciales de base de datos", description = "Utilizado por las APIs Operativas para enrutar conexiones dinámicamente.")
    public ResponseEntity<ApiResponse<TenantDbCredentialsDTO>> getTenantDbCredentials(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Credenciales de base de datos", tenantService.getTenantDbCredentials(id)));
    }

    @PostMapping
    @Operation(summary = "Dar de alta un nuevo negocio", description = "Crea un nuevo tenant en el catálogo maestro y le aprovisiona credenciales.")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Negocio creado exitosamente", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar datos de un negocio", description = "Actualiza los campos comerciales, vertical, plan o estado de un inquilino.")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @PathVariable UUID id,
            @RequestBody UpdateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Negocio actualizado exitosamente", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un negocio definitivamente", description = "Elimina el tenant, sus suscripciones y credenciales asociadas.")
    public ResponseEntity<ApiResponse<String>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.ok("Negocio eliminado exitosamente", "DELETED"));
    }
}
