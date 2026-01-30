package com.tiempoaire.consultasaldo.util;

public class TypeControl {

    public static Tupla validaTipo(String tipo, String s, String etiqueta) {
        switch (tipo) {
            case "cuenta":
                return validaCuenta(s, etiqueta);

            case "cuentaCLABE":
                return validaCuentaCLABE(s, etiqueta);

            case "fecha":
                return validaFecha(s, etiqueta);

            case "eMail":
                return validaEmail(s, etiqueta);

            case "telefono":
                return validaTelefono(s, etiqueta);

            case "monto":
                return validaMonto(s, etiqueta);

            case "numerico":
                return validaNumerico(s, etiqueta);


            default:
                    String mensaje = String.format(
                            "El tipo de dato: [%s] para validar [%s], NO esta soportado",
                            tipo, etiqueta
                );
                return new Tupla("ECTL0000", mensaje);
        }
    }

    public static Tupla validaCuenta(String s, String etiqueta) {
        String result = Validator.esCuenta(s);
        if (!"cuenta".equals(result)) {
            return new Tupla("ECTL1002", etiqueta + " debe ser numerico de 10 Caracteres");
        }

        return new Tupla("true", "");
    }

    public static Tupla validaCuentaCLABE(String s, String etiqueta) {

        if (!"clabe".equals(Validator.esCuenta(s))) {
            return new Tupla("ECTL3004", etiqueta + " debe ser númerico de  18 Caracteres");
        }
        return new Tupla("true", "");
    }

    public static Tupla validaFecha(String s, String etiqueta) {
        if (!Validator.formatoFecha(s)) {
            return new Tupla("ECTL1005", etiqueta + " debe tener formato DD-MM-YYYY");
        }

        return new Tupla("true", "");
    }

    public static Tupla validaEmail(String s, String etiqueta) {
        if (!Validator.esEmail(s)) {
            return new Tupla("ECTL1006", etiqueta + " debe ser un correo electronico valido");
        }

        return new Tupla("true", "");
    }

    public static Tupla validaTelefono(String s, String etiqueta) {
        if (!"cuenta".equals(Validator.esCuenta(s))) {
            return new Tupla("ECTL1001", etiqueta + " no es númerico");
        }

        return new Tupla("true", "");

    }

    public static Tupla validaMonto(String s, String etiqueta) {
        String respuestavValida;

        respuestavValida = Validator.esMonto(s);

        if (!"true".equals(respuestavValida)) {

            if ("tamanio".equals(respuestavValida)) {
                return new Tupla("ECTL1007", etiqueta + " Formato decimal inválido (máx. 15 enteros y 2 decimales)");
            }

            if ("minimo".equals(respuestavValida)) {
                return new Tupla("ECTL1008", etiqueta + " Valor mínimo permitido 0.01");
            }

            if (!"minimo".equals(respuestavValida)) {
                return new Tupla("ECTL0000", etiqueta + " Error al procesar el monto");
            }
        }

        return new Tupla("true", "");
    }

    public static Tupla validaNumerico(String s, String etiqueta) {
        if (!Validator.esNumerica(s)) {
            return new Tupla("ECTL1001", etiqueta + " no es númerico");
        }

        return new Tupla("true", "");
    }
}
