package proyecto_final.dw.security.filters;

import java.util.HashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import proyecto_final.dw.modelos.Usuario;
import proyecto_final.dw.repositorios.UsuarioRepository;
import proyecto_final.dw.security.jwt.JwtUtils;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtils jwtUtils;
    private UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UsuarioRepository usuarioRepository) {
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        Usuario usuario = null;
        String username = "";
        String password = "";

        try {
            usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
            // Aca puedo mapear el correo tambien, testeare el username xd
            username = usuario.getUsername();
            password = usuario.getPassword();

        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        // Obtener los detalles del usuario autenticado
        User user = (User) authResult.getPrincipal();

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Generar el token JWT
        String token = jwtUtils.generateAccessToken(user.getUsername());

        System.out.println("Generated Token: " + token);

        // Añadir el token en el encabezado de la respuesta
        response.addHeader("Authorization", "Bearer " + token);

        // Crear el cuerpo de la respuesta
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("empleadoId", usuario.getIdUsuario());
        httpResponse.put("username", user.getUsername());
        httpResponse.put("message", "Autenticación correcta");

        // Establecer el estado y el tipo de contenido de la respuesta
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Escribir la respuesta en formato JSON
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.getWriter().flush();
    }
}
