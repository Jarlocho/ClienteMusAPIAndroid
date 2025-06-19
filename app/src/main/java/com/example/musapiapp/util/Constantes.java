package com.example.musapiapp.util;

public class Constantes {
    //Puerto nginx: 8088 CREO
    //Puerto desarrollo: 8080
    public static String PUERTO = "8080";

    //Url Desarrollo
    // Jarly: http://10.0.2.2:
    // Pablo: http://192.168.1.9:
    // Nginx: http://...:
    public static String URL_BASE = "http://192.168.1.9:"+PUERTO;
    public static String URL_API = URL_BASE + "/api/";
}
