package com.saas.master.auth.service;

import com.saas.master.auth.dto.AuthResponse;
import com.saas.master.auth.dto.LoginRequest;
import com.saas.master.auth.dto.ValidateTokenResponse;
import com.saas.master.common.exception.BadRequestException;
import com.saas.master.common.exception.ResourceNotFoundException;
import com.saas.master.rbac.entity.Accion;
import com.saas.master.rbac.entity.Persona;
import com.saas.master.rbac.entity.UsuarioNegocio;
import com.saas.master.rbac.repository.UsuarioNegocioRepository;
import com.saas.master.security.JwtTokenProvider;
import com.saas.master.tenants.entity.CredencialBdNegocio;
import com.saas.master.tenants.entity.Negocio;
import com.saas.master.tenants.entity.UsuarioGlobal;
import com.saas.master.tenants.repository.CredencialBdNegocioRepository;
import com.saas.master.tenants.repository.UsuarioGlobalRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioGlobalRepository usuarioGlobalRepository;
    private final UsuarioNegocioRepository usuarioNegocioRepository;
    private final CredencialBdNegocioRepository credencialBdRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UsuarioGlobalRepository usuarioGlobalRepository,
            UsuarioNegocioRepository usuarioNegocioRepository,
            CredencialBdNegocioRepository credencialBdRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.usuarioGlobalRepository = usuarioGlobalRepository;
        this.usuarioNegocioRepository = usuarioNegocioRepository;
        this.credencialBdRepository = credencialBdRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // 1. Verificar en usuarios_negocios (Cajeros, Químicos, Admins de Farmacia)
        Optional<UsuarioNegocio> optNegocio = usuarioNegocioRepository.findByEmail(email);
        if (optNegocio.isPresent()) {
            UsuarioNegocio un = optNegocio.get();
            if (!Boolean.TRUE.equals(un.getEstaActivo())) {
                throw new BadRequestException("La cuenta de usuario se encuentra inactiva");
            }

            Negocio negocio = un.getNegocio();
            if (negocio != null && !"ACTIVO".equalsIgnoreCase(negocio.getEstado())) {
                throw new BadRequestException("El negocio asociado (" + negocio.getNombreComercial() + ") está " + negocio.getEstado());
            }

            // Validar password con BCrypt o texto plano
            boolean passwordMatch = passwordEncoder.matches(request.getPassword(), un.getPasswordHash())
                    || request.getPassword().equals(un.getPasswordHash());

            if (!passwordMatch) {
                throw new BadRequestException("Credenciales inválidas: contraseña incorrecta");
            }

            // Actualizar último acceso
            un.setUltimoAcceso(ZonedDateTime.now());
            usuarioNegocioRepository.save(un);

            UUID tenantId = (negocio != null) ? negocio.getId() : UUID.randomUUID();
            String subdominio = (negocio != null) ? negocio.getSubdominio() : "medicare";
            String verticalId = (negocio != null && negocio.getVertical() != null) ? negocio.getVertical().getId() : "FARMACIA";
            String planId = (negocio != null && negocio.getPlanActual() != null) ? negocio.getPlanActual().getId() : "PLAN-PRO";
            boolean esMaster = Boolean.TRUE.equals(un.getEsMaster()) || (un.getPerfil() != null && ("SUPERADMIN".equalsIgnoreCase(un.getPerfil().getCodigo()) || "ADMIN_MASTER".equalsIgnoreCase(un.getPerfil().getCodigo())));
            boolean esAdmin = (un.getPerfil() != null && "ADMIN_NEGOCIO".equalsIgnoreCase(un.getPerfil().getCodigo())) || esMaster;

            String token = jwtTokenProvider.generateToken(
                    un.getId(),
                    un.getEmail(),
                    tenantId,
                    subdominio,
                    verticalId,
                    esMaster,
                    esAdmin
            );

            String dbHost = (negocio != null) ? credencialBdRepository.findById(negocio.getId())
                    .map(CredencialBdNegocio::getHostBd)
                    .orElse("No asignado") : "No asignado";

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUsuarioId(un.getId());
            response.setEmail(un.getEmail());
            response.setTenantId(tenantId);
            response.setSubdominio(subdominio);
            response.setNombreComercial((negocio != null) ? negocio.getNombreComercial() : "Farmacia Medicare");
            response.setVerticalId(verticalId);
            response.setPlanId(planId);
            response.setEsPropietario(esAdmin || esMaster);
            response.setEsSuperadmin(esMaster);
            response.setDbHost(dbHost);

            if (un.getPerfil() != null) {
                response.setRolCodigo(un.getPerfil().getCodigo());
                response.setRolNombre(un.getPerfil().getNombre());
                response.setAcciones(un.getPerfil().getAcciones().stream().map(Accion::getCodigo).collect(Collectors.toList()));
            }

            if (un.getPersona() != null) {
                Persona p = un.getPersona();
                response.setNombreCompleto(p.getNombres() + " " + p.getApellidos());
                response.setNroColegiatura(p.getNroColegiatura());
            } else {
                response.setNombreCompleto(un.getEmail());
            }

            response.setPinSeguridad(un.getPinSeguridad());
            return response;
        }

        // 2. Si no está en usuarios_negocios, verificar en usuarios_globales (Superadmin / Master legacy)
        UsuarioGlobal usuario = usuarioGlobalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.getEmail()));

        if (!usuario.getEstaActivo()) {
            throw new BadRequestException("La cuenta de usuario se encuentra inactiva");
        }

        Negocio negocio = usuario.getNegocio();
        if (negocio != null && !"ACTIVO".equalsIgnoreCase(negocio.getEstado())) {
            throw new BadRequestException("El negocio asociado (" + negocio.getNombreComercial() + ") está " + negocio.getEstado());
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash()) && !request.getPassword().equals(usuario.getPasswordHash())) {
            throw new BadRequestException("Credenciales inválidas: contraseña incorrecta");
        }

        // Actualizar último login
        usuario.setUltimoInicioSesion(ZonedDateTime.now());
        usuarioGlobalRepository.save(usuario);

        UUID tenantId = (negocio != null) ? negocio.getId() : UUID.randomUUID();
        String subdominio = (negocio != null) ? negocio.getSubdominio() : "medicare";
        String verticalId = (negocio != null && negocio.getVertical() != null) ? negocio.getVertical().getId() : "FARMACIA";
        String planId = (negocio != null && negocio.getPlanActual() != null) ? negocio.getPlanActual().getId() : "PLAN-PRO";

        String token = jwtTokenProvider.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                tenantId,
                subdominio,
                verticalId,
                Boolean.TRUE.equals(usuario.getEsSuperadmin()),
                Boolean.TRUE.equals(usuario.getEsPropietario())
        );

        String dbHost = (negocio != null) ? credencialBdRepository.findById(negocio.getId())
                .map(CredencialBdNegocio::getHostBd)
                .orElse("No asignado") : "No asignado";

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsuarioId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setTenantId(tenantId);
        response.setSubdominio(subdominio);
        response.setNombreComercial((negocio != null) ? negocio.getNombreComercial() : "Farmacia Medicare");
        response.setVerticalId(verticalId);
        response.setPlanId(planId);
        response.setEsPropietario(Boolean.TRUE.equals(usuario.getEsPropietario()));
        response.setEsSuperadmin(Boolean.TRUE.equals(usuario.getEsSuperadmin()));
        response.setDbHost(dbHost);
        response.setRolCodigo(Boolean.TRUE.equals(usuario.getEsSuperadmin()) ? "SUPERADMIN" : "ADMIN_NEGOCIO");
        response.setRolNombre(Boolean.TRUE.equals(usuario.getEsSuperadmin()) ? "Superadministrador" : "Administrador");
        response.setNombreCompleto(usuario.getEmail());

        return response;
    }

    public ValidateTokenResponse validateToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            return new ValidateTokenResponse(
                    true,
                    claims.getSubject(),
                    claims.get("tenant_id", String.class),
                    claims.get("vertical", String.class),
                    claims.get("subdominio", String.class)
            );
        }

        return new ValidateTokenResponse(false, null, null, null, null);
    }
}
