package com.saas.master.tenants.controller;

import com.saas.master.common.dto.ApiResponse;
import com.saas.master.common.exception.BadRequestException;
import com.saas.master.common.exception.ResourceNotFoundException;
import com.saas.master.tenants.dto.SavePlanRequest;
import com.saas.master.tenants.dto.SaveSubscriptionRequest;
import com.saas.master.tenants.dto.SubscriptionResponse;
import com.saas.master.tenants.entity.Negocio;
import com.saas.master.tenants.entity.PlanSuscripcion;
import com.saas.master.tenants.entity.Suscripcion;
import com.saas.master.tenants.entity.Vertical;
import com.saas.master.tenants.repository.NegocioRepository;
import com.saas.master.tenants.repository.PlanSuscripcionRepository;
import com.saas.master.tenants.repository.SuscripcionRepository;
import com.saas.master.tenants.repository.VerticalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subscriptions")
@CrossOrigin(origins = "*")
@Tag(name = "Suscripciones & Planes", description = "Gestión independiente de cobros, planes activos y pagos por negocio")
public class SubscriptionController {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanSuscripcionRepository planRepository;
    private final NegocioRepository negocioRepository;
    private final VerticalRepository verticalRepository;

    public SubscriptionController(
            SuscripcionRepository suscripcionRepository,
            PlanSuscripcionRepository planRepository,
            NegocioRepository negocioRepository,
            VerticalRepository verticalRepository) {
        this.suscripcionRepository = suscripcionRepository;
        this.planRepository = planRepository;
        this.negocioRepository = negocioRepository;
        this.verticalRepository = verticalRepository;
    }

    public static class PlanDTO {
        public String id;
        public String verticalId;
        public String nombre;
        public Integer maxSucursales;
        public Integer maxUsuarios;
        public BigDecimal precioMensual;
        public String caracteristicas;
        public Boolean estaActivo;
    }

    // ==========================================
    // 1. SUSCRIPCIONES POR NEGOCIO (INDEPENDIENTES)
    // ==========================================

