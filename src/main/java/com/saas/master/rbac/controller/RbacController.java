package com.saas.master.rbac.controller;

import com.saas.master.common.dto.ApiResponse;
import com.saas.master.rbac.dto.UsuarioNegocioDTO;
import com.saas.master.rbac.entity.Accion;
import com.saas.master.rbac.entity.Perfil;
import com.saas.master.rbac.service.RbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios-negocio")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios y Seguridad (RBAC)", description = "Gestión de usuarios, personas, roles y permisos en Master DB")
public class RbacController {

    private final RbacService rbacService;

    public RbacController(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios por negocio o todos")
    public ResponseEntity<ApiResponse<List<UsuarioNegocioDTO.Response>>> listarUsuarios(
            @RequestParam(required = false) UUID negocioId) {
        List<UsuarioNegocioDTO.Response> usuarios = (negocioId != null) 
                ? rbacService.listarUsuariosPorNegocio(negocioId)
                : rbacService.listarTodosLosUsuarios();
        return ResponseEntity.ok(ApiResponse.ok("Usuarios listados exitosamente", usuarios));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario vinculado a persona y rol")
    public ResponseEntity<ApiResponse<UsuarioNegocioDTO.Response>> crearUsuario(
            @RequestBody UsuarioNegocioDTO.Request request) {
        UsuarioNegocioDTO.Response nuevo = rbacService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de usuario, persona o rol")
    public ResponseEntity<ApiResponse<UsuarioNegocioDTO.Response>> actualizarUsuario(
            @PathVariable UUID id,
            @RequestBody UsuarioNegocioDTO.Request request) {
        UsuarioNegocioDTO.Response actualizado = rbacService.actualizarUsuario(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente", actualizado));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar usuario")
    public ResponseEntity<ApiResponse<String>> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam boolean activo) {
        rbacService.cambiarEstado(id, activo);
        return ResponseEntity.ok(ApiResponse.ok("Estado modificado exitosamente", activo ? "ACTIVO" : "INACTIVO"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario por ID")
    public ResponseEntity<ApiResponse<String>> eliminarUsuario(@PathVariable UUID id) {
        rbacService.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente", "OK"));
    }

    @GetMapping("/perfiles")
    @Operation(summary = "Listar perfiles / roles disponibles")
    public ResponseEntity<ApiResponse<List<Perfil>>> listarPerfiles(@RequestParam(required = false) UUID negocioId) {
        return ResponseEntity.ok(ApiResponse.ok("Perfiles listados", rbacService.listarPerfiles(negocioId)));
    }

    @GetMapping("/acciones")
    @Operation(summary = "Listar catálogo de acciones y permisos del sistema")
    public ResponseEntity<ApiResponse<List<Accion>>> listarAcciones() {
        return ResponseEntity.ok(ApiResponse.ok("Acciones listadas", rbacService.listarAcciones()));
    }
}
