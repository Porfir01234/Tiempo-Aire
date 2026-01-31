package com.tiempoaire.consultasaldo;

import com.tiempoaire.consultasaldo.util.SnowflakeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsTiempoaireConsultasaldoApplication {

    @Autowired
    private static SnowflakeGenerator snowflakeGenerator;
	public static void main(String[] args) {
		SpringApplication.run(MsTiempoaireConsultasaldoApplication.class, args);

        long snowFlake = snowflakeGenerator.nextId();

        System.out.println(snowFlake);

	}

}
