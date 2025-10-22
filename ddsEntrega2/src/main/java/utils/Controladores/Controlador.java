package utils.Controladores;

import utils.DTO.HechoDTO;

import java.util.List;
import java.util.UUID;

abstract public class Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract void obtenerSolicitudes();

    public abstract void guardarId(UUID id);



}
