package com.tiempoaire.consultasaldo.model;

import lombok.Data;

@Data
public class SuccessResponse {
    private String codigoOperacion;
    private String mensaje;
    private Resultado result;


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

    public Resultado getResult() {
        return result;
    }

    public void setResult(Resultado result) {
        this.result = result;
    }
}
