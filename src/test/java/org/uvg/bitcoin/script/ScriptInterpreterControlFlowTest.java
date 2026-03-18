/**
 * Conjunto de pruebas unitarias para validar el comportamiento del intérprete
 * de Bitcoin Script.
 *
 * Se evalúan:
 * - Control de flujo (OP_IF, OP_ELSE, OP_ENDIF)
 * - Validación (OP_VERIFY, OP_RETURN)
 * - Operaciones aritméticas y lógicas
 *
 * Incluye pruebas de ejecución correcta, fallos esperados y casos borde
 * como pila vacía y tipos inválidos.
 */
package org.uvg.bitcoin.script;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptInterpreterControlFlowTest {

    @Test
    void verifyTrueShouldPass() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_VERIFY");
        assertTrue(interpreter.execute(script));
    }

    @Test
    void verifyFalseShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_0", "OP_VERIFY");
        assertFalse(interpreter.execute(script));
    }

    @Test
    void opReturnShouldAlwaysFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_RETURN", "OP_1");
        assertFalse(interpreter.execute(script));
    }

    @Test
    void ifTrueExecutesBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_IF", "OP_2", "OP_ENDIF");
        assertTrue(interpreter.execute(script));
    }

    @Test
    void ifFalseSkipsBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_0", "OP_IF", "OP_2", "OP_ENDIF");
        assertTrue(interpreter.execute(script));
    }

    @Test
    void ifElseExecutesElseBranch() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "OP_0", "OP_IF", "OP_2",
                "OP_ELSE", "OP_3",
                "OP_ENDIF"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void nestedIfShouldWorkCorrectly() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "OP_1", "OP_IF",
                "OP_1", "OP_IF",
                "OP_2",
                "OP_ENDIF",
                "OP_ENDIF"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void complexScriptShouldPass() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "OP_1", "OP_IF",
                "5", "7", "OP_ADD",
                "12", "OP_EQUAL",
                "OP_VERIFY",
                "OP_ELSE",
                "OP_0",
                "OP_ENDIF"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void addShouldWork() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "5", "7",
                "OP_ADD",
                "12",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void subShouldWork() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "10", "3",
                "OP_SUB",
                "7",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void addWithEmptyStackShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_ADD");
        assertFalse(interpreter.execute(script));
    }

    @Test
    void subWithOneElementShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("5", "OP_SUB");
        assertFalse(interpreter.execute(script));
    }

    @Test
    void addWithZeroShouldWork() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "0", "5",
                "OP_ADD",
                "5",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void negativeResultShouldWork() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "3", "5",
                "OP_SUB",
                "-2",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void boolAndFalseCase() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "1", "0",
                "OP_BOOLAND",
                "0",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void boolOrFalseCase() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList(
                "0", "0",
                "OP_BOOLOR",
                "0",
                "OP_EQUAL"
        );
        assertTrue(interpreter.execute(script));
    }

    @Test
    void invalidTypeShouldThrowException() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("A", "B", "OP_ADD");

        assertThrows(NumberFormatException.class, () -> {
            interpreter.execute(script);
        });
    }
}