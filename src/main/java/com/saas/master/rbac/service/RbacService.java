package com.saas.master.rbac.service;

import com.saas.master.common.exception.BadRequestException;
import com.saas.master.common.exception.ResourceNotFoundException;
import com.saas.master.rbac.dto.UsuarioNegocioDTO;
import com.saas.master.rbac.entity.Accion;
import com.saas.master.rbac.entity.Perfil;
import com.saas.master.rbac.entity.Persona;
import com.saas.master.rbac.entity.UsuarioNegocio;
import com.saas.master.rbac.repository.AccionRepository;
import com.saas.master.rbac.repository.PerfilRepository;
import com.saas.master.rbac.repository.PersonaRepository;
import com.saas.master.rbac.repository.UsuarioNegocioRepository;
import com.saas.master.tenants.entity.Negocio;
import com.saas.master.tenants.repository.NegocioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RbacService {

    private final UsuarioNegocioRepository usuarioNegocioRepo;
    private final PersonaRepository personaRepo;
    private final PerfilRepository perfilRepo;
    private final AccionRepository accionRepo;
    private final NegocioRepository negocioRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RbacService(UsuarioNegocioRepository usuarioNegocioRepo,
                       PersonaRepository personaRepo,
                       PerfilRepository perfilRepo,
                       AccionRepository accionRepo,
                       NegocioRepository negocioRepo) {
        this.usuarioNegocioRepo = usuarioNegocioRepo;
        this.personaRepo = personaRepo;
        this.perfilRepo = perfilRepo;
        this.accionRepo = accionRepo;
        this.negocioRepo = negocioRepo;
    }

    public List<Perfil> listarPerfiles(UUID negocioId) {
        return perfilRepo.findByNegocioIdOrEsSistemaTrue(negocioId);
    }

    public List<Accion> listarAcciones() {
        return accionRepo.findAll();
    }

    public List<UsuarioNegocioDTO.Response> listarUsuariosPorNegocio(UUID negocioId) {
        return usuarioNegocioRepo.findByNegocioId(negocioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UsuarioNegocioDTO.Response> listarTodosLosUsuarios() {
        return usuarioNegocioRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioNegocioDTO.Response crearUsuario(UsuarioNegocioDTO.Request request) {
        Negocio negocio;
        if (request.negocioId != null) {
            negocio = negocioRepo.findById(request.negocioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + request.negocioId));
        } else {
            // Primer negocio por defecto si no se especifica
            negocio = negocioRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new BadRequestException("No hay negocios registrados en el sistema"));
        }

        Perfil perfil = perfilRepo.findByCodigo(request.perfilCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado: " + request.perfilCodigo));

        // 1. Guardar o actualizar Persona
        Persona persona = null;
        if (request.numeroDocumento != null && !request.numeroDocumento.isBlank()) {
            persona = personaRepo.findByNumeroDocumento(request.numeroDocumento)
                    .orElse(new Persona());
            persona.setTipoDocumento(request.tipoDocumento != null ? request.tipoDocumento : "DNI");
            persona.setNumeroDocumento(request.numeroDocumento);
            persona.setNombres(request.nombres != null ? request.nombres : "Usuario");
            persona.setApellidos(request.apellidos != null ? request.apellidos : "General");
            persona.setTelefono(request.telefono);
            persona.setDireccion(request.direccion);
            persona.setEmail(request.email);
            persona.setNroColegiatura(request.nroColegiatura);
            persona = personaRepo.save(persona);
        }

        // 2. Guardar UsuarioNegocio
        UsuarioNegocio usuario = new UsuarioNegocio();
        usuario.setNegocio(negocio);
        usuario.setPerfil(perfil);
        usuario.setPersona(persona);
        usuario.setEmail(request.email);
        usuario.setPasswordHash(passwordEncoder.encode(request.password != null ? request.password : "123456"));
        usuario.setPinSeguridad(request.pinSeguridad != null ? request.pinSeguridad : "1234");
        usuario.setEstaActivo(request.estaActivo != null ? request.estaActivo : true);
        usuario.setEsMaster(request.esMaster != null ? request.esMaster : false);

        UsuarioNegocio guardado = usuarioNegocioRepo.save(usuario);
        return mapToResponse(guardado);
    }

    @Transactional
    public UsuarioNegocioDTO.Response actualizarUsuario(UUID id, UsuarioNegocioDTO.Request request) {
        UsuarioNegocio usuario = usuarioNegocioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (request.perfilCodigo != null) {
            Perfil perfil = perfilRepo.findByCodigo(request.perfilCodigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado: " + request.perfilCodigo));
            usuario.setPerfil(perfil);
        }

        if (request.email != null) usuario.setEmail(request.email);
        if (request.pinSeguridad != null) usuario.setPinSeguridad(request.pinSeguridad);
        if (request.estaActivo != null) usuario.setEstaActivo(request.estaActivo);
        if (request.esMaster != null) usuario.setEsMaster(request.esMaster);
        if (request.password != null && !request.password.isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password));
        }

        // Actualizar datos de Persona
        if (usuario.getPersona() != null) {
            Persona p = usuario.getPersona();
            if (request.nombres != null) p.setNombres(request.nombres);
            if (request.apellidos != null) p.setApellidos(request.apellidos);
            if (request.telefono != null) p.setTelefono(request.telefono);
            if (request.direccion != null) p.setDireccion(request.direccion);
            if (request.nroColegiatura != null) p.setNroColegiatura(request.nroColegiatura);
            personaRepo.save(p);
        }

        UsuarioNegocio actualizado = usuarioNegocioRepo.save(usuario);
        return mapToResponse(actualizado);
    }

    @Transactional
    public void cambiarEstado(UUID id, boolean activo) {
        UsuarioNegocio usuario = usuarioNegocioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setEstaActivo(activo);
        usuarioNegocioRepo.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        UsuarioNegocio usuario = usuarioNegocioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuarioNegocioRepo.delete(usuario);
    }

    private UsuarioNegocioDTO.Response mapToResponse(UsuarioNegocio u) {
        UsuarioNegocioDTO.Response res = new UsuarioNegocioDTO.Response();
        res.id = u.getId();
        res.negocioId = u.getNegocio() != null ? u.getNegocio().getId() : null;
        res.negocioNombre = u.getNegocio() != null ? u.getNegocio().getNombreComercial() : "Farmacia Medicare";
        res.email = u.getEmail();
        res.pinSeguridad = u.getPinSeguridad();
        res.estaActivo = u.getEstaActivo();
        res.esMaster = u.getEsMaster();

        if (u.getPerfil() != null) {
            res.perfilCodigo = u.getPerfil().getCodigo();
            res.perfilNombre = u.getPerfil().getNombre();
            res.acciones = u.getPerfil().getAcciones().stream()
                    .map(Accion::getCodigo)
                    .collect(Collectors.toList());
        }

        if (u.getPersona() != null) {
            Persona p = u.getPersona();
            res.personaId = p.getId();
            res.tipoDocumento = p.getTipoDocumento();
            res.numeroDocumento = p.getNumeroDocumento();
            res.nombres = p.getNombres();
            res.apellidos = p.getApellidos();
            res.nombreCompleto = p.getNombres() + " " + p.getApellidos();
            res.telefono = p.getTelefono();
            res.direccion = p.getDireccion();
            res.nroColegiatura = p.getNroColegiatura();
        } else {
            res.nombreCompleto = u.getEmail();
        }

        return res;
    }
}
