package gestorAdministrativo.dto.Hechos;

public class AgregarEtiquetaDTO {
    private String nombre;

    // Constructor vacío necesario para la serialización
    public AgregarEtiquetaDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
