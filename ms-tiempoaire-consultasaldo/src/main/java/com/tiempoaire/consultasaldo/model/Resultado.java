package com.tiempoaire.consultasaldo.model;

import lombok.Data;

@Data
public class Resultado {

    private String saldo;

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
}
