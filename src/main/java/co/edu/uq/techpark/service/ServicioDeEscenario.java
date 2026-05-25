package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.*;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Carga un escenario de prueba desde un archivo de texto plano hacia un ContextoDelParque.
 *
 * Formato del archivo:
 *   [ZONES]       id|nombre|aforoMaximo
 *   [ATTRACTIONS] id|nombre|tipo|capacidadMaximaPorCiclo|estaturaMinimaEnCm|edadMinima|idZona
 *   [EDGES]       idDesde|idHasta|pesoEnMetros
 *   [STAFF]       nombreUsuario|contrasena|nombreCompleto|rol|idZona
 *   [VISITORS]    nombreCompleto|numeroDocumento|edad|estaturaCm|saldoVirtual
 *
 * Requisitos: 13.2, 13.3, 13.4
 */
public class ServicioDeEscenario {

    public static class ResultadoDeEscenario {
        public final int zonas;
        public final int atracciones;
        public final int personal;
        public final int visitantes;

        public ResultadoDeEscenario(int zonas, int atracciones, int personal, int visitantes) {
            this.zonas = zonas;
            this.atracciones = atracciones;
            this.personal = personal;
            this.visitantes = visitantes;
        }
    }

    private enum Seccion { NINGUNA, ZONAS, ATRACCIONES, ARISTAS, PERSONAL, VISITANTES }

