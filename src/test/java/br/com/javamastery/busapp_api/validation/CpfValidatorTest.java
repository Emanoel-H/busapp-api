package br.com.javamastery.busapp_api.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CpfValidatorTest {
    private final CpfValidator validator = new CpfValidator();

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "11144477735", "529.982.247-25"})
    @DisplayName("Should accept CPFs with valid check digits")
    void validCpf(String cpf){
        assertThat(validator.isValid(cpf, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224726", "11111111111", "1234567890", "123456789012", "abc"})
    @DisplayName("Should reject CPFs wrong digit, repeated digits, wrong length and non-numerics")
    void invalidCpf(String cpf){
        assertThat(validator.isValid(cpf, null)).isFalse();
    }

    @Test
    @DisplayName("Should not blow up on null (the @NotBlank annotation reports it)")
    void nullCpf(){
        assertThat(validator.isValid(null, null)).isTrue();
    }
}
