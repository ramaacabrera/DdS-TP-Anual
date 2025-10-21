package utils.DTO;

import io.javalin.websocket.WsContext;

import java.util.UUID;

public class IdMensajeDTO {
    String type;
    UUID idFuente;

    public IdMensajeDTO(String tipo, UUID id) {
        this.type = tipo;
        this.idFuente = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getIdFuente() {
        return idFuente;
    }

    public void setIdFuente(UUID idFuente) {
        this.idFuente = idFuente;
    }
}
