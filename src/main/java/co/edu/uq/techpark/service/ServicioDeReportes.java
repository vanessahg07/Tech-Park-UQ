package co.edu.uq.techpark.service;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;

/**
 * Servicio para generar reportes de fin de jornada.
 * Requisitos: 11.1, 11.2, 11.3, 11.4
 */
public class ServicioDeReportes {

    public static class ReporteDeJornada {
        public final double ingresosTotales;
        public final ListaEnlazada<Atraccion> atraccionesPorVisitantes;
        public final double minutosEsperaPromedio;
        public final int cierresPorAlertaClimatica;
        public final ListaEnlazada<Atraccion> atraccionesEnMantenimiento;
        public final ListaEnlazada<Atraccion> atraccionesCerradas;
        public final boolean jornadaEnCurso;

        public ReporteDeJornada(double ingresosTotales, ListaEnlazada<Atraccion> atraccionesPorVisitantes,
                                double minutosEsperaPromedio, int cierresPorAlertaClimatica,
                                ListaEnlazada<Atraccion> atraccionesEnMantenimiento,
                                ListaEnlazada<Atraccion> atraccionesCerradas,
                                boolean jornadaEnCurso) {
            this.ingresosTotales = ingresosTotales;
            this.atraccionesPorVisitantes = atraccionesPorVisitantes;
            this.minutosEsperaPromedio = minutosEsperaPromedio;
            this.cierresPorAlertaClimatica = cierresPorAlertaClimatica;
            this.atraccionesEnMantenimiento = atraccionesEnMantenimiento;
            this.atraccionesCerradas = atraccionesCerradas;
            this.jornadaEnCurso = jornadaEnCurso;
        }
    }

    private ServicioDeReportes() {}

    public static ReporteDeJornada generarReporteDeJornada(ContextoDelParque contexto) {
        ListaEnlazada<Atraccion> todas = contexto.getAtraccionesPorId().enOrden();

        double ingresosTotales = 0.0;
        for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            ingresosTotales += a.getVisitantesAcumulados() * a.getCostoAdicional();
        }

        // Ordenar por visitantes acumulados descendente (insertion sort sobre ListaEnlazada)
        ListaEnlazada<Atraccion> ordenadas = ordenarPorVisitantesDesc(todas);

        double promedioEspera = 0.0;
        if (!todas.estaVacia()) {
            double suma = 0;
            for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
                suma += it.siguiente().getMinutosEsperaEstimados();
            }
            promedioEspera = suma / todas.tamanio();
        }

        ListaEnlazada<Atraccion> enMantenimiento = new ListaEnlazada<>();
        ListaEnlazada<Atraccion> cerradas = new ListaEnlazada<>();
        int cierresPorClima = 0;
        for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            if (EstadoAtraccion.EN_MANTENIMIENTO.equals(a.getEstado())) {
                enMantenimiento.agregarAlFinal(a);
            } else if (EstadoAtraccion.CERRADA.equals(a.getEstado())) {
                cerradas.agregarAlFinal(a);
                if ("clima".equals(a.getMotivoCierre())) cierresPorClima++;
            }
        }
        // Sumar también las alertas ya canceladas del historial
        cierresPorClima += contexto.getHistorialDeAlertas().tamanio();

        return new ReporteDeJornada(ingresosTotales, ordenadas, promedioEspera,
                cierresPorClima, enMantenimiento, cerradas, true);
    }

    /** Insertion sort descendente por visitantesAcumulados — sin java.util.Collections. */
    private static ListaEnlazada<Atraccion> ordenarPorVisitantesDesc(ListaEnlazada<Atraccion> fuente) {
        ListaEnlazada<Atraccion> ordenada = new ListaEnlazada<>();
        for (ListaEnlazada.Iterador<Atraccion> it = fuente.iterador(); it.tieneSiguiente(); ) {
            Atraccion aInsertar = it.siguiente();
            boolean insertado = false;
            ListaEnlazada<Atraccion> resultado = new ListaEnlazada<>();
            for (ListaEnlazada.Iterador<Atraccion> si = ordenada.iterador(); si.tieneSiguiente(); ) {
                Atraccion actual = si.siguiente();
                if (!insertado && aInsertar.getVisitantesAcumulados() > actual.getVisitantesAcumulados()) {
                    resultado.agregarAlFinal(aInsertar);
                    insertado = true;
                }
                resultado.agregarAlFinal(actual);
            }
            if (!insertado) resultado.agregarAlFinal(aInsertar);
            ordenada = resultado;
        }
        return ordenada;
    }

    public static String exportarComoTexto(ReporteDeJornada reporte) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("    TECH-PARK UQ - REPORTE DE JORNADA   \n");
        sb.append("========================================\n\n");
        sb.append("Estado jornada : ").append(reporte.jornadaEnCurso ? "En curso" : "Finalizada").append("\n");
        sb.append("Ingresos totales: $").append(String.format("%.2f", reporte.ingresosTotales)).append("\n");
        sb.append("Espera promedio : ").append(String.format("%.1f", reporte.minutosEsperaPromedio)).append(" min\n");
        sb.append("Cierres por alerta climática: ").append(reporte.cierresPorAlertaClimatica).append("\n\n");
        sb.append("--- Atracciones por visitantes (desc) ---\n");
        for (ListaEnlazada.Iterador<Atraccion> it = reporte.atraccionesPorVisitantes.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            sb.append(String.format("  %-30s  visitantes: %d  espera: %d min  estado: %s\n",
                    a.getNombre(), a.getVisitantesAcumulados(), a.getMinutosEsperaEstimados(), a.getEstado()));
        }
        sb.append("\n--- Atracciones en mantenimiento ---\n");
        if (reporte.atraccionesEnMantenimiento.estaVacia()) {
            sb.append("  (ninguna)\n");
        } else {
            for (ListaEnlazada.Iterador<Atraccion> it = reporte.atraccionesEnMantenimiento.iterador(); it.tieneSiguiente(); ) {
                Atraccion a = it.siguiente();
                sb.append("  - ").append(a.getNombre());
                if (a.getMotivoCierre() != null) sb.append(" (").append(a.getMotivoCierre()).append(")");
                sb.append("\n");
            }
        }
        sb.append("\n--- Atracciones cerradas ---\n");
        if (reporte.atraccionesCerradas.estaVacia()) {
            sb.append("  (ninguna)\n");
        } else {
            for (ListaEnlazada.Iterador<Atraccion> it = reporte.atraccionesCerradas.iterador(); it.tieneSiguiente(); ) {
                Atraccion a = it.siguiente();
                sb.append("  - ").append(a.getNombre());
                if (a.getMotivoCierre() != null) sb.append(" (").append(a.getMotivoCierre()).append(")");
                sb.append("\n");
            }
        }
        sb.append("\n========================================\n");
        return sb.toString();
    }
}
