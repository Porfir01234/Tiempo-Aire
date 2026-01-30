package com.tiempoaire.consultasaldo.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private String codigoOperacion;
    private String mensaje;


    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public void setCodigoOperacion(String codigoOperacion) {
        this.codigoOperacion = codigoOperacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

