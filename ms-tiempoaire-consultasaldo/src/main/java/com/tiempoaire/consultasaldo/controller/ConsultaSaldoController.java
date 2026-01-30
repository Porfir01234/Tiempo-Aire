package com.tiempoaire.consultasaldo.controller;
import com.tiempoaire.consultasaldo.model.ConsultaSaldoRequest;
import com.tiempoaire.consultasaldo.model.ConsultaSaldoResponse;
import com.tiempoaire.consultasaldo.model.Metadata;
import com.tiempoaire.consultasaldo.model.SuccessResponse;
import com.tiempoaire.consultasaldo.service.ConsultaSaldoService;
import com.tiempoaire.consultasaldo.util.CloudWatchLogger;
import com.tiempoaire.consultasaldo.util.MsUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/consultasaldo")
public class ConsultaSaldoController {

    @Autowired
    private ConsultaSaldoService consultaSaldoService;

    @Autowired
    private CloudWatchLogger cloudWatchLogger;

    @Autowired
    private MsUtils msUtils;

    @PostMapping("/consultar")
    public ResponseEntity<ConsultaSaldoResponse> consultarSaldo(
            @RequestBody ConsultaSaldoRequest request,
            HttpServletRequest httpRequest) {

        Instant startTime = Instant.now();
        //Enviando al Log
        cloudWatchLogger.logInfo("Iniciando SERVICIO", startTime);

        // 1. Crear y llenar el objeto Metadata desde los headers del httpRequest
        Metadata metadataObj = new Metadata();
        metadataObj.setUsuario(httpRequest.getHeader("x-usuario"));
        metadataObj.setCanal(httpRequest.getHeader("x-canal"));
        // Convertir String a Integer para empresa
        String empresaHeader = httpRequest.getHeader("x-empresa");
        if (empresaHeader != null && empresaHeader.matches("\\d+")) {
            metadataObj.setEmpresa(Integer.parseInt(empresaHeader));
        }
        metadataObj.setIdAcceso(httpRequest.getHeader("x-id-acceso"));
        metadataObj.setIdCorrelativo(httpRequest.getHeader("x-id-correlativo"));
        metadataObj.setApi(httpRequest.getHeader("x-api"));

        //Enviando al Log
        cloudWatchLogger.logInfo("METADATA recibida: " + metadataObj.toString(), startTime);



        //Ejecutar ConsultaSaldo
        ConsultaSaldoResponse response = consultaSaldoService.consultarSaldo(request, metadataObj);

        /*
        ConsultaSaldoResponse response = new  ConsultaSaldoResponse();
        SuccessResponse  success = new SuccessResponse();


        success.setCodigoOperacion("100");
        success.setMensaje("Proceso Ejecutado");

        response.setSuccess(success);
         */

        //Enviando al Log
        cloudWatchLogger.logInfo("TERMINANDO SERVICIO" + response.toString(), startTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("MS_TIEMPOAIRE_CONSULTA_SALDO is running");
    }

}
