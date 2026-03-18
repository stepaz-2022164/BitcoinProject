package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutionContextTest {

    private ExecutionContext context;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
    }

    @Test
    void shouldPushAndPopElementsFromStack() {
        context.push("test".getBytes());
        context.push("data".getBytes());

        assertEquals(2, context.stackSize());
        assertArrayEquals("data".getBytes(), context.pop());
        assertArrayEquals("test".getBytes(), context.pop());
        assertTrue(context.isStackEmpty());
    }

    @Test
    void shouldManageIfStackCorrectly() {
        context.pushIf(true);
        context.pushIf(false);

        assertEquals(2, context.getIfStackSize());
        assertFalse(context.popIf());
        assertTrue(context.popIf());
        assertTrue(context.isIfStackEmpty());
    }

    @Test
    void shouldResetToInitialState() {
        context.push("data".getBytes());
        context.pushIf(true);
        context.setExecuting(false);

        context.reset();

        assertTrue(context.isStackEmpty());
        assertTrue(context.isIfStackEmpty());
        assertTrue(context.isExecuting());
    }

    @Test
    void shouldReturnIterableStackForReading() {
        context.push("first".getBytes());
        context.push("second".getBytes());

        int count = 0;
        for (byte[] item : context.getStack()) {
            count++;
        }
        assertEquals(2, count);
    }
}