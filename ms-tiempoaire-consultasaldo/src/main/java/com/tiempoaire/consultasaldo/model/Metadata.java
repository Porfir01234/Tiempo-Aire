package com.tiempoaire.consultasaldo.model;

import lombok.Data;

@Data
public class Metadata {
    private String canal;
    private Integer empresa;
    private String idAcceso;
    private String idCorrelativo;
    private String usuario;
    private String api;
    private String numeroPagina;
    private String numeroRegistro;

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public String getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(String idAcceso) {
        this.idAcceso = idAcceso;
    }

    public String getIdCorrelativo() {
        return idCorrelativo;
    }

    public void setIdCorrelativo(String idCorrelativo) {
        this.idCorrelativo = idCorrelativo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(String numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }
}
