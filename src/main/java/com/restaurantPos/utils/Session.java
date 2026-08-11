package com.restaurantPos.utils;

import com.restaurantPos.models.Usuarios;

public class Session {
    public static Usuarios getUsuarioActual() {

        return usuarioActual;
    }

    private static Usuarios usuarioActual;

    public static void setUsuarioActual(Usuarios usuario) {
        usuarioActual = usuario;
    }

    public static void limpiarSesion() {
        usuarioActual = null;
    }
}
