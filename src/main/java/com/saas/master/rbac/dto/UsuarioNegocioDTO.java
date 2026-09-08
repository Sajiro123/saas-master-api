package com.saas.master.rbac.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UsuarioNegocioDTO {

    public static class Request {
        public UUID id;
        public UUID personaId;
        public UUID negocioId;
        public String email;
        public String password;
        public String pinSeguridad;
        public String perfilCodigo; // ADMIN_NEGOCIO, QUIMICO_FARMACEUTICO, CAJERO_VENDEDOR, ADMIN_RESTAURANTE, etc.
        public Boolean estaActivo = true;
        public Boolean esMaster = false;
        public Boolean tieneUsuario = false;

        // Datos Personales
        public String tipoDocumento = "DNI";
        public String numeroDocumento;
        public String nombres;
        public String apellidos;
        public String telefono;
        public String direccion;
        public String nroColegiatura; // CQFP / CMP
        public LocalDate fechanacimiento;
    }

    public static class Response {
        public UUID id;
        public UUID usuarioId;
        public UUID personaId;
        public Boolean tieneUsuario;
        public UUID negocioId;
        public String negocioNombre;
        public String email;
        public String pinSeguridad;
        public Boolean estaActivo;
        public Boolean esMaster;
        public String perfilCodigo;
        public String perfilNombre;
        public List<String> acciones;

        // Datos Personales
        public String tipoDocumento;
        public String numeroDocumento;
        public String nombres;
        public String apellidos;
        public String nombreCompleto;
        public String telefono;
        public String direccion;
        public String nroColegiatura;
        public LocalDate fechanacimiento;
    }
}
