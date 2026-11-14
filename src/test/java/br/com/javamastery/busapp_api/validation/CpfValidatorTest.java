package br.com.javamastery.busapp_api.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CpfValidatorTest {
    private final CpfValidator validator = new CpfValidator();

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "11144477735", "529.982.247-25"})
    @DisplayName("Should accept CPFs with valid check digits")
    void validCpf(String cpf){
        assertThat(validator.isValid(cpf, null)).isTrue();
    }
}
