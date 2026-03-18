package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.util.ScriptUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptUtilsTest {

    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        utils = new ScriptUtils();
    }

    @Test
    void shouldReturnOptionalIntWhenConvertingValidNumberString() {
        Optional<Integer> result = utils.toInt("42".getBytes());

        assertTrue(result.isPresent());
        assertEquals(42, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenConvertingInvalidString() {
        Optional<Integer> result = utils.toInt("abc".getBytes());

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnTrueWhenValueIsNonZero() {
        assertTrue(utils.isTrue("42".getBytes()));
        assertTrue(utils.isTrue("1".getBytes()));
    }

    @Test
    void shouldReturnFalseWhenValueIsZeroOrEmpty() {
        assertFalse(utils.isTrue("0".getBytes()));
        assertFalse(utils.isTrue(new byte[0]));
    }
}