package Javacode.Spring.Security1.configuration;

import Javacode.Spring.Security1.JWTAuth.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //Тут запросы от клиента к сервлету и проверка (наличия) прикреплённого JWT Token. Т.е. проверка подлинности аутентификации.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) //Получили запрос к серверу от клиента
            //(Postman), проверяем запрос на наличие JWT Token.
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);     //Извлекли token
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken); //Извлекли username из token
            } catch (Exception e) {
                // Handle exception
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //При условии, что имя пользователя не null
            //и Authentication object отсутствует в Security Context (т.е. пользователь (пока) не аутентифицирован)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); //Получили данные пользователя по имени

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) { //Проверка, token не истек и имя пользователя совпадает с инофрмацией в UserDetails
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); //Генерация новго Authentication object
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //Внесение данных в Authentication object
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); //Добавление Authentication object в Security Context,
                //теперь пользователь аутентифицирован.
            }
        }
        chain.doFilter(request, response); //продолжение работы с запросом, запрос передаётся следующему фильтру в цепочке (или to servlet).
                //В классе SecurityConfig указано, что следующий фильтр: UsernamePasswordAuthenticationFilter.class.
    }
}