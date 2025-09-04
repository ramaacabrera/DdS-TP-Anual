package CargadorProxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import utils.DTO.HechoDTO;

import java.io.InputStream;
//import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class ConexionMetaMapa extends ConexionProxy{

    public ConexionMetaMapa(String url) {this.url = url;}

    @Override
    public List<HechoDTO> obtenerHechos() {
        URL urlHecho;
        try {
            urlHecho = new URL(url + "/hecho");
            return this.conseguirHechos(urlHecho);
        } catch (Exception e) {
            System.out.println("Error al crear url " + e.getMessage());
        }
        return null;
    }

    /*
    public List<HechoDTO> obtenerHechos(Coleccion coleccion){
        URL urlColeccion;
        try {
            urlColeccion = new URL (url + "/coleccion/" + coleccion.getHandle() + "/hechos");
            return this.conseguirHechos(urlColeccion);
        } catch (Exception e){
            System.out.println("Error al crear url " + e.getMessage());
        }
        return null;
    }
     */

    public List<HechoDTO> conseguirHechos(URL urlHecho){
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) urlHecho.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");

            InputStream input = urlConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(input, new TypeReference<List<HechoDTO>>() {
            });
        } catch (Exception e){
            System.out.println("Error al conseguir hechos " + e.getMessage());
            return null;
        }
    }

//    public void crearSolicitudEliminacion(HechoDTO hechoDTO, String justificacion){
//        URL urlSolicitudEliminacion;
//        try{
//            urlSolicitudEliminacion = new URL(url + "/solicitudes");
//            HttpURLConnection urlConnection = (HttpURLConnection) urlSolicitudEliminacion.openConnection();
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setRequestProperty("Content-Type", "application/json");
//            urlConnection.setDoOutput(true);
//
//            Map<String,Object> parametros = new HashMap<>();
//            parametros.put("hechoDTO", hechoDTO);
//            parametros.put("justificacion", justificacion);
//
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(parametros);
//
//            OutputStream output = urlConnection.getOutputStream();
//            output.write(json.getBytes());
//            output.flush();
//            output.close();
//
//            urlConnection.disconnect();
//        }
//        catch (Exception e){
//            System.out.println("Error al crear solicitud " + e.getMessage());
//        }
//    }

}
