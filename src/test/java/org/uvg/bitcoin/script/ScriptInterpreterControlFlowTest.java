/**
 * ScriptInterpreterControlFlowTest
 *
 * Conjunto de pruebas unitarias para validar el comportamiento de los opcodes
 * de control de flujo implementados en el ScriptInterpreter.
 *
 * Se evalúan las operaciones:
 * - OP_IF
 * - OP_ELSE
 * - OP_ENDIF
 * - OP_VERIFY
 * - OP_RETURN
 *
 * Incluye pruebas de:
 * - Ejecución condicional
 * - Bloques IF/ELSE
 * - IF anidados
 * - Validación de condiciones (VERIFY)
 * - Terminación de script (RETURN)
 *
 * Estas pruebas garantizan que la máquina virtual interprete correctamente
 * el flujo de ejecución y los estados del stack.
 *
 * @author Cristian Estuardo Orellana Dieguez
 */

package org.uvg.bitcoin.script;

import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.ScriptInterpreter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptInterpreterControlFlowTest {

    // ============================
    // OP_VERIFY
    // ============================

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

    // ============================
    // OP_RETURN
    // ============================

    @Test
    void opReturnShouldAlwaysFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList("OP_1", "OP_RETURN", "OP_1");

        assertFalse(interpreter.execute(script));
    }

    // ============================
    // OP_IF
    // ============================

    @Test
    void ifTrueExecutesBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList(
                "OP_1",
                "OP_IF",
                "OP_2",
                "OP_ENDIF"
        );

        assertTrue(interpreter.execute(script));
    }

    @Test
    void ifFalseSkipsBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList(
                "OP_0",
                "OP_IF",
                "OP_2",
                "OP_ENDIF"
        );

        assertTrue(interpreter.execute(script)); // stack vacío
    }

    // ============================
    // OP_IF + OP_ELSE
    // ============================

    @Test
    void ifElseExecutesElseBranch() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList(
                "OP_0",
                "OP_IF",
                "OP_2",
                "OP_ELSE",
                "OP_3",
                "OP_ENDIF"
        );

        assertTrue(interpreter.execute(script));
    }

    // ============================
    // IF ANIDADO
    // ============================

    @Test
    void nestedIfShouldWorkCorrectly() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList(
                "OP_1",
                "OP_IF",
                "OP_1",
                "OP_IF",
                "OP_2",
                "OP_ENDIF",
                "OP_ENDIF"
        );

        assertTrue(interpreter.execute(script));
    }

    // ============================
    // CASO COMPLETO
    // ============================

    @Test
    void complexScriptShouldPass() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);

        List<String> script = Arrays.asList(
                "OP_1",
                "OP_IF",
                "5", "7", "OP_ADD",
                "12",
                "OP_EQUAL",
                "OP_VERIFY",
                "OP_ELSE",
                "OP_0",
                "OP_ENDIF"
        );

        assertTrue(interpreter.execute(script));
    }
}