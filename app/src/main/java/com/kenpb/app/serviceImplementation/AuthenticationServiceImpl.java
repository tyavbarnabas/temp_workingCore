package com.kenpb.app.serviceImplementation;

import com.kenpb.app.constants.GeneralResponseEnum;
import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.AuthenticationRequest;
import com.kenpb.app.dtos.AuthenticationResponse;
import com.kenpb.app.dtos.RegistrationRequest;
import com.kenpb.app.exceptions.ModuleExceptionHandler;

import com.kenpb.app.repositories.RoleRepository;
import com.kenpb.app.repositories.UserRepository;
import com.kenpb.app.security.JwtService;
import com.kenpb.app.service.AuthenticationService;
import com.kenpb.app.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


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
                .enabled(true)
                .roles(List.of(role))
                .build();
        log.info("User created: {}", user);

        User saveUser = userRepository.save(user);
        log.info("User saved: {}", saveUser);

        return ApiResponse.builder()
                .message(GeneralResponseEnum.SUCCESS.getMessage())
                .statusCode(String.valueOf(HttpStatus.CREATED))
                .build();


    }



    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        log.info("Entering the authenticate service... {}", request);

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                       request.getEmail(),
                       request.getPassword()
               )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new ModuleExceptionHandler.UserNotFoundException("User not found"));

        var claims = new HashMap<String, Object>();
        user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }


}
