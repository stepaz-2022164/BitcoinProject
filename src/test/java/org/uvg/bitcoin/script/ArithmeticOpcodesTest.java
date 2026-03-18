package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.ArithmeticOpcodes;
import org.uvg.bitcoin.script.util.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;

public class ArithmeticOpcodesTest {

    private ExecutionContext context;
    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
        utils = new ScriptUtils();
    }

    @Test
    void shouldAddTwoNumbersWhenStackHasTwoElements() {
        context.push("5".getBytes());
        context.push("3".getBytes());

        boolean result = ArithmeticOpcodes.add(context, utils);

        assertTrue(result);
        assertEquals(1, context.stackSize());
        assertEquals(8, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldSubtractTwoNumbersWhenStackHasTwoElements() {
        context.push("10".getBytes());
        context.push("4".getBytes());

        boolean result = ArithmeticOpcodes.sub(context, utils);

        assertTrue(result);
        assertEquals(1, context.stackSize());
        assertEquals(6, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldReturnTrueWhenNumbersAreEqualInNumEqualVerify() {
        context.push("42".getBytes());
        context.push("42".getBytes());

        boolean result = ArithmeticOpcodes.numEqualVerify(context, utils);

        assertTrue(result);
        assertEquals(0, context.stackSize());
    }

    @Test
    void shouldReturnFalseWhenNumbersAreNotEqualInNumEqualVerify() {
        context.push("42".getBytes());
        context.push("24".getBytes());

        boolean result = ArithmeticOpcodes.numEqualVerify(context, utils);

        assertFalse(result);
    }
}