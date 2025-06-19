// src/main/java/com/example/musapiapp/util/Constantes.java
package com.example.musapiapp.util;

public class Constantes {
    /** Puerto donde corre tu API en NetBeans **/
    public static final String PUERTO   = "8080";

    /**
     * Base para peticiones web.
     * Desde el emulador: 10.0.2.2 → localhost de tu máquina
     * Pablo: 192.168.1.17
     */
    public static final String URL_BASE = "http://192.168.1.17:" + PUERTO;

    /** Añade “/api/” al final para los endpoints de Retrofit **/
    public static final String URL_API  = URL_BASE + "/api/";
}
