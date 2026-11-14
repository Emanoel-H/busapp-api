package br.com.javamastery.busapp_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService service;

    @BeforeEach
    void setUp(){
        service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", "uma-chave-de-teste-com-mais-de-32-caracteres!");
        ReflectionTestUtils.setField(service, "expiration", 3_600_000L);
    }

    @Test
    @DisplayName("Should return valid token when token is generated")
    void validToken(){
        String token = service.generateToken("empresa@teste.com", "COMPANY");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(service.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Should return valid token when email is correct")
    void validToken_correctEmail(){
        String token = service.generateToken("viajante@teste.com", "TRAVELER");

        assertThat(service.extractEmail(token)).isEqualTo("viajante@teste.com");
    }

    @Test
    @DisplayName("Should return valid token when role is correct")
    void validToken_correctRole(){
        String token = service.generateToken("viajante@teste.com", "TRAVELER");

        assertThat(service.extractRole(token)).isEqualTo("TRAVELER");
    }

    @Test
    @DisplayName("Should return invalid token when token is expired")
    void invalidToken_expired(){
        JwtService jwtServiceTeste = new JwtService();
        ReflectionTestUtils.setField(jwtServiceTeste, "secret", "uma-chave-de-teste-com-mais-de-32-caracteres!");
        ReflectionTestUtils.setField(jwtServiceTeste, "expiration", -1000L);

        String token = jwtServiceTeste.generateToken("empresa@teste.com", "COMPANY");

        assertThat(jwtServiceTeste.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Should return invalid token when token secret is incorrect")
    void invalidToken_incorrectSecret(){
        JwtService jwtServiceTeste = new JwtService();
        ReflectionTestUtils.setField(jwtServiceTeste, "secret", "outra-chave-de-teste-com-mais-de-32-caracteres!");
        ReflectionTestUtils.setField(jwtServiceTeste, "expiration", 3_600_000L);

        String token = jwtServiceTeste.generateToken("empresa@teste.com", "COMPANY");

        assertThat(jwtServiceTeste.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Should return invalid token when role is incorrect")
    void invalidToken_incorrectRole(){
        String token = service.generateToken("viajante@teste.com", "lixo");

        assertThat(service.isTokenValid(token)).isFalse();
    }
}
