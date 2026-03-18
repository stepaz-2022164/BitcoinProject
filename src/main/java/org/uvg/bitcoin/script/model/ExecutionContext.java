package org.uvg.bitcoin.script.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Contexto de ejecución del intérprete.
 * Mantiene el estado actual: pila principal, pila de control de flujo,
 * y flags de ejecución.
 */
public class ExecutionContext {
    private final Deque<byte[]> stack;
    private final Deque<Boolean> ifStack;
    private boolean executing;
    private boolean skipElse;
    private final boolean trace;
    private final boolean stepByStep;

    /**
     * Constructor del contexto de ejecución.
     *
     * @param trace       Indica si se imprime el estado paso a paso
     * @param stepByStep  Indica si se pausa en cada paso
     */
    public ExecutionContext(boolean trace, boolean stepByStep) {
        this.stack = new ArrayDeque<>();
        this.ifStack = new ArrayDeque<>();
        this.executing = true;
        this.skipElse = false;
        this.trace = trace;
        this.stepByStep = stepByStep;
    }

    /**
     * Reinicia el contexto para una nueva ejecución.
     */
    public void reset() {
        stack.clear();
        ifStack.clear();
        executing = true;
        skipElse = false;
    }

    /**
     * Empuja un valor a la pila.
     */
    public void push(byte[] value) {
        stack.push(value);
    }

    /**
     * Extrae y retorna el valor superior de la pila.
     */
    public byte[] pop() {
        return stack.pop();
    }

    /**
     * Retorna el valor superior sin extraerlo.
     */
    public byte[] peek() {
        return stack.peek();
    }

    /**
     * Verifica si la pila está vacía.
     */
    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    /**
     * Retorna el tamaño de la pila.
     */
    public int stackSize() {
        return stack.size();
    }

    /**
     * Limpia la pila.
     */
    public void clearStack() {
        stack.clear();
    }

    /**
     * Retorna la pila (para iteración).
     */
    public Iterable<byte[]> getStack() {
        return stack;
    }

    /**
     * Empuja un estado de IF a la pila de control.
     */
    public void pushIf(boolean value) {
        ifStack.push(value);
    }

    /**
     * Extrae y retorna el estado superior de la pila de control.
     */
    public boolean popIf() {
        return ifStack.pop();
    }

    /**
     * Verifica si la pila de control está vacía.
     */
    public boolean isIfStackEmpty() {
        return ifStack.isEmpty();
    }

    /**
     * Retorna el tamaño de la pila de control.
     */
    public int getIfStackSize() {
        return ifStack.size();
    }

    public boolean isExecuting() {
        return executing;
    }

    public void setExecuting(boolean executing) {
        this.executing = executing;
    }

    public boolean isSkipElse() {
        return skipElse;
    }

    public void setSkipElse(boolean skipElse) {
        this.skipElse = skipElse;
    }

    public boolean isTrace() {
        return trace;
    }

    public boolean isStepByStep() {
        return stepByStep;
    }
}