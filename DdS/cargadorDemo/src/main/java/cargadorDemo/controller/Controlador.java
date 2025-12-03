package cargadorDemo.controller;

import cargadorDemo.dto.HechoDTO;

import java.util.List;
import java.util.UUID;

abstract public interface Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract void guardarId(UUID id);

}
