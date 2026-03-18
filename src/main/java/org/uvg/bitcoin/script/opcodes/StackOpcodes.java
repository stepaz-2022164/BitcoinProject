package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.Iterator;

/**
 * Implementación de todos los opcodes de manipulación de pila.
 */
public class StackOpcodes {

    /**
     * OP_DUP: Duplica el elemento superior de la pila.
     * Antes: [x] → Después: [x, x]
     *
     * @param ctx   El contexto de ejecución de la máquina virtual.
     * @param utils Clase de utilidades para operar conversiones de arreglos.
     * @return true si la operación es exitosa, false si la pila está vacía.
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
     *
     * @param ctx   El contexto de ejecución actual.
     * @param utils Clase de utilidades auxiliar.
     * @return true si la operación es exitosa, false si no hay datos a eliminar.
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
     *
     * @param ctx   El contexto de ejecución actual.
     * @param utils Clase de utilidades auxiliar.
     * @return true si el intercambio fue exitoso, false si hay menos de dos elementos.
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
     * @param ctx   El contexto de ejecución actual.
     * @param utils Clase de utilidades auxiliar.
     * @return true si la operación es exitosa, false si la pila tiene menos de 2 elementos.
     */
    public static boolean over(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.stackSize() < 2) {
            return false;
        }

        byte[] second = null;
        Iterator<byte[]> it = ctx.getStack().iterator();

        for (int i = 0; i < ctx.stackSize() - 2; i++) {
            it.next();
        }
        second = it.next();

        ctx.push(utils.copyOf(second));
        return true;
    }
}