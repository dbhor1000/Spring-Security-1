package Javacode.Spring.Security1.controller;

import Javacode.Spring.Security1.JWTAuth.JwtTokenUtil;
import Javacode.Spring.Security1.dto.AuthenticationRequest;
import Javacode.Spring.Security1.dto.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    //Тут аутентификация пользователя по входным данным и изначальный вход с получением JWT Token, т.е. изначальная аутентификация.

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception { //1.Получен запрос на аутентификацию
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());     //2.1 Запрос передан AuthenticationManager для определения
                                                                                                //правильности входных данных. --->
                                                                                                //Authentication Manager передаст выполнение
        //логики аутентификации стандартному Authentication Provider ---> далее создается 'Authentication' object, представляющий данные запроса/проверит верность
        //даных в запросе. Если данные верные, Authentication object с данными пользователя
        //будет передан в Security Context. Метод продолжит выполнение.

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername()); //Загрузка данных пользователя по имени пользователя
                                                                                                                    //из входящего запроса для передачи
        //в метод генерации Token.
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token)); //Возвращаем Token.
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e); //2.2 Если данные для входа ошибочные, тогда Exception.
        }
    }
}