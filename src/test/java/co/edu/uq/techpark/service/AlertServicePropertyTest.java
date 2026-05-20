package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.*;
import co.edu.uq.techpark.ds.ListaEnlazada;
import net.jqwik.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 13: Alerta climática cierra exactamente las atracciones del tipo afectado
 * Validates: Requirements 8.1
 */
class AlertServicePropertyTest {

    private static final Set<TipoAtraccion> AFECTADAS = EnumSet.of(
            TipoAtraccion.ACUATICA, TipoAtraccion.MECANICA_DE_ALTURA);

    private Atraccion crearActiva(TipoAtraccion tipo, int idx) {
        Atraccion a = new Atraccion();
        a.setNombre("A-" + tipo.name() + "-" + idx);
        a.setTipo(tipo);
        a.setEstado(EstadoAtraccion.ACTIVA);
        return a;
    }

    @Provide
    Arbitrary<List<TipoAtraccion>> tiposMezclados() {
        return Arbitraries.of(TipoAtraccion.values()).list().ofMinSize(1).ofMaxSize(10);
    }

    @Provide
    Arbitrary<TipoAlerta> tiposDeAlerta() {
        return Arbitraries.of(TipoAlerta.TORMENTA_ELECTRICA, TipoAlerta.LLUVIA_FUERTE);
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 13: Alerta climatica cierra exactamente las atracciones del tipo afectado")
    void alertaClimatica_cierraExactamenteTiposAfectados(
            @ForAll("tiposMezclados") List<TipoAtraccion> tipos,
            @ForAll("tiposDeAlerta") TipoAlerta tipoAlerta) {

        ContextoDelParque contexto = new ContextoDelParque();
        for (int i = 0; i < tipos.size(); i++) {
            Atraccion a = crearActiva(tipos.get(i), i);
            contexto.getAtraccionesPorId().insertar(a.getId(), a);
            contexto.getAtraccionesPorNombre().insertar(a.getNombre(), a);
        }

        ServicioDeAlertas.registrarAlerta(tipoAlerta, contexto);

        ListaEnlazada<Atraccion> todas = contexto.getAtraccionesPorId().enOrden();
        for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            if (AFECTADAS.contains(a.getTipo())) {
                assertEquals(EstadoAtraccion.CERRADA, a.getEstado());
                assertEquals("clima", a.getMotivoCierre());
            } else {
                assertEquals(EstadoAtraccion.ACTIVA, a.getEstado());
                assertNull(a.getMotivoCierre());
            }
        }
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 13: Alerta climatica cierra exactamente las atracciones del tipo afectado")
    void alertaClimatica_cierraTodasLasAfectadas(@ForAll("tiposDeAlerta") TipoAlerta tipoAlerta) {
        ContextoDelParque contexto = new ContextoDelParque();
        int i = 0;
        for (TipoAtraccion tipo : AFECTADAS) {
            Atraccion a = crearActiva(tipo, i++);
            contexto.getAtraccionesPorId().insertar(a.getId(), a);
            contexto.getAtraccionesPorNombre().insertar(a.getNombre(), a);
        }
        ServicioDeAlertas.registrarAlerta(tipoAlerta, contexto);
        ListaEnlazada<Atraccion> todas = contexto.getAtraccionesPorId().enOrden();
        for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            assertEquals(EstadoAtraccion.CERRADA, a.getEstado());
            assertEquals("clima", a.getMotivoCierre());
            assertNotNull(a.getFechaCierre());
        }
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 13: Alerta climatica cierra exactamente las atracciones del tipo afectado")
    void alertaClimatica_noAfectaOtrosTipos(@ForAll("tiposDeAlerta") TipoAlerta tipoAlerta) {
        ContextoDelParque contexto = new ContextoDelParque();
        int i = 0;
        for (TipoAtraccion tipo : EnumSet.of(TipoAtraccion.MECANICA, TipoAtraccion.ESPECTACULO, TipoAtraccion.OTRA)) {
            Atraccion a = crearActiva(tipo, i++);
            contexto.getAtraccionesPorId().insertar(a.getId(), a);
            contexto.getAtraccionesPorNombre().insertar(a.getNombre(), a);
        }
        ServicioDeAlertas.registrarAlerta(tipoAlerta, contexto);
        ListaEnlazada<Atraccion> todas = contexto.getAtraccionesPorId().enOrden();
        for (ListaEnlazada.Iterador<Atraccion> it = todas.iterador(); it.tieneSiguiente(); ) {
            Atraccion a = it.siguiente();
            assertEquals(EstadoAtraccion.ACTIVA, a.getEstado());
            assertNull(a.getMotivoCierre());
        }
    }
}
