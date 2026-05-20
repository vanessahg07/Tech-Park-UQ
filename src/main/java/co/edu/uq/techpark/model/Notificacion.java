package co.edu.uq.techpark.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mensaje enviado a un visitante sobre cambios en el parque.
 * Por ejemplo: cierre de una atracción, inicio de un show, etc.
 */
public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificador único de la notificación. */
    private String id;

    /** Texto del mensaje que verá el visitante. */
    private String mensaje;

    /** Fecha y hora en que se creó la notificación. */
    private LocalDateTime creadaEn;

    /** Indica si el visitante ya leyó esta notificación. */
    private boolean leida;

    public Notificacion(String mensaje, LocalDateTime creadaEn) {
        this.id = UUID.randomUUID().toString();
        this.mensaje = mensaje;
        this.creadaEn = creadaEn;
        this.leida = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
