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

import java.util.*;
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
        List<Persona> personas = personaRepo.findByNegocioIdOrderByNombresAsc(negocioId);
        List<UsuarioNegocio> usuariosNegocio = usuarioNegocioRepo.findByNegocioId(negocioId);

        Map<UUID, UsuarioNegocio> unPorPersona = usuariosNegocio.stream()
                .filter(un -> un.getPersona() != null)
                .collect(Collectors.toMap(un -> un.getPersona().getId(), un -> un, (a, b) -> a));

        List<UsuarioNegocioDTO.Response> result = new ArrayList<>();
        Set<UUID> personasProcesadas = new HashSet<>();

        for (Persona p : personas) {
            UsuarioNegocio un = unPorPersona.get(p.getId());
            if (un != null) {
                result.add(mapToResponse(un));
            } else {
                result.add(mapPersonaToResponse(p));
            }
            personasProcesadas.add(p.getId());
        }

        for (UsuarioNegocio un : usuariosNegocio) {
            if (un.getPersona() == null || !personasProcesadas.contains(un.getPersona().getId())) {
                result.add(mapToResponse(un));
            }
        }

        return result;
    }

    public List<UsuarioNegocioDTO.Response> listarTodosLosUsuarios() {
        List<Persona> personas = personaRepo.findAll();
        List<UsuarioNegocio> usuariosNegocio = usuarioNegocioRepo.findAll();

        Map<UUID, UsuarioNegocio> unPorPersona = usuariosNegocio.stream()
                .filter(un -> un.getPersona() != null)
                .collect(Collectors.toMap(un -> un.getPersona().getId(), un -> un, (a, b) -> a));

        List<UsuarioNegocioDTO.Response> result = new ArrayList<>();
        Set<UUID> personasProcesadas = new HashSet<>();

        for (Persona p : personas) {
            if (p.getNegocio() != null) {
                UsuarioNegocio un = unPorPersona.get(p.getId());
                if (un != null) {
                    result.add(mapToResponse(un));
                } else {
                    result.add(mapPersonaToResponse(p));
                }
                personasProcesadas.add(p.getId());
            }
        }

        for (UsuarioNegocio un : usuariosNegocio) {
            if (un.getPersona() == null || !personasProcesadas.contains(un.getPersona().getId())) {
                result.add(mapToResponse(un));
            }
        }

        return result;
    }

    @Transactional
    public UsuarioNegocioDTO.Response crearUsuario(UsuarioNegocioDTO.Request request) {
        Negocio negocio;
        if (request.negocioId != null) {
            negocio = negocioRepo.findById(request.negocioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + request.negocioId));
        } else {
            negocio = negocioRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new BadRequestException("No hay negocios registrados en el sistema"));
        }

        Perfil perfil = null;
        if (request.perfilCodigo != null && !request.perfilCodigo.isBlank()) {
            perfil = perfilRepo.findByCodigo(request.perfilCodigo).orElse(null);
        }

        // 1. Guardar o actualizar Persona
        Persona persona = null;
        if (request.numeroDocumento != null && !request.numeroDocumento.isBlank()) {
            persona = personaRepo.findByNumeroDocumento(request.numeroDocumento).orElse(new Persona());
        } else if (request.personaId != null) {
            persona = personaRepo.findById(request.personaId).orElse(new Persona());
        } else {
            persona = new Persona();
        }

        persona.setTipoDocumento(request.tipoDocumento != null ? request.tipoDocumento : "DNI");
        persona.setNumeroDocumento(request.numeroDocumento);
        persona.setNombres(request.nombres != null ? request.nombres : "Colaborador");
        persona.setApellidos(request.apellidos != null ? request.apellidos : "");
        persona.setTelefono(request.telefono);
        persona.setDireccion(request.direccion);
        persona.setEmail(request.email);
        persona.setNroColegiatura(request.nroColegiatura);
        persona.setFechanacimiento(request.fechanacimiento);
        persona.setNegocio(negocio);
        if (perfil != null) persona.setPerfil(perfil);
        persona.setEstaActivo(request.estaActivo != null ? request.estaActivo : true);
        persona = personaRepo.save(persona);

        // 2. ¿Crear cuenta de acceso en UsuarioNegocio?
        boolean crearCuenta = Boolean.TRUE.equals(request.tieneUsuario) ||
                (request.email != null && !request.email.isBlank() && request.password != null && !request.password.isBlank());

        if (crearCuenta) {
            UsuarioNegocio usuario = new UsuarioNegocio();
            usuario.setNegocio(negocio);
            usuario.setPerfil(perfil != null ? perfil : persona.getPerfil());
            usuario.setPersona(persona);
            usuario.setEmail(request.email);
            usuario.setPasswordHash(passwordEncoder.encode(request.password != null && !request.password.isBlank() ? request.password : "123456"));
            usuario.setPinSeguridad(request.pinSeguridad != null ? request.pinSeguridad : "1234");
            usuario.setEstaActivo(request.estaActivo != null ? request.estaActivo : true);
            usuario.setEsMaster(request.esMaster != null ? request.esMaster : false);

            UsuarioNegocio guardado = usuarioNegocioRepo.save(usuario);
            return mapToResponse(guardado);
        } else {
            return mapPersonaToResponse(persona);
        }
    }

    @Transactional
    public UsuarioNegocioDTO.Response actualizarUsuario(UUID id, UsuarioNegocioDTO.Request request) {
        Optional<UsuarioNegocio> unOpt = usuarioNegocioRepo.findById(id);

        if (unOpt.isPresent()) {
            UsuarioNegocio usuario = unOpt.get();

            if (request.perfilCodigo != null && !request.perfilCodigo.isBlank()) {
                Perfil perfil = perfilRepo.findByCodigo(request.perfilCodigo).orElse(null);
                if (perfil != null) {
                    usuario.setPerfil(perfil);
                    if (usuario.getPersona() != null) usuario.getPersona().setPerfil(perfil);
                }
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
                if (request.numeroDocumento != null) p.setNumeroDocumento(request.numeroDocumento);
                if (request.tipoDocumento != null) p.setTipoDocumento(request.tipoDocumento);
                if (request.telefono != null) p.setTelefono(request.telefono);
                if (request.direccion != null) p.setDireccion(request.direccion);
                if (request.nroColegiatura != null) p.setNroColegiatura(request.nroColegiatura);
                if (request.fechanacimiento != null) p.setFechanacimiento(request.fechanacimiento);
                if (request.estaActivo != null) p.setEstaActivo(request.estaActivo);
                personaRepo.save(p);
            }

            if (Boolean.FALSE.equals(request.tieneUsuario)) {
                Persona persona = usuario.getPersona();
                usuarioNegocioRepo.delete(usuario);
                return mapPersonaToResponse(persona);
            }

            UsuarioNegocio actualizado = usuarioNegocioRepo.save(usuario);
            return mapToResponse(actualizado);
        } else {
            // No existe como UsuarioNegocio, buscar como Persona
            Persona persona = personaRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado con ID: " + id));

            if (request.nombres != null) persona.setNombres(request.nombres);
            if (request.apellidos != null) persona.setApellidos(request.apellidos);
            if (request.numeroDocumento != null) persona.setNumeroDocumento(request.numeroDocumento);
            if (request.tipoDocumento != null) persona.setTipoDocumento(request.tipoDocumento);
            if (request.telefono != null) persona.setTelefono(request.telefono);
            if (request.direccion != null) persona.setDireccion(request.direccion);
            if (request.nroColegiatura != null) persona.setNroColegiatura(request.nroColegiatura);
            if (request.fechanacimiento != null) persona.setFechanacimiento(request.fechanacimiento);
            if (request.estaActivo != null) persona.setEstaActivo(request.estaActivo);

            if (request.perfilCodigo != null && !request.perfilCodigo.isBlank()) {
                Perfil perfil = perfilRepo.findByCodigo(request.perfilCodigo).orElse(null);
                if (perfil != null) persona.setPerfil(perfil);
            }

            persona = personaRepo.save(persona);

            // ¿Se solicitó crear cuenta de usuario en la edición?
            boolean crearCuenta = Boolean.TRUE.equals(request.tieneUsuario) ||
                    (request.email != null && !request.email.isBlank() && ((request.password != null && !request.password.isBlank()) || Boolean.TRUE.equals(request.tieneUsuario)));

            if (crearCuenta) {
                UsuarioNegocio nuevoUsuario = new UsuarioNegocio();
                nuevoUsuario.setPersona(persona);
                nuevoUsuario.setNegocio(persona.getNegocio() != null ? persona.getNegocio() :
                        (request.negocioId != null ? negocioRepo.findById(request.negocioId).orElse(null) : null));
                nuevoUsuario.setPerfil(persona.getPerfil());
                nuevoUsuario.setEmail(request.email != null && !request.email.isBlank() ? request.email : persona.getEmail());
                nuevoUsuario.setPasswordHash(passwordEncoder.encode(request.password != null && !request.password.isBlank() ? request.password : "123456"));
                nuevoUsuario.setPinSeguridad(request.pinSeguridad != null ? request.pinSeguridad : "1234");
                nuevoUsuario.setEstaActivo(request.estaActivo != null ? request.estaActivo : true);
                nuevoUsuario.setEsMaster(request.esMaster != null ? request.esMaster : false);

                UsuarioNegocio guardado = usuarioNegocioRepo.save(nuevoUsuario);
                return mapToResponse(guardado);
            } else {
                return mapPersonaToResponse(persona);
            }
        }
    }

    @Transactional
    public void cambiarEstado(UUID id, boolean activo) {
        Optional<UsuarioNegocio> unOpt = usuarioNegocioRepo.findById(id);
        if (unOpt.isPresent()) {
            UsuarioNegocio un = unOpt.get();
            un.setEstaActivo(activo);
            if (un.getPersona() != null) {
                un.getPersona().setEstaActivo(activo);
                personaRepo.save(un.getPersona());
            }
            usuarioNegocioRepo.save(un);
        } else {
            Persona p = personaRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado con ID: " + id));
            p.setEstaActivo(activo);
            personaRepo.save(p);
        }
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        Optional<UsuarioNegocio> unOpt = usuarioNegocioRepo.findById(id);
        if (unOpt.isPresent()) {
            usuarioNegocioRepo.delete(unOpt.get());
        } else {
            Persona p = personaRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador no encontrado con ID: " + id));
            personaRepo.delete(p);
        }
    }

    private UsuarioNegocioDTO.Response mapToResponse(UsuarioNegocio u) {
        UsuarioNegocioDTO.Response res = new UsuarioNegocioDTO.Response();
        res.id = u.getId();
        res.usuarioId = u.getId();
        res.tieneUsuario = true;
        res.negocioId = u.getNegocio() != null ? u.getNegocio().getId() : null;
        res.negocioNombre = u.getNegocio() != null ? u.getNegocio().getNombreComercial() : "Negocio";
        res.email = u.getEmail();
        res.pinSeguridad = u.getPinSeguridad();
        res.estaActivo = u.getEstaActivo();
        res.esMaster = u.getEsMaster();

        Perfil perfil = u.getPerfil() != null ? u.getPerfil() : (u.getPersona() != null ? u.getPersona().getPerfil() : null);
        if (perfil != null) {
            res.perfilCodigo = perfil.getCodigo();
            res.perfilNombre = perfil.getNombre();
            if (perfil.getAcciones() != null) {
                res.acciones = perfil.getAcciones().stream()
                        .map(Accion::getCodigo)
                        .collect(Collectors.toList());
            }
        }

        if (u.getPersona() != null) {
            Persona p = u.getPersona();
            res.personaId = p.getId();
            res.tipoDocumento = p.getTipoDocumento();
            res.numeroDocumento = p.getNumeroDocumento();
            res.nombres = p.getNombres();
            res.apellidos = p.getApellidos();
            res.nombreCompleto = (p.getNombres() != null ? p.getNombres() : "") + " " + (p.getApellidos() != null ? p.getApellidos() : "");
            res.telefono = p.getTelefono();
            res.direccion = p.getDireccion();
            res.nroColegiatura = p.getNroColegiatura();
            res.fechanacimiento = p.getFechanacimiento();
        } else {
            res.nombreCompleto = u.getEmail();
        }

        return res;
    }

    private UsuarioNegocioDTO.Response mapPersonaToResponse(Persona p) {
        UsuarioNegocioDTO.Response res = new UsuarioNegocioDTO.Response();
        res.id = p.getId();
        res.usuarioId = null;
        res.personaId = p.getId();
        res.tieneUsuario = false;
        res.negocioId = p.getNegocio() != null ? p.getNegocio().getId() : null;
        res.negocioNombre = p.getNegocio() != null ? p.getNegocio().getNombreComercial() : "Negocio";
        res.email = p.getEmail();
        res.pinSeguridad = null;
        res.estaActivo = p.getEstaActivo() != null ? p.getEstaActivo() : true;
        res.esMaster = false;

        if (p.getPerfil() != null) {
            res.perfilCodigo = p.getPerfil().getCodigo();
            res.perfilNombre = p.getPerfil().getNombre();
            if (p.getPerfil().getAcciones() != null) {
                res.acciones = p.getPerfil().getAcciones().stream()
                        .map(Accion::getCodigo)
                        .collect(Collectors.toList());
            }
        }

        res.tipoDocumento = p.getTipoDocumento();
        res.numeroDocumento = p.getNumeroDocumento();
        res.nombres = p.getNombres();
        res.apellidos = p.getApellidos();
        res.nombreCompleto = (p.getNombres() != null ? p.getNombres() : "") + " " + (p.getApellidos() != null ? p.getApellidos() : "");
        res.telefono = p.getTelefono();
        res.direccion = p.getDireccion();
        res.nroColegiatura = p.getNroColegiatura();
        res.fechanacimiento = p.getFechanacimiento();

        return res;
    }
}
