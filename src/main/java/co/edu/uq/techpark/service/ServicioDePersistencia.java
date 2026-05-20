package co.edu.uq.techpark.service;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.util.ExcepcionDelParque;

import java.io.*;

/**
 * Gestiona la serialización y deserialización del ContextoDelParque hacia/desde disco.
 * Requisitos: 16.1, 16.2, 16.3
 */
public class ServicioDePersistencia {

    private ServicioDePersistencia() {}

    public static void guardar(ContextoDelParque contexto, String rutaArchivo) throws ExcepcionDelParque {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(contexto);
        } catch (IOException e) {
            throw new ExcepcionDelParque("Error al guardar el estado del parque en '" + rutaArchivo + "': " + e.getMessage(), e);
        }
    }

    public static ContextoDelParque cargar(String rutaArchivo) throws ExcepcionDelParque {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ContextoDelParque) ois.readObject();
        } catch (InvalidClassException | ClassNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new ExcepcionDelParque("Error al cargar el estado del parque desde '" + rutaArchivo + "': " + e.getMessage(), e);
        }
    }
}
