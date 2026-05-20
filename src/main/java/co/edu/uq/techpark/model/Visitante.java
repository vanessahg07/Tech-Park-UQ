package co.edu.uq.techpark.model;

import co.edu.uq.techpark.ds.ListaEnlazada;
import co.edu.uq.techpark.ds.ConjuntoSimple;

import java.io.Serializable;
import java.util.UUID;

/**
 * Persona registrada en el sistema que puede comprar tiquetes
 * y acceder a las atracciones del parque.
 *
 * Cumple con los Requisitos 1.1, 7.1, 7.4
 */
public class Visitante implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único del visitante. */
    private String id;

    /** Nombre completo del visitante. */
    private String nombreCompleto;

    /** Número de documento de identidad. Se usa también como nombre de usuario. */
    private String numeroDocumento;

    /** Contraseña cifrada con SHA-256. */
    private String hashContrasena;

    /** Edad del visitante en años. Se verifica contra la edad mínima de cada atracción. */
    private int edad;

    /** Estatura del visitante en centímetros. Se verifica contra la estatura mínima de cada atracción. */
    private int estaturaCm;

    /** Saldo virtual en pesos disponible para pagar costos adicionales de atracciones. */
    private double saldoVirtual;

    /** Foto del visitante como arreglo de bytes. Puede ser null si no subió foto. */
    private byte[] foto;

    /** Tiquete activo para la jornada actual. Null si no ha comprado tiquete. */
    private Tiquete tiqueteActivo;

    /** Lista cronológica de todas las atracciones que ha visitado en la jornada. */
    private ListaEnlazada<RegistroDeVisita> historialDeVisitas;

    /** Conjunto de IDs de atracciones marcadas como favoritas (sin duplicados). */
    private ConjuntoSimple<String> favoritos;

    /** Lista de notificaciones que el visitante aún no ha leído. */
    private ListaEnlazada<Notificacion> notificacionesSinLeer;

    public Visitante() {
        this.id = UUID.randomUUID().toString();
        this.historialDeVisitas = new ListaEnlazada<>();
        this.favoritos = new ConjuntoSimple<>();
        this.notificacionesSinLeer = new ListaEnlazada<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public int getEstaturaCm() { return estaturaCm; }
    public void setEstaturaCm(int estaturaCm) { this.estaturaCm = estaturaCm; }

    public double getSaldoVirtual() { return saldoVirtual; }
    public void setSaldoVirtual(double saldoVirtual) { this.saldoVirtual = saldoVirtual; }

    public byte[] getFoto() { return foto; }
    public void setFoto(byte[] foto) { this.foto = foto; }

    public Tiquete getTiqueteActivo() { return tiqueteActivo; }
    public void setTiqueteActivo(Tiquete tiqueteActivo) { this.tiqueteActivo = tiqueteActivo; }

    public ListaEnlazada<RegistroDeVisita> getHistorialDeVisitas() { return historialDeVisitas; }
    public void setHistorialDeVisitas(ListaEnlazada<RegistroDeVisita> historialDeVisitas) { this.historialDeVisitas = historialDeVisitas; }

    public ConjuntoSimple<String> getFavoritos() { return favoritos; }
    public void setFavoritos(ConjuntoSimple<String> favoritos) { this.favoritos = favoritos; }

    public ListaEnlazada<Notificacion> getNotificacionesSinLeer() { return notificacionesSinLeer; }
    public void setNotificacionesSinLeer(ListaEnlazada<Notificacion> notificacionesSinLeer) {
        this.notificacionesSinLeer = notificacionesSinLeer;
    }
}
