package com.example.musapiapp.util;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private static final String NOMBRE_PREF = "MusAPI_Prefs";
    private static final String CLAVE_TOKEN = "token";

    public static void guardarToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        prefs.edit().putString(CLAVE_TOKEN, token).apply();
    }

    public static String obtenerToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        return prefs.getString(CLAVE_TOKEN, null);
    }

    public static void borrarToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        prefs.edit().remove(CLAVE_TOKEN).apply();
    }
}