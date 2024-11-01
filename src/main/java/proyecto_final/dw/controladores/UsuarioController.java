package proyecto_final.dw.controladores;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proyecto_final.dw.dtos.request.UsuarioRequest;
import proyecto_final.dw.dtos.response.UsuarioResponse;
import proyecto_final.dw.modelos.Usuario;
import proyecto_final.dw.servicios.UsuarioService;


@RestController
@RequestMapping("api/usuarios")
@CrossOrigin("http://localhost:4200")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> obtenerUsuario() {
        return usuarioService.obtenerTodosUsuarios();
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        Usuario nuevoUsuario = usuarioService.crearUsuarioConRol(usuarioRequest);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRequest> actualizarUsuario(@PathVariable Long id,
                                                            @RequestBody Usuario usuario,
                                                            @RequestParam Long idDepartamento,
                                                            @RequestParam Long idHorario,
                                                            @RequestParam Set<Long> idsRoles) {
        try {
            UsuarioRequest usuarioActualizadoDTO = usuarioService.actualizarUsuario(id, usuario, idDepartamento, idHorario, idsRoles);
            return new ResponseEntity<>(usuarioActualizadoDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    
}
