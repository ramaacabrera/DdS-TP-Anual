package cargadorEstatico.controller;

import cargadorEstatico.domain.ConversorCSV;
import cargadorEstatico.domain.fuente.TipoDeFuente;
import cargadorEstatico.dto.Hechos.FuenteDTO;
import cargadorEstatico.dto.Hechos.HechoDTO;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

public class ConexionEstaticaDrive {
    private Drive driveService;
    private final String folderId;
    private final String serviceAccountKeyPath;
    private static final String GUIA_FILE_NAME = "guia.csv";
    private String csvEncoding = StandardCharsets.UTF_8.name();

    private static ConexionEstaticaDrive instance;

    public static synchronized ConexionEstaticaDrive getInstance() throws IOException, GeneralSecurityException {
        if (instance == null) {
            Properties props = new Properties();
            try (InputStream input = ConexionEstaticaDrive.class.getClassLoader()
                    .getResourceAsStream("config/drive-config.properties")) {
                props.load(input);
            }

            instance = new ConexionEstaticaDrive(
                    props.getProperty("drive.folder.id"),
                    props.getProperty("drive.service.account.key")
            );
            instance.conectar();
        }
        return instance;
    }

    private ConexionEstaticaDrive(String folderId, String serviceAccountKeyPath) {
        this.folderId = folderId;
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }

    public void conectar() throws IOException, GeneralSecurityException {
        System.out.println("Conectando a Google Drive...");

        System.out.println("Cargando credenciales a partir de: " + serviceAccountKeyPath);

        InputStream keyStream = getClass().getClassLoader()
                .getResourceAsStream(serviceAccountKeyPath);

        System.out.println("Se cargaron las credenciales 1");

        if (keyStream == null) {
            throw new FileNotFoundException("No se encontró el archivo de credenciales: " + serviceAccountKeyPath);
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        System.out.println("Se cargaron las credenciales 2");

        driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("CargadorEstatico")
                .build();

        System.out.println("Se cargaron las credenciales 3");

        System.out.println("✓ Conectado a Google Drive");
    }

    public List<HechoDTO> obtenerHechos() {
        List<HechoDTO> todosHechos = new ArrayList<>();
        ConversorCSV conversor = new ConversorCSV();

        try {
            Set<String> archivosProcesados = cargarGuiaProcesados();
            System.out.println("Archivos ya procesados: " + archivosProcesados.size());

            List<FileInfo> archivosDisponibles = obtenerArchivosCSV();
            System.out.println("Archivos disponibles en Drive: " + archivosDisponibles.size());

            List<FileInfo> archivosNuevos = new ArrayList<>();
            for (FileInfo archivo : archivosDisponibles) {
                if (!archivosProcesados.contains(archivo.nombre) &&
                        !archivo.nombre.equals(GUIA_FILE_NAME)) {
                    archivosNuevos.add(archivo);
                }
            }

            System.out.println("Archivos nuevos por procesar: " + archivosNuevos.size());

            if (archivosNuevos.isEmpty()) {
                System.out.println("No hay archivos nuevos para procesar");
                return todosHechos;
            }

            Set<String> archivosProcesadosEnEsteCiclo = new HashSet<>();

            for (FileInfo archivo : archivosNuevos) {
                try {
                    System.out.println("Procesando: " + archivo.nombre);
                    List<HechoDTO> hechosArchivo = procesarArchivoCSV(archivo, conversor);

                    FuenteDTO fuente = new FuenteDTO(
                            null,
                            TipoDeFuente.ESTATICA.toString(),
                            archivo.nombre
                    );

                    for (HechoDTO hecho : hechosArchivo) {
                        hecho.setFuente(fuente);
                    }

                    todosHechos.addAll(hechosArchivo);
                    archivosProcesadosEnEsteCiclo.add(archivo.nombre);

                    System.out.println("  ✓ " + hechosArchivo.size() + " hechos obtenidos");

                } catch (Exception e) {
                    System.err.println("  ✗ Error procesando " + archivo.nombre + ": " + e.getMessage());
                }
            }

            if (!archivosProcesadosEnEsteCiclo.isEmpty()) {
                actualizarGuiaProcesados(archivosProcesadosEnEsteCiclo);
                System.out.println("✓ Guía actualizada con " + archivosProcesadosEnEsteCiclo.size() + " archivos nuevos");
            }

            System.out.println("✓ Total hechos obtenidos: " + todosHechos.size());

        } catch (Exception e) {
            System.err.println("✗ Error obteniendo hechos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener hechos de Google Drive", e);
        }

        return todosHechos;
    }

    private Set<String> cargarGuiaProcesados() throws IOException {
        Set<String> procesados = new HashSet<>();

        try {
            Optional<FileInfo> guia = buscarArchivoPorNombre(GUIA_FILE_NAME);

            if (!guia.isPresent()) {
                System.out.println("No se encontró guía de procesados, se creará una nueva");
                return procesados;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files().get(guia.get().id).executeMediaAndDownloadTo(outputStream);
            String contenido = outputStream.toString(StandardCharsets.UTF_8.name());

            try (BufferedReader reader = new BufferedReader(
                    new StringReader(contenido))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        procesados.add(linea);
                    }
                }
            }

            System.out.println("Guía cargada: " + procesados.size() + " archivos procesados");

        } catch (Exception e) {
            System.err.println("Error cargando guía: " + e.getMessage());
        }

        return procesados;
    }

