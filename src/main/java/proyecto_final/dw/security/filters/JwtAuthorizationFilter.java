package proyecto_final.dw.security.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import proyecto_final.dw.security.jwt.JwtUtils;
import proyecto_final.dw.servicios.UserDetailsServiceImpl;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    // Metodo que se ejecuta en cada peticion
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extraer el token del encabezado
        String tokenTitle = request.getHeader("Authorization");

        // Verificar si el token es nulo o no empieza con Bearer para extraer el
        // tokenbody
        if (tokenTitle != null && tokenTitle.startsWith("Bearer ")) {
            // Extraer el tokenbody
            String tokenBody = tokenTitle.substring(7, tokenTitle.length());

            // Verificar si el token es valido
            if (jwtUtils.isTokenValid(tokenBody)) {
                // Extraer el username del token
                String username = jwtUtils.getUsernameToken(tokenBody);
                // Cargar los detalles del usuario por el username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear la autenticacion
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, userDetails.getAuthorities());

                // Establecer la autenticacion en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // Si el token no es valido o no existe, se sigue con la cadena de filtros
        filterChain.doFilter(request, response);
    }

}