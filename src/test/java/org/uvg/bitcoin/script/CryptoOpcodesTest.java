package org.uvg.bitcoin.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.CryptoOpcodes;
import org.uvg.bitcoin.script.util.ScriptUtils;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoOpcodesTest {

    private ExecutionContext context;
    private ScriptUtils utils;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext(false, false);
        utils = new ScriptUtils();
    }

    @Test
    void shouldPrefixDataWithHASH160WhenHash160IsCalled() {
        context.push("data".getBytes());

        boolean result = CryptoOpcodes.hash160(context, utils);

        assertTrue(result);
        assertEquals("HASH160_data", new String(context.peek()));
    }

    @Test
    void shouldPrefixDataWithSHA256WhenSha256IsCalled() {
        context.push("data".getBytes());

        boolean result = CryptoOpcodes.sha256(context, utils);

        assertTrue(result);
        assertEquals("SHA256_data", new String(context.peek()));
    }

    @Test
    void shouldReturnOneWhenSignatureIsValid() {
        context.push("VALID_SIG".getBytes());
        context.push("pubkey".getBytes());

        boolean result = CryptoOpcodes.checkSig(context, utils);

        assertTrue(result);
        assertEquals(1, Integer.parseInt(new String(context.peek())));
    }

    @Test
    void shouldReturnZeroWhenSignatureIsInvalid() {
        // Usar una firma que NO contenga "VALID"
        context.push("BAD_SIGNATURE".getBytes());  // Cambiado de "INVALID_SIG"
        context.push("pubkey".getBytes());

        boolean result = CryptoOpcodes.checkSig(context, utils);

        assertTrue(result);
        assertEquals(0, Integer.parseInt(new String(context.peek())));
    }
}