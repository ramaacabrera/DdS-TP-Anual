package utils.DTO;

import java.util.UUID;

public class SolicitudHechosMensajeDTO {
    String type;

    public SolicitudHechosMensajeDTO(String tipo) {
        this.type = tipo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

