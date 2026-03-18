package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

/**
 * Implementación de opcodes criptográficos y de firma (simulados).
 */
public class CryptoOpcodes {

    /**
     * OP_HASH160: SHA256 + RIPEMD160 (simulado).
     * Antes: [data] → Después: [HASH160_data]
     */
    public static boolean hash160(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }

        byte[] data = ctx.pop();
        String hash = "HASH160_" + new String(data);
        ctx.push(hash.getBytes());
        return true;
    }

    /**
     * OP_SHA256: SHA256 (simulado).
     * Antes: [data] → Después: [SHA256_data]
     */
    public static boolean sha256(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }

        byte[] data = ctx.pop();
        String hash = "SHA256_" + new String(data);
        ctx.push(hash.getBytes());
        return true;
    }

    /**
     * OP_HASH256: Doble SHA256 (simulado).
     * Antes: [data] → Después: [HASH256_data]
     */
    public static boolean hash256(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }

        byte[] data = ctx.pop();
        String hash = "HASH256_" + new String(data);
        ctx.push(hash.getBytes());
        return true;
    }

    /**
     * OP_CHECKSIG: Verifica firma (simulado).
     * Antes: [firma, pubKey] → Después: [1] si firma es válida, [0] si no
     */
    public static boolean checkSig(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        byte[] pubKey = ctx.pop();
        byte[] signature = ctx.pop();

        // Simulación: firma válida si contiene "VALID"
        boolean valid = new String(signature).contains("VALID");
        ctx.push(utils.serializeNumber(valid ? 1 : 0));
        return true;
    }

    /**
     * OP_CHECKSIGVERIFY: OP_CHECKSIG + OP_VERIFY.
     * Antes: [firma, pubKey] → Después: [] si firma válida, falla si no
     */
    public static boolean checkSigVerify(ExecutionContext ctx, ScriptUtils utils) {
        if (!checkSig(ctx, utils)) {
            return false;
        }
        return FlowControlOpcodes.verify(ctx, utils);
    }
}