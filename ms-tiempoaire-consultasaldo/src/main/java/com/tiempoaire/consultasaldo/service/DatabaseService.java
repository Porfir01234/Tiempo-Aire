package com.tiempoaire.consultasaldo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiempoaire.consultasaldo.model.ConsultaSaldoRequest; // Ajusta tus imports
import com.tiempoaire.consultasaldo.model.ConsultaSaldoResponse;
import com.tiempoaire.consultasaldo.model.ErrorResponse;
import com.tiempoaire.consultasaldo.model.Metadata;
import com.tiempoaire.consultasaldo.model.Resultado;
import com.tiempoaire.consultasaldo.model.SuccessResponse;
import com.tiempoaire.consultasaldo.util.CloudWatchLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;


@Service
public class DatabaseService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CloudWatchLogger cloudWatchLogger;

    //@Transactional // Asegura que la transacción se complete correctamente
    public ConsultaSaldoResponse ejecutarConsultarSaldo(ConsultaSaldoRequest request,  ConsultaSaldoResponse response, Metadata  metadata) {

        ErrorResponse respuestaError = new  ErrorResponse();
        SuccessResponse respuestaSuccess = new SuccessResponse();
        Resultado respuestaResultado = new Resultado();

        // 1. Crear la llamada al SP
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_SALDO_CUENTA");

        // 2. Registrar parámetros de Entrada (IN)
        query.registerStoredProcedureParameter("p_numero_cuenta", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_usuario", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_canal", String.class, ParameterMode.IN);

        // 3. Registrar parámetros de Salida (OUT)
        query.registerStoredProcedureParameter("p_codigo_operacion", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        //query.registerStoredProcedureParameter("p_saldo", Double.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_saldo", java.math.BigDecimal.class, ParameterMode.OUT);

        // 4. Asignar valores de entrada
        query.setParameter("p_numero_cuenta", request.getNumeroCuenta());
        query.setParameter("p_usuario", metadata.getUsuario());
        query.setParameter("p_canal", metadata.getCanal());

        // 5. Ejecutar
        try {
            query.execute();
        } catch (Exception e) {
            // Esto imprimirá el error ORA-XXXXX real en tu consola
            e.printStackTrace();
            throw e;
        }

        // 6. Obtener resultados de salida
        /*
        String codigoOp = (String) query.getOutputParameterValue("p_codigo_operacion");
        String mensaje = (String) query.getOutputParameterValue("p_mensaje");
        //Double saldo = (Double) query.getOutputParameterValue("p_saldo");
        java.math.BigDecimal saldoBD = (java.math.BigDecimal) query.getOutputParameterValue("p_saldo");
        Double saldo = (saldoBD != null) ? saldoBD.doubleValue() : 0.0;
        */
        Object outCod = query.getOutputParameterValue("p_codigo_operacion");
        Object outMsg = query.getOutputParameterValue("p_mensaje");
        Object outSal = query.getOutputParameterValue("p_saldo");

        String codigoOp = (outCod != null) ? outCod.toString() : "E999";
        String mensaje  = (outMsg != null) ? outMsg.toString() : "Sin mensaje de la DB";

        // Convertir de forma segura NUMBER a Double
        Double saldo = 0.0;
        if (outSal instanceof Number) {
            saldo = ((Number) outSal).doubleValue();
        }

        // 8. Validar lógica: Si es "E" es error
        //if (codigoOp != null && codigoOp.contains("E")) {
        if (codigoOp != null && (codigoOp.startsWith("E") || codigoOp.contains("ERR"))){
            // Aquí puedes lanzar una excepción personalizada o manejar el error
            respuestaError.setCodigoOperacion(codigoOp);
            respuestaError.setMensaje(mensaje);
            response.setError(respuestaError);
            response.setSuccess(null);
        } else {
            respuestaSuccess.setCodigoOperacion(codigoOp);
            respuestaSuccess.setMensaje(mensaje);

            respuestaResultado.setSaldo(saldo != null ? saldo.toString() : "0.0");

            respuestaSuccess.setResult(respuestaResultado);

            response.setSuccess(respuestaSuccess);
            response.setError(null);
        }

        // 7. Insertar en Bitácora (Independientemente de si es éxito o error)
        this.insertarBitacora(request, response, codigoOp, metadata);

        return response;
    }

    public CompletableFuture<Void>  insertarBitacora(ConsultaSaldoRequest request,
                                                    ConsultaSaldoResponse response,
                                                    String codigo,
                                                    Metadata metadata) {
        Instant startTimeBitacora = Instant.now();

        try {
        // Convertir objetos a JSON usando [Jackson ObjectMapper](https://fasterxml.github.io)
        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = objectMapper.writeValueAsString(response);
        String metadataJson = objectMapper.writeValueAsString(metadata);


        // 1. Crear la llamada al SP
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SPALTABITACORA");

        // 2. Registrar parámetros de Entrada (IN)

            query.registerStoredProcedureParameter("PA_FIIDCORRELATIVO", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_FECHA", java.sql.Timestamp.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_SERVICIO", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_REQUEST", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_RESPONSE", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_METADATA", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("PA_CODIGO", String.class, ParameterMode.IN);


            // 3. Registrar parámetros de Salida (OUT)
            query.registerStoredProcedureParameter("P_CODIGO_OPERACION", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("P_MENSAJE", String.class, ParameterMode.OUT);

            // 4. Asignar valores de entrada
            query.setParameter("PA_FIIDCORRELATIVO", metadata.getIdAcceso());
            query.setParameter("PA_FECHA", new java.sql.Timestamp(System.currentTimeMillis()));
            query.setParameter("PA_SERVICIO", "MS_TIEMPOAIRE_CONSULTA_SALDO");
            query.setParameter("PA_REQUEST", requestJson);
            query.setParameter("PA_RESPONSE", responseJson);
            query.setParameter("PA_METADATA", metadataJson);
            query.setParameter("PA_CODIGO", codigo);


            // 5. Ejecutar
                query.execute();

            // 6. Obtener resultados de salida
            String codigoOp = (String) query.getOutputParameterValue("P_CODIGO_OPERACION");
            String mensaje = (String) query.getOutputParameterValue("P_MENSAJE");

            Instant endTime = Instant.now();

            // 8. Validar lógica: Si es "E" es error
            if (codigoOp != null && codigoOp.contains("E")) {
                cloudWatchLogger.logInfo("STATUS BITACORA " + mensaje, Duration.between(startTimeBitacora, endTime).toMillis());
    //        } else {
            }


            cloudWatchLogger.logInfo("Bitácora insertada exitosamente", Duration.between(startTimeBitacora, endTime).toMillis());

        } catch (Exception e) {
            cloudWatchLogger.logException("Error al insertar bitácora", e, startTimeBitacora);
        }
        return CompletableFuture.completedFuture(null);
    }


//    @Async
//    public CompletableFuture<Void> insertarBitacora(ConsultaSaldoRequest request,
//                                                    ConsultaSaldoResponse response,
//                                                    String codigo,
//                                                    Metadata metadata) {
//        Instant startTime = Instant.now();
//        try {
//            // Convertir objetos a JSON usando [Jackson ObjectMapper](https://fasterxml.github.io)
//            String requestJson = objectMapper.writeValueAsString(request);
//            String responseJson = objectMapper.writeValueAsString(response);
//            String metadataJson = objectMapper.writeValueAsString(metadata);
//
//            /* MySQL
//            String sql = """
//                INSERT INTO bitacora_peticiones
//                (fecha, servicio, request, response, metadata, codigo, id_correlativo)
//                VALUES (NOW(), ?, ?, ?, ?, ?, ?)
//                """;
//             */
//
//            String sql = """
//                INSERT INTO bitacora_peticiones
//                (fecha, servicio, request, response, metadata, codigo, id_correlativo)
//                VALUES (SYSDATE, ?, ?, ?, ?, ?, ?)
//                """;
//
//            // Uso de [JdbcTemplate](https://docs.spring.io) para ejecución directa
//            jdbcTemplate.update(sql,
//                    "MS_TIEMPOAIRE_CONSULTA_SALDO",
//                    requestJson,
//                    responseJson,
//                    metadataJson,
//                    codigo,
//                    CloudWatchLogger.getCorrelativeId()
//            );
//
//            Instant endTime = Instant.now();
//            cloudWatchLogger.logInfo("Bitácora insertada exitosamente", Duration.between(startTime, endTime).toMillis());
//
//        } catch (Exception e) {
//            cloudWatchLogger.logException("Error al insertar bitácora", e, startTime);
//        }
//        return CompletableFuture.completedFuture(null);
//    }

}
