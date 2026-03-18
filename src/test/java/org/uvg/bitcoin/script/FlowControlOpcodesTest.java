package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.FlowControlOpcodes;
import org.uvg.bitcoin.script.util.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;

public class FlowControlOpcodesTest {

    private ExecutionContext context;
    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
        utils = new ScriptUtils();
    }

    @Test
    void shouldReturnTrueWhenTopElementIsTrueInVerify() {
        context.push("1".getBytes());

        boolean result = FlowControlOpcodes.verify(context, utils);

        assertTrue(result);
        assertEquals(0, context.stackSize());
    }

    @Test
    void shouldReturnFalseWhenTopElementIsFalseInVerify() {
        context.push("0".getBytes());

        boolean result = FlowControlOpcodes.verify(context, utils);

        assertFalse(result);
        assertEquals(0, context.stackSize());
    }

    @Test
    void shouldReturnFalseWhenStackIsEmptyInVerify() {
        boolean result = FlowControlOpcodes.verify(context, utils);

        assertFalse(result);
    }

    @Test
    void shouldAlwaysReturnFalseForOpReturn() {
        boolean result = FlowControlOpcodes.opReturn(context, utils);

        assertFalse(result);
    }
}