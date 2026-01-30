package com.tiempoaire.consultasaldo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

import java.time.Duration;
import java.time.Instant;

@Component
public class CloudWatchLogger {
    private static final Logger logger = LoggerFactory.getLogger(CloudWatchLogger.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Obtiene el ID correlativo actual del contexto (MDC).
     * Si no existe, genera uno nuevo automáticamente.
     */
    public static String getCorrelativeId() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
        return correlationId;
    }

    /**
     * Permite establecer un ID correlativo específico (ej. recibido de un header externo).
     */
    public static void setCorrelativeId(String id) {
        if (id != null && !id.isEmpty()) {
            MDC.put(CORRELATION_ID_KEY, id);
        }
    }

    /**
     * Limpia el contexto de logs.
     * Útil al finalizar la petición para evitar contaminación entre hilos.
     */
    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    // Nuevo método para soportar logInfo con mensaje y duración
    public void logInfo(String mensaje, long duracionMs) {
        // Aseguramos que el correlativo esté en el MDC para este hilo
        getCorrelativeId();
        logger.info("{} - Duración: {} ms", mensaje, duracionMs);
    }

    /**
     * Registra información calculando la duración desde un Instant inicial hasta ahora.
     * @param mensaje   El texto a loguear.
     * @param startTime El momento en que inició la operación.
     */
    public void logInfo(String mensaje, Instant startTime) {
        long duracionMs = 0;
        if (startTime != null) {
            duracionMs = Duration.between(startTime, Instant.now()).toMillis();
        }

        getCorrelativeId(); // Asegura trazabilidad
        logger.info("{} - Duración: {} ms", mensaje, duracionMs);
    }


    // Método estático por si prefieres no instanciar la clase
    public static void logInfoStatic(String mensaje, long duracionMs) {
        logger.info("{} - Duración: {} ms", mensaje, duracionMs);
    }

    /**
     * Registra una excepción calculando la duración desde un tiempo inicial.
     *
     * @param mensaje   Descripción del error.
     * @param e         La excepción capturada.
     * @param startTime El Instant de inicio de la operación (ej. Instant.now()).
     */
    public void logException(String mensaje, Exception e, Instant startTime) {
        long duracionMs = 0;
        if (startTime != null) {
            duracionMs = Duration.between(startTime, Instant.now()).toMillis();
        }

        // Aseguramos el ID correlativo
        getCorrelativeId();

        // Logueamos el mensaje, la duración y la excepción completa (stacktrace)
        logger.error("{} - Duración: {} ms - Error: {}", mensaje, duracionMs, e.getMessage(), e);
    }
}
