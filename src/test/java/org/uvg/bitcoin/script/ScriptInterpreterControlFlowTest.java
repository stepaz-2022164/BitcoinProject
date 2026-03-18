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

    /**
     * Comprueba que OP_VERIFY no trunque la ejecución cuando lee un valor verdadero ('1').
     */
    @Test
    void verifyTrueShouldPass() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_VERIFY");
        assertTrue(interpreter.execute(script));
    }

    /**
     * Verifica que OP_VERIFY falle y devuelva falso al encontrar un valor '0' en el top de la pila.
     */
    @Test
    void verifyFalseShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_0", "OP_VERIFY");
        assertFalse(interpreter.execute(script));
    }

    /**
     * Asegura que el uso de OP_RETURN corte siempre la ejecución retornando falso.
     */
    @Test
    void opReturnShouldAlwaysFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_RETURN", "OP_1");
        assertFalse(interpreter.execute(script));
    }

    /**
     * Valida la ejecución correcta de los comandos dentro de un bloque OP_IF con una condición verdadera.
     */
    @Test
    void ifTrueExecutesBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_1", "OP_IF", "OP_2", "OP_ENDIF");
        assertTrue(interpreter.execute(script));
    }

    /**
     * Comprueba que los comandos de un OP_IF sean completamente evadidos cuando la condición es falsa.
     */
    @Test
    void ifFalseSkipsBlock() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_0", "OP_IF", "OP_2", "OP_ENDIF");
        assertTrue(interpreter.execute(script));
    }

    /**
     * Verifica el salto correcto a la rama OP_ELSE en caso de recibir una condición falsa en un OP_IF.
     */
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

    /**
     * Prueba exhaustiva del comportamiento del intérprete procesando bloques OP_IF anidados dentro de otros bloques OP_IF.
     */
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

    /**
     * Ejecuta una prueba de caso integral que acopla matemática, condicionales y evaluación.
     * La prueba simula cálculos en un bloque IF ignorando correctamente la rama ELSE.
     */
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

    /**
     * Verifica la funcionalidad estándar y exitosa de la operación OP_ADD (Suma).
     */
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

    /**
     * Verifica la funcionalidad estándar y exitosa de la operación OP_SUB (Resta).
     */
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

    /**
     * Comprueba el fallo de un operador binario al recibir una pila sin elementos suficientes.
     */
    @Test
    void addWithEmptyStackShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("OP_ADD");
        assertFalse(interpreter.execute(script));
    }

    /**
     * Comprueba el fallo del intérprete en operadores aritméticos cuando solo hay un elemento.
     */
    @Test
    void subWithOneElementShouldFail() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("5", "OP_SUB");
        assertFalse(interpreter.execute(script));
    }

    /**
     * Asegura el soporte a sumar valor cero sin fallos aritméticos.
     */
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

    /**
     * Verifica el comportamiento apropiado con números y resultados negativos.
     */
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

    /**
     * Evalúa la operación OP_BOOLAND resolviendo correctamente a 0 cuando hay escenarios dispares (true y false).
     */
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

    /**
     * Evalúa la operación OP_BOOLOR resolviendo a 0 cuando ambos escenarios son falsos (0 y 0).
     */
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

    /**
     * Asegura el lanzamiento de excepciones cuando se tratan de procesar operaciones matemáticas con tipos que no son representaciones numéricas.
     */
    @Test
    void invalidTypeShouldThrowException() {
        ScriptInterpreter interpreter = new ScriptInterpreter(false);
        List<String> script = Arrays.asList("A", "B", "OP_ADD");

        assertThrows(NumberFormatException.class, () -> {
            interpreter.execute(script);
        });
    }
}