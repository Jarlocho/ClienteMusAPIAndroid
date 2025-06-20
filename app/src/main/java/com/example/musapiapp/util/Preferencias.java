package com.example.musapiapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musapiapp.dto.UsuarioDTO;
import com.google.gson.Gson;

public class Preferencias {

    private static final String NOMBRE_PREF     = "MusAPI_Prefs";
    private static final String CLAVE_TOKEN     = "token";
    private static final String CLAVE_USUARIO   = "usuario_json";

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

    // --------------------------------------------------------
    // Métodos para guardar/recuperar el UsuarioDTO como JSON
    // --------------------------------------------------------

    public static void guardarUsuarioJson(Context context, String usuarioJson) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        prefs.edit().putString(CLAVE_USUARIO, usuarioJson).apply();
    }

    public static String recuperarUsuarioJson(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        return prefs.getString(CLAVE_USUARIO, null);
    }

    public static void borrarUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        prefs.edit().remove(CLAVE_USUARIO).apply();
    }
    /**
     * Recupera el UsuarioDTO completo (asumiendo que lo guardaste como JSON).
     */
    public static UsuarioDTO obtenerUsuario(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        String json = prefs.getString(CLAVE_USUARIO, null);
        if (json == null) return null;
        return new Gson().fromJson(json, UsuarioDTO.class);
    }

    /**
     * Obtiene el ID del usuario actualmente logueado, o -1 si no hay.
     */
    public static int obtenerIdUsuario(Context context) {
        UsuarioDTO u = obtenerUsuario(context);
        return (u != null ? u.getIdUsuario() : -1);
    }



}
