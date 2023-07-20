package org.adersonjara.springcloud.msvc.usuarios.services;

import org.adersonjara.springcloud.msvc.usuarios.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listar();

    Optional<Usuario> porId(Long id);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);

    Optional<Usuario> porEmail(String email);

    boolean existePorEmail(String email);

    List<Usuario> listarPorIds(Iterable<Long> ids);

    /*Comunicación con otro microservicio*/
}
