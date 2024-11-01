package proyecto_final.dw.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto_final.dw.modelos.Departamento;
import proyecto_final.dw.modelos.Horario;
import proyecto_final.dw.modelos.Rol;
import proyecto_final.dw.modelos.Usuario;
import proyecto_final.dw.repositorios.DepartamentoRepository;
import proyecto_final.dw.repositorios.HorarioRepository;
import proyecto_final.dw.repositorios.RolRepository;
import proyecto_final.dw.repositorios.UsuarioRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepartamentoRepository departamentoReposity;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    PasswordEncoder passwordEncoder;




    public Set<Usuario> obtenerTodosLosUsuarios() {
        return new HashSet<>(usuarioRepository.findAll());
    }

    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));
    }



    public Usuario actualizarUsuario(Long idUsuario, Usuario usuarioActualizado, Long idDepartamento, Long idHorario, Set<Long> idsRoles) {
        Usuario usuarioExistente = obtenerUsuarioPorId(idUsuario);
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());

        Optional<Departamento> departamentoOpt = departamentoReposity.findById(idDepartamento);
        Optional<Horario> horarioOpt = horarioRepository.findById(idHorario);

        if (departamentoOpt.isPresent() && horarioOpt.isPresent()) {
            usuarioExistente.setDepartamento(departamentoOpt.get());
            usuarioExistente.setHorario(horarioOpt.get());

            Set<Rol> roles = new HashSet<>(rolRepository.findAllById(idsRoles));
            usuarioExistente.setRoles(roles);

            return usuarioRepository.save(usuarioExistente);
        } else {
            throw new RuntimeException("Departamento o horario no encontrados");
        }
    }

    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(idUsuario);
    }

}

