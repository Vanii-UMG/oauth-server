package proyecto_final.dw.dtos.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private long idUsuario;
    private long idHorario;
    private long idDepartamento;
    private Set<Long> idRol;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean enabled;

    // Constructor, getters y setters
    public UsuarioResponse(Long idUsuario, Long idHorario, Long idDepartamento, Set<Long> idsRol, String nombre, String apellido, String email, String telefono, boolean enabled) {
        this.idUsuario = idUsuario;
        this.idHorario = idHorario;
        this.idDepartamento = idDepartamento;
        this.idRol = idsRol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.enabled = enabled;
    }

}
