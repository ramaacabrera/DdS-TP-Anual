package cargadorMetamapa.dto.ModelosMensajesDTO;

import java.util.UUID;

public class IdCargadorPayload {
    public UUID idCargador;

    public IdCargadorPayload() {};

    public IdCargadorPayload(UUID id) {
        this.idCargador = id;
    }

    public UUID getIdCargador() {
        return idCargador;
    }

    public void setIdCargador(UUID idCargador) {};
}
