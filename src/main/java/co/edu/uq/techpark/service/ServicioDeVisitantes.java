package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.util.ExcepcionDelParque;

/**
 * Servicio para las operaciones de gestión de visitantes.
 * Requisitos: 1.1, 1.2, 1.3, 1.4
 */
public class ServicioDeVisitantes {

    private ServicioDeVisitantes() {}

    public static void registrar(Visitante visitante, ContextoDelParque contexto) throws ExcepcionDelParque {
        String doc = visitante.getNumeroDocumento();
        if (contexto.getVisitantes().contiene(doc)) {
            throw new ExcepcionDelParque("El documento ya está en uso: " + doc);
        }
        contexto.getVisitantes().insertar(doc, visitante);
    }

    public static void actualizarPerfil(Visitante visitante, ContextoDelParque contexto) throws ExcepcionDelParque {
        Visitante existente = contexto.getVisitantes().buscar(visitante.getNumeroDocumento());
        if (existente == null) throw new ExcepcionDelParque("Visitante no encontrado: " + visitante.getNumeroDocumento());
        existente.setNombreCompleto(visitante.getNombreCompleto());
        existente.setEdad(visitante.getEdad());
        existente.setEstaturaCm(visitante.getEstaturaCm());
        existente.setFoto(visitante.getFoto());
    }

    public static double obtenerSaldo(String numeroDocumento, ContextoDelParque contexto) throws ExcepcionDelParque {
        Visitante visitante = contexto.getVisitantes().buscar(numeroDocumento);
        if (visitante == null) throw new ExcepcionDelParque("Visitante no encontrado: " + numeroDocumento);
        return visitante.getSaldoVirtual();
    }
}
