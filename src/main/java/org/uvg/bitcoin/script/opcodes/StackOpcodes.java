package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de todos los opcodes de manipulación de pila.
 */
public class StackOpcodes {

    /**
     * OP_DUP: Duplica el elemento superior de la pila.
     * Antes: [x] → Después: [x, x]
     */
    public static boolean dup(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }
        byte[] top = ctx.peek();
        ctx.push(utils.copyOf(top));
        return true;
    }

    /**
     * OP_DROP: Elimina el elemento superior de la pila.
     * Antes: [x, ...] → Después: [...]
     */
    public static boolean drop(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }
        ctx.pop();
        return true;
    }

    /**
     * OP_SWAP: Intercambia los dos elementos superiores.
     * Antes: [x1, x2, ...] → Después: [x2, x1, ...]
     */
    public static boolean swap(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }
        byte[] top = ctx.pop();
        byte[] second = ctx.pop();
        ctx.push(top);
        ctx.push(second);
        return true;
    }

    /**
     * OP_OVER: Copia el segundo elemento a la cima.
     * Antes: [x1, x2, ...] → Después: [x1, x2, x1, ...]
     *
     * Ejemplo: [bottom, top] → después de OVER: [bottom, top, bottom]
     */
    public static boolean over(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        // Guardar toda la pila temporalmente
        List<byte[]> temp = new ArrayList<>();
        while (!ctx.isStackEmpty()) {
            temp.add(0, ctx.pop()); // Insertar al principio para mantener orden
        }

        // temp ahora tiene [bottom, top] (bottom en índice 0, top en índice 1)

        // Reconstruir la pila con OVER aplicado
        for (byte[] item : temp) {
            ctx.push(item);
        }

        // El segundo elemento es temp.get(0) (bottom)
        ctx.push(utils.copyOf(temp.get(0))); // Copiar bottom a la cima

        return true;
    }
}