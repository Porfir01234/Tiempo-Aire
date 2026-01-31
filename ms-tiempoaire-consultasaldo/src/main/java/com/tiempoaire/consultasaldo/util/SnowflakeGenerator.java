package com.tiempoaire.consultasaldo.util;

import cn.hutool.core.lang.Snowflake;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeGenerator {

    private final Snowflake snowflake = new Snowflake(1, 1);

    public synchronized long nextId() {
        return snowflake.nextId();
    }
}
