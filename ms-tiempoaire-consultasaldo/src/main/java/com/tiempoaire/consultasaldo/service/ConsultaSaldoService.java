package com.tiempoaire.consultasaldo.service;
import com.tiempoaire.consultasaldo.model.*;
import com.tiempoaire.consultasaldo.util.CloudWatchLogger;
//import com.tiempoaire.consultasaldo.util.MsUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import com.tiempoaire.consultasaldo.util.MsUtils;
import com.tiempoaire.consultasaldo.util.Tupla;
import com.tiempoaire.consultasaldo.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

// Usa ClassPathResource de Spring para archivos en resources
import org.springframework.core.io.ClassPathResource;

@Service
public class ConsultaSaldoService {

    @Autowired
    private DatabaseService databaseService;


    @Autowired
    private Validator validator;


    @Autowired
    private MsUtils msUtils;

    @Autowired
    private CloudWatchLogger cloudWatchLogger;

    //@Transactional
    public ConsultaSaldoResponse consultarSaldo(ConsultaSaldoRequest request, Metadata metadata) {
        Instant startTime = Instant.now();
        ConsultaSaldoResponse response = new ConsultaSaldoResponse();

        try {
            cloudWatchLogger.logInfo("********** COMENZANDO SERVICIO DE CONSULTA_SALDO *********", startTime);

            //********************************************************************
            // 1. Obtener Rules
            ObjectMapper mapper = new ObjectMapper();

            // La ruta debe ser relativa a la carpeta 'resources'
            ClassPathResource resource = new ClassPathResource("file/rulesConsultaSaldo.json");

            // Leemos el stream directamente
            JsonNode json = mapper.readTree(resource.getInputStream());


            //********************************************************************
            // 2. Validar Metadata
            String resultadoValidacion = msUtils.extractMetadata(metadata,"");

            //Enviando al Log
            cloudWatchLogger.logInfo("VALIDANDO METADATA: " + resultadoValidacion, startTime);

            // Validar si la extracción fue exitosa
            if (!resultadoValidacion.equals("true")) {

                registrarError(resultadoValidacion, msUtils.getMetaOss().toString(), request, response, startTime);

                return response;
            }

            //********************************************************************
            // 3. Validar campos obligatorios
            cloudWatchLogger.logInfo("ARG RULES JSON: " + json.toString(),Instant.now());
            String validacionCampos = validator.clearRequest(request, json);


            cloudWatchLogger.logInfo("ARG VALIDACION: " + validacionCampos,Instant.now());

            if (!"true".equals(validacionCampos)) {
                registrarError("ECTL1000", validacionCampos, request, response, startTime);
                //databaseService.insertarBitacora(request, response, "EVA1002", metadata);
                return response;
            }


            //********************************************************************
            // 4. Validar tipo de datos
            Tupla validacionTipos = validator.typeRequest(request, json);

            if (validacionTipos.codigo().charAt(0) == 'E') {

                registrarError(validacionTipos.codigo(), validacionTipos.detalle(), request, response, startTime);
               // databaseService.insertarBitacora(request, response, validacionTipos.codigo(), metadata);

                return response;
            }


            //********************************************************************
            // 5. Ejecutar Store Procedure
            response = databaseService.ejecutarConsultarSaldo(request, response, metadata);



            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            cloudWatchLogger.logInfo("******* EL SERVICIO DE CONSULTA_SALDO SE EJECUTO CON EXITO *******",
                                                    duration.toMillis());


        } catch (IOException e) {

            registrarError("ECTL9999", "Error IOEXCEPCION del sistema: " + e.getMessage(),
                                    request, response, startTime);

        } catch (Exception e) {

            //cloudWatchLogger.logException("Error inesperado en consulta de saldo", e, startTime);
            registrarError("ECTL9999", "Error interno del sistema: " + e.getMessage(),
                                    request, response, startTime);

        }

        return response;
    }


    private void registrarError(String codigo, String detalle, ConsultaSaldoRequest request,
                                ConsultaSaldoResponse  response, Instant startTime) {
        ErrorResponse error = new ErrorResponse();
        error.setCodigoOperacion(codigo);
        error.setMensaje(detalle);
        response.setError(error);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        //cloudWatchLogger.logError(codigo + " : " + detalle, duration.toMillis());
    }

    // Método asíncrono
    public CompletableFuture<ConsultaSaldoResponse> consultarSaldoAsync(ConsultaSaldoRequest request, Metadata metadata) {
        return CompletableFuture.supplyAsync(() -> consultarSaldo(request, metadata));
    }
}
