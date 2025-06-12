package org.example;

public class FuenteDinamica extends Fuente{

    public FuenteDinamica(Conexion conexion) {
        super(conexion);
    }

    app.post("/api/hechos", new PostHechoHandler());

    public void crearSolicitudDeModificacion(Hecho hecho, String modificacion) {
    //HAY QUE VALIDAR QUE EL HECHO SEA EDITABLE (si la persona está registrada) Y QUE SEA DENTRO DE 7 DIAS
        //SI SE PUEDE, CAMBIAR EL CONTENIDO A TRAVÉS DE SOLICITUD?
    }

}
