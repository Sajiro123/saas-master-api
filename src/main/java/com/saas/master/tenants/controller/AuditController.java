package com.saas.master.tenants.controller;

import com.saas.master.common.dto.ApiResponse;
import com.saas.master.tenants.dto.AuditResponse;
import com.saas.master.tenants.repository.AuditoriaEventoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Auditoría Global", description = "Bitácora de eventos del sistema y aprovisionamiento de bases de datos")
public class AuditController {

    private final AuditoriaEventoRepository auditoriaRepository;

    public AuditController(AuditoriaEventoRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping
    @Operation(summary = "Consultar bitácora de auditoría", description = "Retorna los eventos registrados ordenados cronológicamente.")
    public ResponseEntity<ApiResponse<List<AuditResponse>>> getAuditLogs() {
        List<AuditResponse> list = auditoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "creadoEn")).stream().map(a -> {
            AuditResponse dto = new AuditResponse();
            dto.setId(a.getId());
            dto.setTenantNombre(a.getNegocio() != null ? a.getNegocio().getNombreComercial() : "Sistema Global");
            dto.setUsuarioEmail(a.getUsuarioGlobal() != null ? a.getUsuarioGlobal().getEmail() : "Sistema Automático");
            dto.setTipoEvento(a.getTipoEvento());
            dto.setDescripcion(a.getDescripcion());
            dto.setDetallesJson(a.getDetallesJson());
            dto.setDireccionIp(a.getDireccionIp());
            dto.setCreadoEn(a.getCreadoEn());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("Eventos de auditoría", list));
    }
}
