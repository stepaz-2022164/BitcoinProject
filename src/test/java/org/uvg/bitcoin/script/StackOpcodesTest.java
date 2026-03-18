package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.StackOpcodes;
import org.uvg.bitcoin.script.util.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class StackOpcodesTest {

    private ExecutionContext context;
    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
        utils = new ScriptUtils();
    }

    @Test
    void shouldDuplicateTopElementWhenStackHasElements() {
        context.push("value".getBytes());

        boolean result = StackOpcodes.dup(context, utils);

        assertTrue(result);
        assertEquals(2, context.stackSize());
        assertArrayEquals("value".getBytes(), context.pop());
        assertArrayEquals("value".getBytes(), context.pop());
    }

    @Test
    void shouldFailDupWhenStackIsEmpty() {
        boolean result = StackOpcodes.dup(context, utils);

        assertFalse(result);
    }

    @Test
    void shouldSwapTopTwoElementsWhenStackHasAtLeastTwo() {
        context.push("first".getBytes());
        context.push("second".getBytes());

        boolean result = StackOpcodes.swap(context, utils);

        assertTrue(result);
        assertArrayEquals("first".getBytes(), context.pop());
        assertArrayEquals("second".getBytes(), context.pop());
    }

    @Test
    void shouldCopySecondElementToTopWhenStackHasAtLeastTwo() {
        // Limpiar contexto
        context = new ExecutionContext(false, false);

        // Push elementos
        byte[] bottom = "bottom".getBytes();
        byte[] top = "top".getBytes();

        System.out.println("bottom length: " + bottom.length);
        System.out.println("top length: " + top.length);

        context.push(bottom);
        context.push(top);

        System.out.println("Stack size before OVER: " + context.stackSize());

        boolean result = StackOpcodes.over(context, utils);

        System.out.println("Stack size after OVER: " + context.stackSize());
        assertTrue(result);
        assertEquals(3, context.stackSize());

        // Verificar cada elemento
        byte[] popped1 = context.pop();
        byte[] popped2 = context.pop();
        byte[] popped3 = context.pop();

        System.out.println("popped1: '" + new String(popped1) + "', length: " + popped1.length);
        System.out.println("popped2: '" + new String(popped2) + "', length: " + popped2.length);
        System.out.println("popped3: '" + new String(popped3) + "', length: " + popped3.length);

        // Verificaciones
        assertArrayEquals(bottom, popped1, "First pop should be 'bottom' (copied)");
        assertArrayEquals(top, popped2, "Second pop should be 'top'");
        assertArrayEquals(bottom, popped3, "Third pop should be 'bottom' (original)");
    }
}