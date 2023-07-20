package org.adersonjara.springcloud.msvc.cursos.services;

import org.adersonjara.springcloud.msvc.cursos.models.Usuario;
import org.adersonjara.springcloud.msvc.cursos.models.entity.Curso;
import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> listar();
    Optional<Curso> porId(Long id);
    Curso guardar(Curso curso);
    void eliminar(Long id);


    /*Lógica para obtener datos de otro microservicio*/
    Optional<Usuario> asignarUsuario(Usuario usuario, Long curso_id);//Datos de usuarios, ID del curso
    Optional<Usuario> crearUsuario(Usuario usuario, Long curso_id);//Crear al usuario, ID del curso
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long curso_id);//Desasignar al usuario de un curso

    Optional<Curso> porIdConUsuarios(Long id);

    void eliminarCursoUsuarioPorId(Long id);
}
