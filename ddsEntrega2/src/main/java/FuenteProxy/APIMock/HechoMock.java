package FuenteProxy.APIMock;


import java.time.LocalDateTime;

public class HechoMock {
    private String mockId;
    private String mockTitulo;
    private String mockDescripcion;
    private String mockCategoria;
    private String mockUbicacion; // Ej: "Lat: -34.0, Lon: -58.0"
    private LocalDateTime mockFechaAcontecimiento;
    private String mockOrigen;
    private String campoExtra; // Un campo extra que solo algunos hechos tendrán


    //constructor
    public HechoMock(String mockId, String mockTitulo, String mockDescripcion,
                     String mockCategoria, String mockUbicacion, LocalDateTime mockFechaAcontecimiento,
                     String mockOrigen, String campoExtra) {
        this.mockId = mockId;
        this.mockTitulo = mockTitulo;
        this.mockDescripcion = mockDescripcion;
        this.mockCategoria = mockCategoria;
        this.mockUbicacion = mockUbicacion;
        this.mockFechaAcontecimiento = mockFechaAcontecimiento;
        this.mockOrigen = mockOrigen;
        this.campoExtra = campoExtra;

    }

    // Constructor mínimo para hechos con menos campos
    public HechoMock(String mockId, String mockTitulo, String mockDescripcion, String mockCategoria,
                     String mockUbicacion, LocalDateTime mockFechaAcontecimiento, String mockOrigen) {
        this(mockId, mockTitulo, mockDescripcion, mockCategoria, mockUbicacion, mockFechaAcontecimiento, mockOrigen, null);
    }
    //necesito un constructor vacio para jackson
    public HechoMock() {}

    // Getters y Setters
    public String getMockId() { return mockId; }
    public void setMockId(String mockId) { this.mockId = mockId; }
    public String getMockTitulo() { return mockTitulo; }
    public void setMockTitulo(String mockTitulo) { this.mockTitulo = mockTitulo; }
    public String getMockDescripcion() { return mockDescripcion; }
    public void setMockDescripcion(String mockDescripcion) { this.mockDescripcion = mockDescripcion; }
    public String getMockCategoria() { return mockCategoria; }
    public void setMockCategoria(String mockCategoria) { this.mockCategoria = mockCategoria; }
    public String getMockUbicacion() { return mockUbicacion; }
    public void setMockUbicacion(String mockUbicacion) { this.mockUbicacion = mockUbicacion; }
    public LocalDateTime getMockFechaAcontecimiento() { return mockFechaAcontecimiento; }
    public void setMockFechaAcontecimiento(LocalDateTime mockFechaAcontecimiento) { this.mockFechaAcontecimiento = mockFechaAcontecimiento; }
    public String getMockOrigen() { return mockOrigen; }
    public void setMockOrigen(String mockOrigen) { this.mockOrigen = mockOrigen; }
    public String getcampoExtra() { return campoExtra; }
    public void setcampoExtra(String campoExtra) { this.campoExtra = campoExtra; }

}


