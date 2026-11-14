package br.com.javamastery.busapp_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService service;

    @BeforeEach
    void setUp(){
        service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", "uma-chave-de-teste-com-mais-de-32-caracteres!");
        ReflectionTestUtils.setField(service, "expiration", 3_600_000L);
    }
}
