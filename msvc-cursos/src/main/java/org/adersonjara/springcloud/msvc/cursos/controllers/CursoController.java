package org.adersonjara.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import org.adersonjara.springcloud.msvc.cursos.models.Usuario;
import org.adersonjara.springcloud.msvc.cursos.services.CursoService;
import org.adersonjara.springcloud.msvc.cursos.models.entity.Curso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar(){
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        //Optional<Curso> cursoOptional = cursoService.porId(id);
        //es igual a esto pero con diferente método
        Optional<Curso> cursoOptional = cursoService.porIdConUsuarios(id);

        if (cursoOptional.isPresent()){
            return ResponseEntity.ok(cursoOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if (result.hasErrors()){
            return validar(result);
        }
        Curso cursoDb = cursoService.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result,@PathVariable Long id){
        if (result.hasErrors()){
            return validar(result);
        }
        Optional<Curso> cursoOptional = cursoService.porId(id);
        if (cursoOptional.isPresent()){
            Curso cursoDb = cursoOptional.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Curso> cursoOptional = cursoService.porId(id);
        if (cursoOptional.isPresent()){
            cursoService.eliminar(cursoOptional.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String,String>> validar(BindingResult result){
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(),"El campo "+err.getField()+" "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

    /*Métodos para comunicarme con otro microservicio*/
    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o;
        try{
            o = cursoService.asignarUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje","No existe el usuario por " +
                            "el ID o error en la comunicación: "+e.getMessage()));
        }
        if (o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o;
        try{
            o = cursoService.crearUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje","No se pudo crear el usuario " +
                            "o error en la comunicación"+e.getMessage()));
        }
        if (o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o;
        try{
            o = cursoService.eliminarUsuario(usuario,cursoId);
        } catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje","No existe el usuario por " +
                            "el ID o error en la comunicación: "+e.getMessage()));
        }
        if (o.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
        cursoService.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }


}
