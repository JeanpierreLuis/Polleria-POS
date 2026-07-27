package com.restaurantPos.services;

import com.restaurantPos.models.Usuarios;
import com.restaurantPos.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean login(String username, String password) {

        Usuarios usuario = usuarioRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return false;
        }
        return  encoder.matches(password, usuario.getPassword());
    }

    public Usuarios autenticar(String username, String password) {
        Usuarios usuario = usuarioRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return null;
        }
        if  (!encoder.matches(password, usuario.getPassword())) {
            return null;
        }
        return usuario;
    }
}
