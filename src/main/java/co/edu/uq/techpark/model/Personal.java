package co.edu.uq.techpark.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Empleado del parque: puede ser Operador o Administrador.
 * La contraseña se guarda como hash SHA-256, nunca en texto plano.
 *
 * Cumple con el Requisito 9.1
 */
public class Personal implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único del empleado. */
    private String id;

    /** Nombre de usuario para iniciar sesión en el sistema. */
    private String nombreUsuario;

    /** Contraseña cifrada con SHA-256. Nunca se guarda la contraseña original. */
    private String hashContrasena;

    /** Nombre completo del empleado. */
    private String nombreCompleto;

    /** Rol del empleado: OPERADOR o ADMINISTRADOR. */
    private RolPersonal rol;

    /** Identificador de la zona asignada al operador. Null si no tiene zona. */
    private String idZonaAsignada;

    /** Indica si la cuenta del empleado está activa. Las cuentas inactivas no pueden iniciar sesión. */
    private boolean activo;

    public Personal() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    /**
     * Convierte una contraseña en texto plano a su representación SHA-256.
     * Se usa al crear o verificar contraseñas.
     */
    public static String hashContrasena(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder(hash.length * 2);
            for (byte b : hash) hexadecimal.append(String.format("%02x", b));
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible en este sistema", e);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public RolPersonal getRol() { return rol; }
    public void setRol(RolPersonal rol) { this.rol = rol; }

    public String getIdZonaAsignada() { return idZonaAsignada; }
    public void setIdZonaAsignada(String idZonaAsignada) { this.idZonaAsignada = idZonaAsignada; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
