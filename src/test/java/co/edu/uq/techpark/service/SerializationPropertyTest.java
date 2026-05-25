package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.Atraccion;
import co.edu.uq.techpark.model.EstadoAtraccion;
import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Visitante;
import net.jqwik.api.*;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property 4: Round-trip de serialización — Validates: Requirements 16.4
 */
@Tag("Feature: tech-park-uq, Property 4: Round-trip de serializacion")
class SerializationPropertyTest {

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T original) throws IOException, ClassNotFoundException {
        File tmp = File.createTempFile("techpark-serial-", ".dat");
        tmp.deleteOnExit();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp))) {
            oos.writeObject(original);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tmp))) {
            return (T) ois.readObject();
        }
    }

    @Property(tries = 100)
    void visitante_roundTrip_preservaAtributos(
            @ForAll String nombreCompleto,
            @ForAll String numeroDocumento,
            @ForAll @IntRange(min = 1, max = 120) int edad,
            @ForAll @IntRange(min = 50, max = 250) int estaturaCm,
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double saldoVirtual
    ) throws IOException, ClassNotFoundException {

        Visitante original = new Visitante();
        original.setNombreCompleto(nombreCompleto);
        original.setNumeroDocumento(numeroDocumento);
        original.setEdad(edad);
        original.setEstaturaCm(estaturaCm);
        original.setSaldoVirtual(saldoVirtual);

        Visitante deserializado = roundTrip(original);

        assertEquals(original.getNombreCompleto(), deserializado.getNombreCompleto());
        assertEquals(original.getNumeroDocumento(), deserializado.getNumeroDocumento());
        assertEquals(original.getEdad(), deserializado.getEdad());
        assertEquals(original.getEstaturaCm(), deserializado.getEstaturaCm());
        assertEquals(original.getSaldoVirtual(), deserializado.getSaldoVirtual());
    }

    @Property(tries = 100)
    void atraccion_roundTrip_preservaAtributos(
            @ForAll String nombre,
            @ForAll @IntRange(min = 0, max = 250) int estaturaMinCm,
            @ForAll @IntRange(min = 0, max = 120) int edadMin,
            @ForAll EstadoAtraccion estado
    ) throws IOException, ClassNotFoundException {

        Atraccion original = new Atraccion();
        original.setNombre(nombre);
        original.setEstaturaMinimaEnCm(estaturaMinCm);
        original.setEdadMinima(edadMin);
        original.setEstado(estado);

        Atraccion deserializado = roundTrip(original);

        assertEquals(original.getNombre(), deserializado.getNombre());
        assertEquals(original.getEstaturaMinimaEnCm(), deserializado.getEstaturaMinimaEnCm());
        assertEquals(original.getEdadMinima(), deserializado.getEdadMinima());
        assertEquals(original.getEstado(), deserializado.getEstado());
    }

    @Property(tries = 100)
    void contextoPark_roundTrip_preservaAtributos(
            @ForAll @IntRange(min = 1, max = 10_000) int aforoMaximo,
            @ForAll @IntRange(min = 0, max = 10_000) int visitantesActuales
    ) throws IOException, ClassNotFoundException {

        ContextoDelParque original = new ContextoDelParque();
        original.setAforoMaximoDelParque(aforoMaximo);
        original.setVisitantesActualesEnElParque(visitantesActuales);

        ContextoDelParque deserializado = roundTrip(original);

        assertEquals(original.getAforoMaximoDelParque(), deserializado.getAforoMaximoDelParque());
        assertEquals(original.getVisitantesActualesEnElParque(), deserializado.getVisitantesActualesEnElParque());
    }
}
