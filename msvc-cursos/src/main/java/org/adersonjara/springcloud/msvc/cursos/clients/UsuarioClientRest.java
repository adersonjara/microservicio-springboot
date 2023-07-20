package org.adersonjara.springcloud.msvc.cursos.clients;

import org.adersonjara.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-usuarios")
public interface UsuarioClientRest {

    @GetMapping("/{id}")
    public Usuario detalle(@PathVariable Long id);

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario);

    @GetMapping("/usuarios-por-curso")
    public List<Usuario> obtenerAlumnosPorCurso(@RequestParam Iterable<Long> ids);

}
