package services.auth;

import daos.AuthUserDAO;
import dtos.auth.LoginRequestDTO;
import dtos.auth.LoginResponseDTO;
import exceptions.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.people.User;
import utils.JwtUtil;
import utils.PasswordHasher;

@RequestScoped
public class AuthService {

    @Inject
    AuthUserDAO authUserDAO;

    @Inject
    PasswordHasher passwordHasher;

    @Inject
    JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO credentials) {
        User user = authUserDAO.findByEmail(credentials.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!passwordHasher.check(credentials.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Usuario no habilitado");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponseDTO(token);
    }
}

