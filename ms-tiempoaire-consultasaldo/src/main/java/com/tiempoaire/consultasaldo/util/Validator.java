package com.tiempoaire.consultasaldo.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Map;

import java.util.regex.Pattern;
@Component
public class Validator {

    private final ObjectMapper mapper = new ObjectMapper();

    // Se precompila el patrón para mejorar la eficiencia en Java
    private static final Pattern PATRON_EMAIL =
            Pattern.compile("(\\w+)(\\.\\w+)*@(\\w+)([\\.-]\\w+)*(\\.\\w{2,})+");
    private static final Pattern SOLO_DIGITOS =
            Pattern.compile("^[0-9]+$");

    private static final Pattern ALFANUMERICO =
            Pattern.compile("^[a-zA-Z0-9]+$");

    private static final Pattern SOLO_DOUBLE =
            Pattern.compile("^\\d{1,15}(\\.\\d{1,2})?$");

    @Autowired
    private CloudWatchLogger cloudWatchLogger;
    public String clearRequest(Object  request, JsonNode rulesJson) {
        try {
            // Convertimos el objeto request a un árbol de nodos de Jackson directamente
            JsonNode jsonObj = mapper.valueToTree(request);

            Iterator<Map.Entry<String, JsonNode>> fields = rulesJson.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                String key = entry.getKey();
                JsonNode ruleValue = entry.getValue();

                // .at() busca por puntero JSON.
                // Asegúrate que en rules.json las llaves empiecen con / (ej: "/telefono")
                JsonNode nodeValue = jsonObj.at(key);
                String valor = (nodeValue.isMissingNode() || nodeValue.isNull()) ? "" : nodeValue.asText();

                String firstRule = ruleValue.get(0).asText();

                cloudWatchLogger.logInfo("clearRequest==> key: ["+ key + "]  valor: [" + valor + "]" + "]  firstRule: [" + firstRule + "]" , Instant.now());

                if ("obligatorio".equals(firstRule) && esVacio(valor)) {

                    return key + " Obligatorio";
                }

            }
            return "true";
        } catch (Exception e) {
            return "ERROR_INTERNO: " + e.getMessage();
        }
    }


    public Tupla typeRequest(Object request, JsonNode json) {
        try {
            // Convertir protobuf a JSON string
            //String jsonStr = JsonFormat.printer().print(request);
            //JsonNode jsonObj = JsonUtils.parseJson(jsonStr);
            JsonNode jsonObj = mapper.valueToTree(request);

            // Iterar sobre las reglas del JSON usando fields()
            Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey(); // Ej: "/numeroCuenta"
                JsonNode valueNode = jsonObj.at(key); // Acceder con JSON Pointer
                String value = valueNode.isMissingNode() ? "" : valueNode.asText();

                JsonNode ruleNode = entry.getValue();
                // Verificar si el nodo de reglas tiene al menos 2 elementos
                if (ruleNode.isArray() && ruleNode.size() > 1) {
                    String tipo = ruleNode.get(1).asText("");
                    if (!Validator.esVacio(value) && !Validator.esVacio(tipo)) {
                        Tupla res = TypeControl.validaTipo(tipo, value, key);
                        if (res.codigo().charAt(0) == 'E') {
                            return res; // Error encontrado
                        }
                    }
                }
            }
            return new Tupla("true", "");
        } catch (Exception e) {
            System.err.println("Error al convertir el request a JSON: " + e.getMessage());
            return new Tupla("ECTL0000", "Error al convertir el request a JSON");
        }
    }

    private static boolean esVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean esAlfanumerica(String s) {
        return (s != null) && ALFANUMERICO.matcher(s).matches();
    }

    public static boolean esNumerica(String s) {
        return s != null && SOLO_DIGITOS.matcher(s).matches();
    }

    public static String esMonto(String s){

        if ( !SOLO_DOUBLE.matcher(s).matches()){
            return "tamanio";
        }

        double monto;
        try {
            // En Java, Double.parseDouble es el equivalente a std::stof/stod
            monto = Double.parseDouble(s);

            // Validación de valor mínimo
            if (monto < 0.01) {
                return "minimo";
            }

        } catch (NumberFormatException e) {
            return "error";

        }

        return "true";
    }

    public static String esCuenta(String s) {
        if (s == null)
            return "false";

        if (s.length() == 10 && esNumerica(s))
            return "cuenta";

        if (s.length() == 18 && esNumerica(s))
            return "clabe";

        return "false";
    }

    public static boolean formatoFecha(String dateStr) {
        // 1. Validación rápida de formato con regex
        if (dateStr == null || !dateStr.matches("^\\d{2}-\\d{2}-\\d{4}$")) {
            return false;
        }

        // 2. Validación completa con java.time
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean esEmail(String s) {
        return s != null && PATRON_EMAIL.matcher(s).matches();
    }
}
