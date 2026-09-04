package com.saas.master.tenants.service;

import com.saas.master.common.exception.BadRequestException;
import com.saas.master.common.exception.ResourceNotFoundException;
import com.saas.master.tenants.dto.CreateTenantRequest;
import com.saas.master.tenants.dto.TenantDbCredentialsDTO;
import com.saas.master.tenants.dto.TenantResponse;
import com.saas.master.tenants.dto.UpdateTenantRequest;
import com.saas.master.tenants.entity.*;
import com.saas.master.tenants.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final NegocioRepository negocioRepository;
    private final VerticalRepository verticalRepository;
    private final PlanSuscripcionRepository planRepository;
    private final CredencialBdNegocioRepository credencialBdRepository;
    private final UsuarioGlobalRepository usuarioGlobalRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final AuditoriaEventoRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantService(
            NegocioRepository negocioRepository,
            VerticalRepository verticalRepository,
            PlanSuscripcionRepository planRepository,
            CredencialBdNegocioRepository credencialBdRepository,
            UsuarioGlobalRepository usuarioGlobalRepository,
            SuscripcionRepository suscripcionRepository,
            AuditoriaEventoRepository auditoriaRepository,
            PasswordEncoder passwordEncoder) {
        this.negocioRepository = negocioRepository;
        this.verticalRepository = verticalRepository;
        this.planRepository = planRepository;
        this.credencialBdRepository = credencialBdRepository;
        this.usuarioGlobalRepository = usuarioGlobalRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TenantResponse> getAllTenants() {
        return negocioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TenantResponse getTenantById(UUID id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + id));
        return mapToResponse(negocio);
    }

    public TenantResponse getTenantBySubdomain(String subdomain) {
        Negocio negocio = negocioRepository.findBySubdominio(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con subdominio: " + subdomain));
        return mapToResponse(negocio);
    }

    @Transactional(readOnly = true)
    public TenantDbCredentialsDTO getTenantDbCredentials(UUID tenantId) {
        CredencialBdNegocio creds = credencialBdRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("No existen credenciales de base de datos para el tenant: " + tenantId));

        return new TenantDbCredentialsDTO(
                creds.getNegocioId(),
                creds.getHostBd(),
                creds.getPuertoBd(),
                creds.getNombreBd(),
                creds.getUsuarioBd(),
                creds.getPasswordBdCifrado(),
                creds.getModoSsl(),
                creds.getPoolMinConexiones(),
                creds.getPoolMaxConexiones()
        );
    }

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        if (negocioRepository.existsBySubdominio(request.getSubdominio())) {
            throw new BadRequestException("El subdominio '" + request.getSubdominio() + "' ya está registrado");
        }
        if (negocioRepository.existsByNumeroIdentificacion(request.getNumeroIdentificacion())) {
            throw new BadRequestException("El número de identificación (RUC) '" + request.getNumeroIdentificacion() + "' ya está registrado");
        }

        Vertical vertical = verticalRepository.findById(request.getVerticalId())
                .orElseThrow(() -> new ResourceNotFoundException("Vertical no encontrada: " + request.getVerticalId()));

        PlanSuscripcion plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + request.getPlanId()));

        Negocio negocio = new Negocio();
        negocio.setSubdominio(request.getSubdominio().toLowerCase().trim());
        negocio.setRazonSocial(request.getRazonSocial());
        negocio.setNombreComercial(request.getNombreComercial() != null ? request.getNombreComercial() : request.getRazonSocial());
        negocio.setNumeroIdentificacion(request.getNumeroIdentificacion());
        negocio.setVertical(vertical);
        negocio.setPlanActual(plan);
        negocio.setEmailContacto(request.getEmailContacto());
        negocio.setTelefonoContacto(request.getTelefonoContacto());
        negocio.setEstado("ACTIVO");

        negocio = negocioRepository.save(negocio);

        // Guardar credenciales de BD si se proporcionan
        if (request.getDbHost() != null && !request.getDbHost().isBlank()) {
            CredencialBdNegocio creds = new CredencialBdNegocio();
            creds.setNegocio(negocio);
            creds.setHostBd(request.getDbHost());
            creds.setPuertoBd(request.getDbPort() != null ? request.getDbPort() : 5432);
            creds.setNombreBd("postgres");
            creds.setUsuarioBd("postgres");
            creds.setPasswordBdCifrado(request.getDbPassword() != null ? request.getDbPassword() : "PENDIENTE");
            credencialBdRepository.save(creds);
        }

        // Crear usuario propietario inicial si no existe
        if (!usuarioGlobalRepository.existsByEmail(request.getEmailContacto())) {
            UsuarioGlobal owner = new UsuarioGlobal();
            owner.setEmail(request.getEmailContacto());
            owner.setPasswordHash(passwordEncoder.encode("Temporal123*"));
            owner.setNegocio(negocio);
            owner.setEsPropietario(true);
            owner.setEsSuperadmin(false);
            owner.setEstaActivo(true);
            usuarioGlobalRepository.save(owner);
        }

        // Auditoría
        AuditoriaEvento evento = new AuditoriaEvento(
                negocio, null, "ALTA_TENANT_MASTER_API",
                "Negocio dado de alta mediante Master API",
                "{\"subdominio\": \"" + negocio.getSubdominio() + "\"}",
                "127.0.0.1"
        );
        auditoriaRepository.save(evento);

        return mapToResponse(negocio);
    }

    @Transactional
    public TenantResponse updateTenant(UUID id, UpdateTenantRequest request) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + id));

        if (request.getNombreComercial() != null && !request.getNombreComercial().isBlank()) {
            negocio.setNombreComercial(request.getNombreComercial());
        }
        if (request.getRazonSocial() != null && !request.getRazonSocial().isBlank()) {
            negocio.setRazonSocial(request.getRazonSocial());
        }
        if (request.getNumeroIdentificacion() != null && !request.getNumeroIdentificacion().isBlank()) {
            negocio.setNumeroIdentificacion(request.getNumeroIdentificacion());
        }
        if (request.getEmailContacto() != null && !request.getEmailContacto().isBlank()) {
            negocio.setEmailContacto(request.getEmailContacto());
        }
        if (request.getTelefonoContacto() != null) {
            negocio.setTelefonoContacto(request.getTelefonoContacto());
        }
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            negocio.setEstado(request.getEstado());
        }

        if (request.getVerticalId() != null && !request.getVerticalId().isBlank()) {
            Vertical vertical = verticalRepository.findById(request.getVerticalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vertical no encontrada: " + request.getVerticalId()));
            negocio.setVertical(vertical);
        }

        if (request.getPlanId() != null && !request.getPlanId().isBlank()) {
            PlanSuscripcion plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + request.getPlanId()));
            negocio.setPlanActual(plan);
        }

        negocio.setActualizadoEn(ZonedDateTime.now());
        negocio = negocioRepository.save(negocio);

        // Actualizar host si se especificó
        if (request.getDbHost() != null && !request.getDbHost().isBlank()) {
            CredencialBdNegocio creds = credencialBdRepository.findById(id).orElse(new CredencialBdNegocio());
            creds.setNegocio(negocio);
            creds.setHostBd(request.getDbHost());
            creds.setActualizadoEn(ZonedDateTime.now());
            credencialBdRepository.save(creds);
        }

        // Auditoría
        AuditoriaEvento evento = new AuditoriaEvento(
                negocio, null, "MODIFICACION_TENANT",
                "Datos actualizados para negocio " + negocio.getNombreComercial(),
                "{\"estado\": \"" + negocio.getEstado() + "\"}",
                "127.0.0.1"
        );
        auditoriaRepository.save(evento);

        return mapToResponse(negocio);
    }

    @Transactional
    public void deleteTenant(UUID id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + id));

        String nombre = negocio.getNombreComercial();
        String subdominio = negocio.getSubdominio();

        // 1. Eliminar suscripciones
        suscripcionRepository.deleteAll(suscripcionRepository.findByNegocioId(id));

        // 2. Eliminar credenciales
        credencialBdRepository.deleteById(id);

        // 3. Eliminar usuarios del tenant
        usuarioGlobalRepository.deleteAll(usuarioGlobalRepository.findByNegocioId(id));

        // 4. Eliminar el negocio
        negocioRepository.delete(negocio);

        // 5. Registrar evento de auditoría global
        AuditoriaEvento evento = new AuditoriaEvento(
                null, null, "ELIMINACION_TENANT",
                "Inquilino eliminado definitivamente: " + nombre + " (" + subdominio + ")",
                "{\"tenantId\": \"" + id + "\", \"subdominio\": \"" + subdominio + "\"}",
                "127.0.0.1"
        );
        auditoriaRepository.save(evento);
    }

    private TenantResponse mapToResponse(Negocio negocio) {
        TenantResponse dto = new TenantResponse();
        dto.setId(negocio.getId());
        dto.setSubdominio(negocio.getSubdominio());
        dto.setRazonSocial(negocio.getRazonSocial());
        dto.setNombreComercial(negocio.getNombreComercial());
        dto.setNumeroIdentificacion(negocio.getNumeroIdentificacion());
        dto.setVerticalId(negocio.getVertical().getId());
        dto.setVerticalNombre(negocio.getVertical().getNombre());
        dto.setPlanId(negocio.getPlanActual().getId());
        dto.setPlanNombre(negocio.getPlanActual().getNombre());
        dto.setEstado(negocio.getEstado());
        dto.setEmailContacto(negocio.getEmailContacto());
        dto.setTelefonoContacto(negocio.getTelefonoContacto());
        dto.setCreadoEn(negocio.getCreadoEn());

        credencialBdRepository.findById(negocio.getId())
                .ifPresent(c -> dto.setDbHost(c.getHostBd()));

        return dto;
    }
}
