package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptInterpreterTest {

    private ScriptInterpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new ScriptInterpreter(false);
    }

    @Test
    void shouldValidateCorrectP2PKHScript() {
        List<String> script = Arrays.asList(
                "VALID_SIGNATURE", "PUBKEY_UVG",
                "OP_DUP", "OP_HASH160", "HASH160_PUBKEY_UVG",
                "OP_EQUALVERIFY", "OP_CHECKSIG"
        );

        boolean result = interpreter.execute(script);

        assertTrue(result);
    }

    @Test
    void shouldRejectP2PKHWithInvalidSignature() {
        List<String> script = Arrays.asList(
                "BAD_SIGNATURE", "PUBKEY_UVG",  // Cambiado de "INVALID_SIG" a "BAD_SIGNATURE"
                "OP_DUP", "OP_HASH160", "HASH160_PUBKEY_UVG",
                "OP_EQUALVERIFY", "OP_CHECKSIG"
        );

        boolean result = interpreter.execute(script);

        assertFalse(result);
    }

    @Test
    void shouldRejectP2PKHWithWrongPubKeyHash() {
        List<String> script = Arrays.asList(
                "VALID_SIGNATURE", "PUBKEY_UVG",
                "OP_DUP", "OP_HASH160", "WRONG_HASH",
                "OP_EQUALVERIFY", "OP_CHECKSIG"
        );

        boolean result = interpreter.execute(script);

        assertFalse(result);
    }

    @Test
    void shouldHandleSimpleIfStatement() {
        List<String> script = Arrays.asList(
                "OP_1", "OP_IF", "OP_42", "OP_ENDIF"
        );

        boolean result = interpreter.execute(script);

        assertTrue(result);
    }

    @Test
    void shouldHandleIfElseStatement() {
        List<String> script = Arrays.asList(
                "OP_0", "OP_IF", "OP_42", "OP_ELSE", "OP_24", "OP_ENDIF"
        );

        boolean result = interpreter.execute(script);

        assertTrue(result);
    }

    @Test
    void shouldHandleNestedIfStatements() {
        List<String> script = Arrays.asList(
                "OP_1", "OP_IF",
                "OP_1", "OP_IF",
                "OP_1",
                "OP_ELSE",
                "OP_0",
                "OP_ENDIF",
                "OP_ELSE",
                "OP_0",
                "OP_ENDIF"
        );

        boolean result = interpreter.execute(script);

        assertTrue(result);
    }

    @Test
    void shouldFailWhenScriptEndsWithEmptyStack() {
        List<String> script = Arrays.asList("OP_1", "OP_DROP");

        boolean result = interpreter.execute(script);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenIfConditionHasEmptyStack() {
        List<String> script = Arrays.asList("OP_IF", "OP_1", "OP_ENDIF");

        boolean result = interpreter.execute(script);

        assertFalse(result);
    }

    @Test
    void shouldAddTwoNumbers() {
        List<String> script = Arrays.asList("OP_5", "OP_3", "OP_ADD");

        boolean result = interpreter.execute(script);

        assertTrue(result);
    }

    @Test
    void shouldFailAddWithInsufficientStack() {
        List<String> script = Arrays.asList("OP_5", "OP_ADD");

        boolean result = interpreter.execute(script);

        assertFalse(result);
    }
}