    @GetMapping
    @Operation(summary = "Listar suscripciones independientes por negocio o todas")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getAllSubscriptions(
            @RequestParam(required = false) UUID negocioId) {
        
        List<Suscripcion> suscripciones = (negocioId != null)
                ? suscripcionRepository.findByNegocioId(negocioId)
                : suscripcionRepository.findAll();

        List<SubscriptionResponse> list = suscripciones.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Listado de suscripciones", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de suscripción por ID")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(@PathVariable UUID id) {
        Suscripcion s = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Suscripción encontrada", mapToResponse(s)));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Crear o asignar nueva suscripción a un negocio")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @RequestBody SaveSubscriptionRequest request) {

        if (request.negocioId == null) {
            throw new BadRequestException("El negocioId es obligatorio");
        }
        if (request.planId == null || request.planId.isBlank()) {
            throw new BadRequestException("El planId es obligatorio");
        }

        Negocio negocio = negocioRepository.findById(request.negocioId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + request.negocioId));

        PlanSuscripcion plan = planRepository.findById(request.planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + request.planId));

        Suscripcion s = new Suscripcion();
        s.setNegocio(negocio);
        s.setPlan(plan);
        s.setFechaInicio(request.fechaInicio != null ? request.fechaInicio : LocalDate.now());
        s.setFechaFin(request.fechaFin != null ? request.fechaFin : LocalDate.now().plusMonths(1));
        s.setEstado(request.estado != null ? request.estado.toUpperCase() : "ACTIVA");
        s.setMontoPago(request.montoPago != null ? request.montoPago : plan.getPrecioMensual());
        s.setMetodoPago(request.metodoPago != null ? request.metodoPago : "TRANSFERENCIA");
        s.setCodigoTransaccion(request.codigoTransaccion != null ? request.codigoTransaccion : "TXN-" + System.currentTimeMillis());

        Suscripcion guardada = suscripcionRepository.save(s);

        if ("ACTIVA".equalsIgnoreCase(guardada.getEstado())) {
            negocio.setPlanActual(plan);
            negocioRepository.save(negocio);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Suscripción asignada exitosamente", mapToResponse(guardada)));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualizar suscripción de un negocio")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable UUID id,
            @RequestBody SaveSubscriptionRequest request) {

        Suscripcion s = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));

        if (request.planId != null && !request.planId.isBlank()) {
            PlanSuscripcion plan = planRepository.findById(request.planId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + request.planId));
            s.setPlan(plan);
            if ("ACTIVA".equalsIgnoreCase(s.getEstado()) && s.getNegocio() != null) {
                s.getNegocio().setPlanActual(plan);
                negocioRepository.save(s.getNegocio());
            }
        }

        if (request.fechaInicio != null) s.setFechaInicio(request.fechaInicio);
        if (request.fechaFin != null) s.setFechaFin(request.fechaFin);
        if (request.estado != null) s.setEstado(request.estado.toUpperCase());
        if (request.montoPago != null) s.setMontoPago(request.montoPago);
        if (request.metodoPago != null) s.setMetodoPago(request.metodoPago);
        if (request.codigoTransaccion != null) s.setCodigoTransaccion(request.codigoTransaccion);

        Suscripcion actualizada = suscripcionRepository.save(s);
        return ResponseEntity.ok(ApiResponse.ok("Suscripción actualizada exitosamente", mapToResponse(actualizada)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Eliminar suscripción de negocio")
    public ResponseEntity<ApiResponse<String>> deleteSubscription(@PathVariable UUID id) {
        Suscripcion s = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripción no encontrada con ID: " + id));
        suscripcionRepository.delete(s);
        return ResponseEntity.ok(ApiResponse.ok("Suscripción eliminada exitosamente", "OK"));
    }

    // ==========================================
    // 2. CATÁLOGO DE PLANES SAAS
    // ==========================================

    @GetMapping("/plans")
    @Operation(summary = "Listar todos los planes de suscripción")
    public ResponseEntity<ApiResponse<List<PlanDTO>>> getAllPlans() {
        List<PlanDTO> list = planRepository.findAll().stream().map(this::mapPlanToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Catálogo de planes", list));
    }

    @PostMapping("/plans")
    @Transactional
    @Operation(summary = "Crear nuevo plan de suscripción")
    public ResponseEntity<ApiResponse<PlanDTO>> createPlan(@RequestBody SavePlanRequest request) {
        if (request.id == null || request.id.isBlank()) {
            throw new BadRequestException("El ID del plan es obligatorio");
        }
        if (request.verticalId == null || request.verticalId.isBlank()) {
            request.verticalId = "FARMACIA";
        }

        Vertical vertical = verticalRepository.findById(request.verticalId)
                .orElseThrow(() -> new ResourceNotFoundException("Vertical no encontrada: " + request.verticalId));

        PlanSuscripcion plan = new PlanSuscripcion();
        plan.setId(request.id.toUpperCase().trim());
        plan.setVertical(vertical);
        plan.setNombre(request.nombre != null ? request.nombre : plan.getId());
        plan.setMaxSucursales(request.maxSucursales != null ? request.maxSucursales : 1);
        plan.setMaxUsuarios(request.maxUsuarios != null ? request.maxUsuarios : 3);
        plan.setPrecioMensual(request.precioMensual);
        plan.setCaracteristicas(request.caracteristicas != null ? request.caracteristicas : "[]");
        plan.setEstaActivo(request.estaActivo != null ? request.estaActivo : true);

        PlanSuscripcion guardado = planRepository.save(plan);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Plan creado exitosamente", mapPlanToDTO(guardado)));
    }

    @PutMapping("/plans/{id}")
    @Transactional
    @Operation(summary = "Actualizar plan de suscripción")
    public ResponseEntity<ApiResponse<PlanDTO>> updatePlan(
            @PathVariable String id,
            @RequestBody SavePlanRequest request) {

        PlanSuscripcion plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + id));

        if (request.nombre != null) plan.setNombre(request.nombre);
        if (request.maxSucursales != null) plan.setMaxSucursales(request.maxSucursales);
        if (request.maxUsuarios != null) plan.setMaxUsuarios(request.maxUsuarios);
        if (request.precioMensual != null) plan.setPrecioMensual(request.precioMensual);
        if (request.caracteristicas != null) plan.setCaracteristicas(request.caracteristicas);
        if (request.estaActivo != null) plan.setEstaActivo(request.estaActivo);

        PlanSuscripcion actualizado = planRepository.save(plan);
        return ResponseEntity.ok(ApiResponse.ok("Plan actualizado exitosamente", mapPlanToDTO(actualizado)));
    }

    @DeleteMapping("/plans/{id}")
    @Transactional
    @Operation(summary = "Eliminar plan de suscripción")
    public ResponseEntity<ApiResponse<String>> deletePlan(@PathVariable String id) {
        PlanSuscripcion plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + id));
        planRepository.delete(plan);
        return ResponseEntity.ok(ApiResponse.ok("Plan eliminado exitosamente", "OK"));
    }

    private PlanDTO mapPlanToDTO(PlanSuscripcion p) {
        PlanDTO dto = new PlanDTO();
        dto.id = p.getId();
        dto.nombre = p.getNombre();
        dto.maxSucursales = p.getMaxSucursales();
        dto.maxUsuarios = p.getMaxUsuarios();
        dto.precioMensual = p.getPrecioMensual();
        dto.caracteristicas = p.getCaracteristicas();
        dto.estaActivo = p.getEstaActivo();
        if (p.getVertical() != null) {
            dto.verticalId = p.getVertical().getId();
        }
        return dto;
    }

    private SubscriptionResponse mapToResponse(Suscripcion s) {
        SubscriptionResponse dto = new SubscriptionResponse();
        dto.setId(s.getId());
        if (s.getNegocio() != null) {
            dto.setTenantId(s.getNegocio().getId());
            dto.setTenantNombreComercial(s.getNegocio().getNombreComercial());
            dto.setTenantSubdominio(s.getNegocio().getSubdominio());
            if (s.getNegocio().getVertical() != null) {
                dto.setVerticalId(s.getNegocio().getVertical().getId());
            }
        }
        if (s.getPlan() != null) {
            dto.setPlanId(s.getPlan().getId());
            dto.setPlanNombre(s.getPlan().getNombre());
        }
        dto.setMontoPago(s.getMontoPago());
        dto.setMetodoPago(s.getMetodoPago());
        dto.setCodigoTransaccion(s.getCodigoTransaccion());
        dto.setEstado(s.getEstado());
        dto.setFechaInicio(s.getFechaInicio());
        dto.setFechaFin(s.getFechaFin());
        dto.setCreadoEn(s.getCreadoEn());
        return dto;
    }
}
