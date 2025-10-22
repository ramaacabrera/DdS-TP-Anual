package utils.DTO.ModelosMensajesDTO;

public class WsMessage<T> {
    public String type;          // p.ej. "idCargador", "obtenerHechos", "hechosObtenidos"
    public String correlationId; // opcional, para request/response
    public T payload;
}

