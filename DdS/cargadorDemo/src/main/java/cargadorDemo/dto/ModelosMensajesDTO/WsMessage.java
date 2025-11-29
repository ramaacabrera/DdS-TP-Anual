package cargadorDemo.dto.ModelosMensajesDTO;

public class WsMessage<T> {
    public String type;          // p.ej. "idCargador", "obtenerHechos", "hechosObtenidos"
    public T payload;

    public WsMessage(String type, T payload) {
        this.type = type;
        this.payload = payload;
    }
}

