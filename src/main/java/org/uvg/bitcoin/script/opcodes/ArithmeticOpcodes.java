package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.Optional;

/**
 * Implementación de todos los opcodes aritméticos.
 */
public class ArithmeticOpcodes {

    /**
     * OP_ADD: Suma dos números.
     * Antes: [a, b] → Después: [a+b]
     */
    public static boolean add(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() + b.get()));
        return true;
    }

    /**
     * OP_SUB: Resta dos números (a - b).
     * Antes: [a, b] → Después: [a-b]
     */
    public static boolean sub(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() - b.get()));
        return true;
    }

    /**
     * OP_NUMEQUALVERIFY: Verifica igualdad numérica y elimina si es true.
     * Antes: [a, b] → Después: [] (si a == b)
     */
    public static boolean numEqualVerify(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        if (!a.get().equals(b.get())) {
            return false;
        }

        return true; // Verificación exitosa, operandos ya eliminados
    }

    /**
     * OP_LESSTHAN: Compara si a < b.
     * Antes: [a, b] → Después: [1] si a < b, [0] si no
     */
    public static boolean lessThan(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() < b.get() ? 1 : 0));
        return true;
    }

    /**
     * OP_GREATERTHAN: Compara si a > b.
     * Antes: [a, b] → Después: [1] si a > b, [0] si no
     */
    public static boolean greaterThan(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() > b.get() ? 1 : 0));
        return true;
    }

    /**
     * OP_LESSTHANOREQUAL: Compara si a <= b.
     * Antes: [a, b] → Después: [1] si a <= b, [0] si no
     */
    public static boolean lessThanOrEqual(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() <= b.get() ? 1 : 0));
        return true;
    }

    /**
     * OP_GREATERTHANOREQUAL: Compara si a >= b.
     * Antes: [a, b] → Después: [1] si a >= b, [0] si no
     */
    public static boolean greaterThanOrEqual(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        Optional<Integer> b = utils.toInt(ctx.pop());
        Optional<Integer> a = utils.toInt(ctx.pop());

        if (!a.isPresent() || !b.isPresent()) {
            return false;
        }

        ctx.push(utils.serializeNumber(a.get() >= b.get() ? 1 : 0));
        return true;
    }
}