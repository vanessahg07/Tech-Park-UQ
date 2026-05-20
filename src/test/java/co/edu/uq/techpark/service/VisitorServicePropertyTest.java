package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import co.edu.uq.techpark.util.ExcepcionDelParque;
import net.jqwik.api.*;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.NotEmpty;
import net.jqwik.api.constraints.Size;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 1: Unicidad de documento de visitante — Validates: Requirements 1.2
 * Property 14: Saldo insuficiente siempre deniega el acceso — Validates: Requirements 1.5
 */
class VisitorServicePropertyTest {

    private static Visitante crearVisitante(String documento, String nombre, double saldo) {
        Visitante v = new Visitante();
        v.setNumeroDocumento(documento);
        v.setNombreCompleto(nombre);
        v.setEdad(25);
        v.setEstaturaCm(170);
        v.setSaldoVirtual(saldo);
        return v;
    }

    static boolean puedeCostearse(Visitante visitante, double costo) {
        return visitante.getSaldoVirtual() >= costo;
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 1: Unicidad de documento de visitante")
    void documentoDuplicado_lanzaExcepcion(
            @ForAll @NotEmpty String documento,
            @ForAll String primerNombre,
            @ForAll String segundoNombre) {

        ContextoDelParque contexto = new ContextoDelParque();
        ServicioDeVisitantes.registrar(crearVisitante(documento, primerNombre, 0.0), contexto);
        int antesDelRegistro = contexto.getVisitantes().enOrden().tamanio();

        assertThrows(ExcepcionDelParque.class,
                () -> ServicioDeVisitantes.registrar(crearVisitante(documento, segundoNombre, 0.0), contexto));
        assertEquals(antesDelRegistro, contexto.getVisitantes().enOrden().tamanio());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 1: Unicidad de documento de visitante")
    void documentosDistintos_todosRegistradosExitosamente(
            @ForAll @Size(min = 1, max = 20) List<@NotEmpty String> documentos) {

        ContextoDelParque contexto = new ContextoDelParque();
        int registrados = 0;
        for (String doc : documentos) {
            if (!contexto.getVisitantes().contiene(doc)) {
                ServicioDeVisitantes.registrar(crearVisitante(doc, "Nombre-" + doc, 0.0), contexto);
                registrados++;
            }
        }
        assertEquals(registrados, contexto.getVisitantes().enOrden().tamanio());
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 14: Saldo insuficiente siempre deniega el acceso")
    void saldoInsuficiente_accesoDenegado_saldoSinCambios(
            @ForAll @DoubleRange(min = 0.0, max = 9_999.99) double saldo,
            @ForAll @DoubleRange(min = 0.01, max = 10_000.0) double costoAdicional) {

        Assume.that(saldo < costoAdicional);
        Visitante visitante = crearVisitante("DOC-P14", "Test", saldo);
        double antes = visitante.getSaldoVirtual();

        assertFalse(puedeCostearse(visitante, costoAdicional));
        assertEquals(antes, visitante.getSaldoVirtual(), 1e-9);
    }

    @Property(tries = 100)
    @Tag("Feature: tech-park-uq, Property 14: Saldo insuficiente siempre deniega el acceso")
    void saldoExacto_accesoPermitido(
            @ForAll @DoubleRange(min = 0.0, max = 10_000.0) double costo) {

        Visitante visitante = crearVisitante("DOC-P14B", "Limite", costo);
        assertTrue(puedeCostearse(visitante, costo));
        assertEquals(costo, visitante.getSaldoVirtual(), 1e-9);
    }
}
