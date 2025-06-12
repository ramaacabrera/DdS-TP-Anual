package org.example;

import java.util.ArrayList;
import java.util.List;

public class SolicitudRepositorio {
    private List<Solicitud> solicitudes = new ArrayList<Solicitud>();

    public void guardar(Solicitud solicitud){
        solicitudes.add(solicitud);
    }

    public void eliminar(Solicitud solicitud){
        solicitudes.remove(solicitud);
    }

    public void actualizar(Solicitud solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

}
