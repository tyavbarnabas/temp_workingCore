package com.kenpb.app.serviceImplementation;

import com.kenpb.app.constants.GeneralResponseEnum;
import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.RegistrationRequest;
import com.kenpb.app.exceptions.ModuleExceptionHandler;
import com.kenpb.app.repositories.RoleRepository;
import com.kenpb.app.repositories.TokenRepository;
import com.kenpb.app.repositories.UserRepository;
import com.kenpb.app.service.AuthenticationService;
import com.kenpb.app.user.Token;
import com.kenpb.app.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private  final TokenRepository tokenRepository;

    @Override
    public ApiResponse register(RegistrationRequest request) {

        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ModuleExceptionHandler.UserRoleNotFoundException("Role not found"));

        log.info("Role found: {}", role.getName());

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();

        log.info("User created: {}", user);

        User saveUser = userRepository.save(user);
        log.info("User saved: {}", saveUser);

       //sendValidationEmail(user);

        return ApiResponse.builder()
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .statusCode(String.valueOf(HttpStatus.CREATED))
                .build();


    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);
        //send email
    }

    private Object generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generatedActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generatedActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
