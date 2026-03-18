package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.Opcode;
import org.uvg.bitcoin.script.opcodes.OpcodeRegistry;
import static org.junit.jupiter.api.Assertions.*;

public class OpcodeRegistryTest {

    private OpcodeRegistry registry;
    private ExecutionContext context;

    @BeforeEach
    void setUp() {
        registry = new OpcodeRegistry();
        context = new ExecutionContext(false, false);
    }

    @Test
    void shouldReturnOpcodeWhenRegisteredOpcodeIsRequested() {
        Opcode opcode = registry.getOpcode("OP_DUP");

        assertNotNull(opcode);
    }

    @Test
    void shouldReturnNullWhenUnknownOpcodeIsRequested() {
        Opcode opcode = registry.getOpcode("OP_UNKNOWN");

        assertNull(opcode);
    }

    @Test
    void shouldContainAllRequiredOpcodes() {
        // Verificar opcodes básicos
        assertTrue(registry.containsOpcode("OP_DUP"));
        assertTrue(registry.containsOpcode("OP_DROP"));
        assertTrue(registry.containsOpcode("OP_SWAP"));
        assertTrue(registry.containsOpcode("OP_OVER"));

        // Aritméticos
        assertTrue(registry.containsOpcode("OP_ADD"));
        assertTrue(registry.containsOpcode("OP_SUB"));

        // Lógicos
        assertTrue(registry.containsOpcode("OP_EQUAL"));
        assertTrue(registry.containsOpcode("OP_EQUALVERIFY"));

        // Control de flujo - NOTA: OP_IF no está directamente en el registro
        // porque se maneja en ScriptInterpreter, no como opcode
        assertTrue(registry.containsOpcode("OP_VERIFY"));
        assertTrue(registry.containsOpcode("OP_RETURN"));

        // Criptográficos
        assertTrue(registry.containsOpcode("OP_HASH160"));
        assertTrue(registry.containsOpcode("OP_SHA256"));
        assertTrue(registry.containsOpcode("OP_CHECKSIG"));

        // Literales
        assertTrue(registry.containsOpcode("OP_0"));
        assertTrue(registry.containsOpcode("OP_1"));
        assertTrue(registry.containsOpcode("OP_16"));
    }

    @Test
    void shouldPushCorrectValueForLiteralOpcodes() {
        // Probar OP_0 (debe empujar array vacío)
        Opcode opZero = registry.getOpcode("OP_0");
        assertNotNull(opZero);

        boolean resultZero = opZero.execute(context);
        assertTrue(resultZero);
        assertEquals(1, context.stackSize());
        assertEquals(0, context.peek().length); // OP_0 es array vacío

        // Probar OP_5 (debe empujar "5")
        Opcode opFive = registry.getOpcode("OP_5");
        assertNotNull(opFive);

        boolean resultFive = opFive.execute(context);
        assertTrue(resultFive);
        assertEquals(2, context.stackSize());

        // El tope ahora es "5" (el último empujado)
        byte[] top = context.pop();
        assertEquals("5", new String(top));

        // El siguiente es el array vacío de OP_0
        byte[] next = context.pop();
        assertEquals(0, next.length);
    }
}