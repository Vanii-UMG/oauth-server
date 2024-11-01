package proyecto_final.dw.servicios;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto_final.dw.dtos.request.UsuarioRequest;
import proyecto_final.dw.dtos.response.UsuarioResponse;
import proyecto_final.dw.modelos.Departamento;
import proyecto_final.dw.modelos.Horario;
import proyecto_final.dw.modelos.Rol;
import proyecto_final.dw.modelos.Usuario;
import proyecto_final.dw.repositorios.DepartamentoRepository;
import proyecto_final.dw.repositorios.HorarioRepository;
import proyecto_final.dw.repositorios.RolRepository;
import proyecto_final.dw.repositorios.UsuarioRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DepartamentoRepository departamentoRepository;

    @Autowired
    HorarioRepository horarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public Usuario crearUsuarioConRol(UsuarioRequest usuarioRequest) {
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        usuario.setNombre(usuarioRequest.getNombre());
        usuario.setApellido(usuarioRequest.getApellido());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setTelefono(usuarioRequest.getTelefono());

        Rol rol = rolRepository.findById(usuarioRequest.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuarioRequest.getRolId()));

        Horario horario = horarioRepository.findById(usuarioRequest.getIdHorario())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + usuarioRequest.getIdHorario()));


        Departamento departamento = departamentoRepository.findById(usuarioRequest.getIdDepartamento())
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + usuarioRequest.getIdDepartamento()));

        usuario.getRoles().add(rol);
        usuario.setHorario(horario);
        usuario.setDepartamento(departamento);

        return usuarioRepository.save(usuario);
    }

    public List<UsuarioResponse> obtenerTodosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAUsuarioResponse)  // Convierte cada Usuario en UsuarioResponse
                .collect(Collectors.toList());         // Recopila el resultado en una lista
    }


    public UsuarioRequest actualizarUsuario(Long idUsuario, Usuario usuarioActualizado, Long idDepartamento, Long idHorario, Set<Long> idsRoles) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());

        Optional<Departamento> departamentoOpt = departamentoRepository.findById(idDepartamento);
        Optional<Horario> horarioOpt = horarioRepository.findById(idHorario);

        if (departamentoOpt.isPresent() && horarioOpt.isPresent()) {
            usuarioExistente.setDepartamento(departamentoOpt.get());
            usuarioExistente.setHorario(horarioOpt.get());

            Set<Rol> roles = new HashSet<>(rolRepository.findAllById(idsRoles));
            usuarioExistente.setRoles(roles);

            usuarioRepository.save(usuarioExistente);
            return convertirAUsuarioDTO(usuarioExistente);
        } else {
            throw new RuntimeException("Departamento o horario no encontrados");
        }
    }

    private UsuarioRequest convertirAUsuarioDTO(Usuario usuario) {
        return new UsuarioRequest(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRoles() != null && !usuario.getRoles().isEmpty() 
                    ? usuario.getRoles().iterator().next().getId() 
                    : null,
                usuario.getHorario() != null 
                    ? usuario.getHorario().getIdHorario() 
                    : null,
                usuario.getDepartamento() != null 
                    ? usuario.getDepartamento().getIdDepartamento() 
                    : null,
                usuario.getDepartamento() != null 
                    ? usuario.getDepartamento().getNombreDepartamento() 
                    : null
        );
    }

    private UsuarioResponse convertirAUsuarioResponse(Usuario usuario) {
        // Obtener el ID del departamento
        Long idDepartamento = usuario.getDepartamento() != null ? usuario.getDepartamento().getIdDepartamento() : null;

        // Obtener el ID del horario
        Long idHorario = usuario.getHorario() != null ? usuario.getHorario().getIdHorario() : null;

        // Obtener los IDs de los roles
        Set<Long> idsRoles = usuario.getRoles().stream()
                .map(Rol::getId) // Asegúrate de que Rol tiene el método getId
                .collect(Collectors.toSet());

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                idHorario,
                idDepartamento,
                idsRoles,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.isEnabled()
        );
    }
    
    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(idUsuario);
    }

    public Set<Rol> obtenerRolesPorIdUser(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario))
                .getRoles();
    }




}