    private void actualizarGuiaProcesados(Set<String> nuevosArchivos) throws IOException {
        System.out.println("Actualizando guia 1");

        Set<String> todosProcesados = cargarGuiaProcesados();

        System.out.println("Actualizando guia 2");

        todosProcesados.addAll(nuevosArchivos);

        System.out.println("Actualizando guia 3");

        StringBuilder contenido = new StringBuilder();
        for (String archivo : todosProcesados) {
            contenido.append(archivo).append("\n");
        }

        System.out.println("Actualizando guia 4");

        ByteArrayContent mediaContent = new ByteArrayContent(
                "text/csv",
                contenido.toString().getBytes(StandardCharsets.UTF_8)
        );

        System.out.println("Actualizando guia 5");

        Optional<FileInfo> guiaExistente = buscarArchivoPorNombre(GUIA_FILE_NAME);

        System.out.println("Actualizando guia 6");

        if (guiaExistente.isPresent()) {
            driveService.files().update(guiaExistente.get().id, null, mediaContent).execute();
            System.out.println("Guía actualizada en Drive");
        } else {
            File fileMetadata = new File();
            fileMetadata.setName(GUIA_FILE_NAME);
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setMimeType("text/csv");

            driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("Nueva guía creada en Drive");
        }
    }

    private List<FileInfo> obtenerArchivosCSV() throws IOException {
        List<FileInfo> archivos = new ArrayList<>();
        String pageToken = null;

        String query = String.format(
                "'%s' in parents and mimeType = 'text/csv' and trashed = false",
                folderId
        );

        do {
            FileList result = driveService.files().list()
                    .setQ(query)
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(100)
                    .setPageToken(pageToken)
                    .execute();

            for (File file : result.getFiles()) {
                archivos.add(new FileInfo(file.getId(), file.getName()));
            }

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return archivos;
    }

    private Optional<FileInfo> buscarArchivoPorNombre(String nombre) throws IOException {
        String query = String.format(
                "name = '%s' and '%s' in parents and trashed = false",
                nombre, folderId
        );

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name, modifiedTime)")
                .execute();

        if (!result.getFiles().isEmpty()) {
            File file = result.getFiles().get(0);
            return Optional.of(new FileInfo(file.getId(), file.getName(), file.getModifiedTime().toString()));
        }

        return Optional.empty();
    }

    private List<HechoDTO> procesarArchivoCSV(FileInfo archivo, ConversorCSV conversor)
            throws IOException {

        List<HechoDTO> hechos = new ArrayList<>();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(archivo.id).executeMediaAndDownloadTo(outputStream);
        byte[] contenido = outputStream.toByteArray();

        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(contenido), csvEncoding);
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim()
                     .parse(reader)) {

            for (CSVRecord registro : parser) {
                HechoDTO hechoDTO = conversor.mapearAHecho(registro);
                hechos.add(hechoDTO);
            }
        }

        return hechos;
    }

    private static class FileInfo {
        String id;
        String nombre;
        Date modificado;

        FileInfo(String id, String nombre, Date modificado) {
            this.id = id;
            this.nombre = nombre;
            this.modificado = modificado;
        }

        FileInfo(String id, String nombre, String fechaModificacionString) {
            this.id = id;
            this.nombre = nombre;
            this.modificado = parseGoogleDriveDate(fechaModificacionString);
        }

        FileInfo(String id, String nombre) {
            this(id, nombre, new Date());
        }

        private Date parseGoogleDriveDate(String fechaString) {
            if (fechaString == null) {
                return new Date();
            }

            try {
                java.time.Instant instant = java.time.Instant.parse(fechaString);
                return Date.from(instant);

            } catch (Exception e) {
                System.err.println("Error parseando fecha de Google Drive: " + fechaString);
                return new Date();
            }
        }
    }
}