    public static ResultadoDeEscenario cargar(InputStream entrada, ContextoDelParque contexto) throws ExcepcionDelParque {
        ContextoDelParque copia = new ContextoDelParque();
        copia.setAforoMaximoDelParque(contexto.getAforoMaximoDelParque());
        copia.setFechaJornadaActual(contexto.getFechaJornadaActual());

        int numeroLinea = 0;
        int contadorZonas = 0, contadorAtracciones = 0, contadorPersonal = 0, contadorVisitantes = 0;
        Seccion seccionActual = Seccion.NINGUNA;

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String recortada = linea.trim();
                if (recortada.isEmpty() || recortada.startsWith("#")) continue;

                if (recortada.startsWith("[")) {
                    switch (recortada.toUpperCase()) {
                        case "[ZONES]":       seccionActual = Seccion.ZONAS;       break;
                        case "[ATTRACTIONS]": seccionActual = Seccion.ATRACCIONES; break;
                        case "[EDGES]":       seccionActual = Seccion.ARISTAS;     break;
                        case "[STAFF]":       seccionActual = Seccion.PERSONAL;    break;
                        case "[VISITORS]":    seccionActual = Seccion.VISITANTES;  break;
                        default:
                            throw new ExcepcionDelParque("Error en línea " + numeroLinea + ": sección desconocida '" + recortada + "'");
                    }
                    continue;
                }

                switch (seccionActual) {
                    case ZONAS:       parsearZona(recortada, numeroLinea, copia);       contadorZonas++;       break;
                    case ATRACCIONES: parsearAtraccion(recortada, numeroLinea, copia);  contadorAtracciones++; break;
                    case ARISTAS:     parsearArista(recortada, numeroLinea, copia);                            break;
                    case PERSONAL:    parsearPersonal(recortada, numeroLinea, copia);   contadorPersonal++;    break;
                    case VISITANTES:  parsearVisitante(recortada, numeroLinea, copia);  contadorVisitantes++;  break;
                    default:
                        throw new ExcepcionDelParque("Error en línea " + numeroLinea + ": datos fuera de sección");
                }
            }
        } catch (ExcepcionDelParque e) {
            throw e;
        } catch (Exception e) {
            throw new ExcepcionDelParque("Error en línea " + numeroLinea + ": " + e.getMessage(), e);
        }

        // Confirmar en el contexto real solo si el parseo fue exitoso
        contexto.setZonas(copia.getZonas());
        contexto.setAtraccionesPorId(copia.getAtraccionesPorId());
        contexto.setAtraccionesPorNombre(copia.getAtraccionesPorNombre());
        contexto.setGrafoDelParque(copia.getGrafoDelParque());
        contexto.setPersonal(copia.getPersonal());
        contexto.setVisitantes(copia.getVisitantes());

        return new ResultadoDeEscenario(contadorZonas, contadorAtracciones, contadorPersonal, contadorVisitantes);
    }

    private static void parsearZona(String linea, int nl, ContextoDelParque ctx) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 3) throw new ExcepcionDelParque("Error en línea " + nl + ": formato de zona inválido (id|nombre|aforoMaximo)");
        Zona zona = new Zona();
        zona.setId(p[0].trim());
        zona.setNombre(p[1].trim());
        try { zona.setAforoMaximo(Integer.parseInt(p[2].trim())); }
        catch (NumberFormatException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": aforoMaximo no es entero"); }
        ctx.getZonas().insertar(zona.getId(), zona);
    }

    private static void parsearAtraccion(String linea, int nl, ContextoDelParque ctx) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 7) throw new ExcepcionDelParque("Error en línea " + nl + ": formato de atracción inválido");
        Atraccion a = new Atraccion();
        a.setId(p[0].trim());
        a.setNombre(p[1].trim());
        try { a.setTipo(TipoAtraccion.valueOf(p[2].trim().toUpperCase())); }
        catch (IllegalArgumentException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": tipo de atracción desconocido '" + p[2].trim() + "'"); }
        try {
            a.setCapacidadMaximaPorCiclo(Integer.parseInt(p[3].trim()));
            a.setEstaturaMinimaEnCm(Integer.parseInt(p[4].trim()));
            a.setEdadMinima(Integer.parseInt(p[5].trim()));
        } catch (NumberFormatException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": valor numérico inválido en atracción"); }
        a.setIdZona(p[6].trim());
        ctx.getAtraccionesPorId().insertar(a.getId(), a);
        ctx.getAtraccionesPorNombre().insertar(a.getNombre(), a);
        ctx.getGrafoDelParque().agregarNodo(a.getId());
        Zona zona = ctx.getZonas().buscar(a.getIdZona());
        if (zona != null) zona.getIdsDeAtracciones().agregarAlFinal(a.getId());
    }

    private static void parsearPersonal(String linea, int nl, ContextoDelParque ctx) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 5) throw new ExcepcionDelParque("Error en línea " + nl + ": formato de personal inválido");
        Personal s = new Personal();
        s.setNombreUsuario(p[0].trim());
        s.setHashContrasena(Personal.hashContrasena(p[1].trim()));
        s.setNombreCompleto(p[2].trim());
        try { s.setRol(RolPersonal.valueOf(p[3].trim().toUpperCase())); }
        catch (IllegalArgumentException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": rol desconocido '" + p[3].trim() + "'"); }
        String idZona = p[4].trim();
        s.setIdZonaAsignada(idZona.isEmpty() ? null : idZona);
        ctx.getPersonal().insertar(s.getNombreUsuario(), s);
    }

    private static void parsearArista(String linea, int nl, ContextoDelParque ctx) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 3) throw new ExcepcionDelParque("Error en línea " + nl + ": formato de arista inválido (idDesde|idHasta|pesoEnMetros)");
        String idDesde = p[0].trim();
        String idHasta = p[1].trim();
        int peso;
        try { peso = Integer.parseInt(p[2].trim()); }
        catch (NumberFormatException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": peso de arista no es entero"); }
        ctx.getGrafoDelParque().agregarArista(idDesde, idHasta, peso);
    }

    private static void parsearVisitante(String linea, int nl, ContextoDelParque ctx) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 5) throw new ExcepcionDelParque("Error en línea " + nl + ": formato de visitante inválido");
        Visitante v = new Visitante();
        v.setNombreCompleto(p[0].trim());
        v.setNumeroDocumento(p[1].trim());
        try {
            v.setEdad(Integer.parseInt(p[2].trim()));
            v.setEstaturaCm(Integer.parseInt(p[3].trim()));
            v.setSaldoVirtual(Double.parseDouble(p[4].trim()));
        } catch (NumberFormatException e) { throw new ExcepcionDelParque("Error en línea " + nl + ": valor numérico inválido en visitante"); }
        ctx.getVisitantes().insertar(v.getNumeroDocumento(), v);
    }
}
