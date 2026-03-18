package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.Arrays;

/**
 * Implementación de todos los opcodes lógicos y de comparación.
 */
public class LogicalOpcodes {

    /**
     * OP_EQUAL: Compara dos elementos (byte a byte).
     * Antes: [a, b] → Después: [1] si a == b, [0] si no
     */
    public static boolean equal(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        byte[] a = ctx.pop();
        byte[] b = ctx.pop();

        boolean result = Arrays.equals(a, b);
        ctx.push(utils.serializeNumber(result ? 1 : 0));
        return true;
    }

    /**
     * OP_EQUALVERIFY: Combina OP_EQUAL + OP_VERIFY.
     * Antes: [a, b] → Después: [] si a == b, falla si no
     */
    public static boolean equalVerify(ExecutionContext ctx, ScriptUtils utils) {
        if (!equal(ctx, utils)) {
            return false;
        }
        return FlowControlOpcodes.verify(ctx, utils);
    }

    /**
     * OP_NOT: Negación lógica (0 → 1, cualquier otro → 0).
     * Antes: [x] → Después: [1] si x == 0, [0] si no
     */
    public static boolean not(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }

        boolean val = utils.isTrue(ctx.pop());
        ctx.push(utils.serializeNumber(val ? 0 : 1));
        return true;
    }

    /**
     * OP_BOOLAND: AND lógico.
     * Antes: [a, b] → Después: [1] si a y b son true, [0] si no
     */
    public static boolean boolAnd(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        boolean b = utils.isTrue(ctx.pop());
        boolean a = utils.isTrue(ctx.pop());

        ctx.push(utils.serializeNumber((a && b) ? 1 : 0));
        return true;
    }

    /**
     * OP_BOOLOR: OR lógico.
     * Antes: [a, b] → Después: [1] si a o b son true, [0] si no
     */
    public static boolean boolOr(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        boolean b = utils.isTrue(ctx.pop());
        boolean a = utils.isTrue(ctx.pop());

        ctx.push(utils.serializeNumber((a || b) ? 1 : 0));
        return true;
    }
}