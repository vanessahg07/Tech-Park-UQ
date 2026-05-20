package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.Zona;
import co.edu.uq.techpark.util.ExcepcionDelParque;

/**
 * Servicio para gestionar los miembros del personal del parque.
 * Requisitos: 9.2, 9.3, 9.6
 */
public class ServicioDePersonal {

    private ServicioDePersonal() {}

    public static void crear(Personal miembro, ContextoDelParque contexto) throws ExcepcionDelParque {
        if (contexto.getPersonal().contiene(miembro.getNombreUsuario()))
            throw new ExcepcionDelParque("El nombre de usuario ya existe: " + miembro.getNombreUsuario());
        contexto.getPersonal().insertar(miembro.getNombreUsuario(), miembro);
    }

    public static void actualizar(Personal miembro, ContextoDelParque contexto) throws ExcepcionDelParque {
        Personal existente = contexto.getPersonal().buscar(miembro.getNombreUsuario());
        if (existente == null) throw new ExcepcionDelParque("Personal no encontrado: " + miembro.getNombreUsuario());
        existente.setNombreCompleto(miembro.getNombreCompleto());
        existente.setRol(miembro.getRol());
        existente.setIdZonaAsignada(miembro.getIdZonaAsignada());
    }

    public static void desactivar(String nombreUsuario, ContextoDelParque contexto) throws ExcepcionDelParque {
        Personal existente = contexto.getPersonal().buscar(nombreUsuario);
        if (existente == null) throw new ExcepcionDelParque("Personal no encontrado: " + nombreUsuario);
        existente.setActivo(false);
    }

    public static void asignarAZona(Personal miembro, Zona zona, ContextoDelParque contexto) throws ExcepcionDelParque {
        Personal miembroExistente = contexto.getPersonal().buscar(miembro.getNombreUsuario());
        if (miembroExistente == null) throw new ExcepcionDelParque("Personal no encontrado: " + miembro.getNombreUsuario());
        Zona zonaExistente = contexto.getZonas().buscar(zona.getId());
        if (zonaExistente == null) throw new ExcepcionDelParque("Zona no encontrada: " + zona.getId());
        miembroExistente.setIdZonaAsignada(zonaExistente.getId());
        zonaExistente.getOperadores().agregarAlFinal(miembroExistente);
    }

    public static ListaEnlazada<Personal> obtenerOperadoresPorZona(String idZona, ContextoDelParque contexto) throws ExcepcionDelParque {
        Zona zona = contexto.getZonas().buscar(idZona);
        if (zona == null) throw new ExcepcionDelParque("Zona no encontrada: " + idZona);
        return zona.getOperadores();
    }
}
