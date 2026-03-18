package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.LogicalOpcodes;
import org.uvg.bitcoin.script.util.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;

public class LogicalOpcodesTest {

    private ExecutionContext context;
    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
        utils = new ScriptUtils();
    }

    @Test
    void shouldPushOneWhenEqualElementsAreCompared() {
        context.push("hello".getBytes());
        context.push("hello".getBytes());

        boolean result = LogicalOpcodes.equal(context, utils);

        assertTrue(result);
        assertEquals(1, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldPushZeroWhenDifferentElementsAreCompared() {
        context.push("hello".getBytes());
        context.push("world".getBytes());

        boolean result = LogicalOpcodes.equal(context, utils);

        assertTrue(result);
        assertEquals(0, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldNegateZeroToOne() {
        context.push("0".getBytes());

        boolean result = LogicalOpcodes.not(context, utils);

        assertTrue(result);
        assertEquals(1, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldPerformLogicalAndOperation() {
        context.push("1".getBytes());
        context.push("42".getBytes());

        boolean result = LogicalOpcodes.boolAnd(context, utils);

        assertTrue(result);
        assertEquals(1, Integer.parseInt(new String(context.peek())));
    }
}