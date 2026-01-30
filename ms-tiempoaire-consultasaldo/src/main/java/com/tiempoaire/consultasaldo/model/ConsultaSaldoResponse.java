package com.tiempoaire.consultasaldo.model;

import lombok.Data;

@Data
public class ConsultaSaldoResponse {
    private SuccessResponse success;
    private ErrorResponse error;

    public boolean hasError() {
        return error != null;
    }


    public SuccessResponse getSuccess() {
        return success;
    }

    public void setSuccess(SuccessResponse success) {
        this.success = success;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
