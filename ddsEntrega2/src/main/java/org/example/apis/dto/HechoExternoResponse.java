package org.example.apis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; //util si los nombres de JSON son diferentes
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora propiedades JSON que no estén en esta clase
public class HechoExternoResponse {

}
