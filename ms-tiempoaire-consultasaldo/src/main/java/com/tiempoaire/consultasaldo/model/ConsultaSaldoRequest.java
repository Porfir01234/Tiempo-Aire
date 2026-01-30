package com.tiempoaire.consultasaldo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class ConsultaSaldoRequest {

    /*
    @NotBlank(message = "El número de cuenta es obligatorio")
    @JsonProperty("numeroCuenta")
    */
    private String numeroCuenta;
    private String cuentaCLABE;
    private String fechaNacimiento;
    private String correoElectronico;

    private String telefono;
    private String monto;
    private String idPais;
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getCuentaCLABE() {
        return cuentaCLABE;
    }

    public void setCuentaCLABE(String cuentaCLABE) {
        this.cuentaCLABE = cuentaCLABE;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getIdPais() {
        return idPais;
    }

    public void setIdPais(String idPais) {
        this.idPais = idPais;
    }
}
