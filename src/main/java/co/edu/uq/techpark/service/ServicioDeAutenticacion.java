package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.util.ExcepcionDeAutenticacion;

import javax.servlet.http.HttpSession;

/**
 * Gestiona la autenticación y el manejo de sesiones.
 * Se usa un mensaje genérico para evitar la enumeración de usuarios.
 * Requisitos: 12.1, 12.2
 */
public class ServicioDeAutenticacion {

    private ServicioDeAutenticacion() {}

    /**
     * Autentica un usuario. Personal: nombreUsuario + contraseña hasheada.
     * Visitante: numeroDocumento como nombre de usuario.
     */
    public static Object iniciarSesion(String nombreUsuario, String contrasena, ContextoDelParque contexto)
            throws ExcepcionDeAutenticacion {

        // Intentar con personal (búsqueda en árbol por nombreUsuario)
        Personal miembroPersonal = contexto.getPersonal().buscar(nombreUsuario);
        if (miembroPersonal != null) {
            if (!miembroPersonal.isActivo()) throw new ExcepcionDeAutenticacion("Credenciales incorrectas");
            if (Personal.hashContrasena(contrasena).equals(miembroPersonal.getHashContrasena())) return miembroPersonal;
            throw new ExcepcionDeAutenticacion("Credenciales incorrectas");
        }

        // Intentar con visitante (numeroDocumento como nombreUsuario)
        Visitante visitante = contexto.getVisitantes().buscar(nombreUsuario);
        if (visitante != null) {
            if (visitante.getHashContrasena() != null && !visitante.getHashContrasena().isEmpty()) {
                if (contrasena != null && Personal.hashContrasena(contrasena).equals(visitante.getHashContrasena())) {
                    return visitante;
                }
                throw new ExcepcionDeAutenticacion("Credenciales incorrectas");
            }
            if (contrasena != null && !contrasena.isEmpty()) return visitante;
        }

        throw new ExcepcionDeAutenticacion("Credenciales incorrectas");
    }

    public static void cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
    }
}
