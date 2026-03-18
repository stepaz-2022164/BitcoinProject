package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;

/**
 * Interfaz funcional para todos los opcodes de Bitcoin Script.
 * Cada opcode debe implementar este método execute.
 */
@FunctionalInterface
public interface Opcode {
    /**
     * Ejecuta el opcode en el contexto dado.
     *
     * @param context Contexto de ejecución actual
     * @return true si la ejecución fue exitosa, false en caso contrario
     */
    boolean execute(ExecutionContext context);
}