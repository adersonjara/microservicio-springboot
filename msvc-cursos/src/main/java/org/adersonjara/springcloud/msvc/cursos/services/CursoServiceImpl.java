package org.adersonjara.springcloud.msvc.cursos.services;

import org.adersonjara.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.adersonjara.springcloud.msvc.cursos.models.Usuario;
import org.adersonjara.springcloud.msvc.cursos.models.entity.Curso;
import org.adersonjara.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.adersonjara.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService{

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioClientRest usuarioClientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long curso_id) {
        Optional<Curso> o = cursoRepository.findById(curso_id);
        if (o.isPresent()){
            Usuario usuarioMicroservicio = usuarioClientRest.detalle(usuario.getId());//Se comunica con el microservicio
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicio.getId());
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicio);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long curso_id) {
        Optional<Curso> o = cursoRepository.findById(curso_id);
        if (o.isPresent()){
            Usuario usuarioMicroservicioNuevo = usuarioClientRest.crear(usuario);//Se comunica con el microservicio
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicioNuevo.getId());
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicioNuevo);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long curso_id) {
        Optional<Curso> o = cursoRepository.findById(curso_id);
        if (o.isPresent()){
            Usuario usuarioMicroservicio = usuarioClientRest.detalle(usuario.getId());//Se comunica con el microservicio
            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicroservicio.getId());
            curso.removeCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMicroservicio);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> o = cursoRepository.findById(id);
        if (o.isPresent()){
            Curso curso = o.get();
            if (!curso.getCursoUsuarios().isEmpty()){
                List<Long> ids = curso.getCursoUsuarios().stream().map(cu -> cu.getUsuarioId())
                        .collect(Collectors.toList());
                List<Usuario> usuarios = usuarioClientRest.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        cursoRepository.eliminarCursoUsuarioPorId(id);
    }


}
