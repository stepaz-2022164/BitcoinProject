package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

/**
 * Implementación de opcodes de control de flujo.
 */
public class FlowControlOpcodes {

    /**
     * OP_VERIFY: Verifica que el tope sea verdadero, si no, falla.
     * Antes: [x] → Después: [] si x es true, falla si no
     */
    public static boolean verify(ExecutionContext ctx, ScriptUtils utils) {
        if (ctx.isStackEmpty()) {
            return false;
        }

        if (!utils.isTrue(ctx.pop())) {
            return false;
        }

        return true;
    }

    /**
     * OP_RETURN: Marca el script como inválido.
     * Siempre retorna false para detener la ejecución.
     */
    public static boolean opReturn(ExecutionContext ctx, ScriptUtils utils) {
        return false; // Siempre falla
    }
}