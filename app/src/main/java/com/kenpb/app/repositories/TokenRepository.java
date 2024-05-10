package com.kenpb.app.repositories;

import com.kenpb.app.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long>{

    Optional<Token> findByToken(String token);

;
}
