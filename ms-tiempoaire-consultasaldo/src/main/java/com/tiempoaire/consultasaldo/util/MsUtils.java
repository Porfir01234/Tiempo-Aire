package com.tiempoaire.consultasaldo.util;


import com.tiempoaire.consultasaldo.model.Metadata;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsUtils {
    private Map<String, String> metadataMap = new HashMap<>();
    private StringBuilder metaOss = new StringBuilder();

    public String extractMetadata(Metadata headers, String noBuscar) {
        metaOss.setLength(0);
        metadataMap.clear();

        // Extraemos los valores usando los Getters de tu clase Metadata
        checkAndPut("x-usuario", headers.getUsuario(), noBuscar);
        checkAndPut("x-canal", headers.getCanal(), noBuscar);
        // Convertimos Integer a String para empresa
        checkAndPut("x-empresa", headers.getEmpresa() != null ? headers.getEmpresa().toString() : "", noBuscar);
        checkAndPut("x-id-acceso", headers.getIdAcceso(), noBuscar);
        //checkAndPut("x-id-correlativo", headers.getIdCorrelativo(), noBuscar);
        //checkAndPut("x-api", headers.getApi(), noBuscar);

        if (metaOss.length() > 0) {
            return "ECTL1000"; // Campos obligatorios faltantes
        }

        if (!isNumeric(getEmpresa())) {
            metaOss.append("x-empresa no es númerico");
            return "ECTL1001";
        }

        if (!isNumeric(getCanal())) {
            metaOss.append("x-canal no es númerico");
            return "ECTL1001";
        }

        // Actualizamos el logger con el ID correlativo
        //CloudWatchLogger.setCorrelativeId(getIdCorrelativo());

        return "true";
    }

    private void checkAndPut(String key, String value, String noBuscar) {
        String val = (value == null) ? "" : value;
        if (val.isEmpty() && !key.equals(noBuscar)) {
            metaOss.append("Falta Header " + key).append(", ");
        }
        metadataMap.put(key, val);
    }

    public String getEmpresa() { return metadataMap.getOrDefault("x-empresa", ""); }
    public String getCanal() { return metadataMap.getOrDefault("x-canal", ""); }
    //public String getIdCorrelativo() { return metadataMap.getOrDefault("x-id-correlativo", ""); }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    public StringBuilder getMetaOss() {
        return metaOss;
    }

    public void setMetaOss(StringBuilder metaOss) {
        this.metaOss = metaOss;
    }
}
