package org.adersonjara.springcloud.msvc.usuarios.controllers;

import org.adersonjara.springcloud.msvc.usuarios.entity.Usuario;
import org.adersonjara.springcloud.msvc.usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/crash")
    public void crash(){
        ((ConfigurableApplicationContext)context).close();
    }

    @GetMapping
    public Map<String,List<Usuario>> listar(){
        return Collections.singletonMap("users",usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if (usuarioOptional.isPresent()){
            return ResponseEntity.ok(usuarioOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){
        if (result.hasErrors()){
            /*Map<String,String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> {
                errores.put(err.getField(),"El campo "+err.getField()+" "+err.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errores);*/
            return validar(result);
        }

        if (!usuario.getEmail().isEmpty() && usuarioService.existePorEmail(usuario.getEmail())){
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje","Ya existe un usuario con ese email."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result,@PathVariable Long id){
        if (result.hasErrors()){
            return validar(result);
        }

        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if (usuarioOptional.isPresent()){
            Usuario usuarioDb = usuarioOptional.get();

            if (!usuario.getEmail().isEmpty() &&
                    !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) &&
                    usuarioService.porEmail(usuario.getEmail()).isPresent() ){
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("mensaje","Ya existe un usuario con ese email."));
            }

            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuarioDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if (usuarioOptional.isPresent()){
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
        return ResponseEntity.ok(usuarioService.listarPorIds(ids));
    }

    private ResponseEntity<Map<String,String>> validar(BindingResult result){
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(),"El campo "+err.getField()+" "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
