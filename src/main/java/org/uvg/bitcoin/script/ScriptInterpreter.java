package org.uvg.bitcoin.script;

import java.util.*;
import java.util.function.Supplier;

/**
 * Intérprete de un subconjunto de Bitcoin Script basado en pila (stack-based).
 *
 * Este intérprete evalúa scripts de izquierda a derecha utilizando una pila LIFO.
 * Implementa operaciones de manipulación de pila, aritmética, lógica, comparación
 * y control de flujo (OP_IF, OP_ELSE, OP_ENDIF).
 *
 * La ejecución es válida si:
 * - No ocurren errores durante la ejecución.
 * - La pila final no está vacía.
 * - El elemento en la cima de la pila es verdadero (distinto de cero).
 *
 * Incluye un modo de depuración (--trace) que imprime el estado de la pila
 * después de cada instrucción.
 */
public class ScriptInterpreter {

    private final Deque<byte[]> stack;
    private final boolean trace;
    private final Map<String, Supplier<Boolean>> opcodeMap;
    private final boolean stepByStep;

    /**
     * Constructor del intérprete.
     *
     * @param trace Indica si se imprime el estado de la pila tras cada operación.
     * @param stepByStep Indica si se pausa tras cada operación.
     */
    public ScriptInterpreter(boolean trace, boolean stepByStep) {
        this.stack = new ArrayDeque<>();
        this.trace = trace;
        this.opcodeMap = new HashMap<>();
        this.stepByStep = stepByStep;
        initOpcodes();
    }

    /**
     * Constructor de conveniencia sin modo paso a paso.
     *
     * @param trace Indica si se imprime el estado de la pila tras cada operación.
     */
    public ScriptInterpreter(boolean trace) {
        this(trace, false);
    }

    /**
     * Inicializa todos los opcodes disponibles en el intérprete.
     */
    private void initOpcodes() {

        for (int i = 0; i <= 16; i++) {
            addOp(i);
        }

        opcodeMap.put("OP_DUP", this::opDup);
        opcodeMap.put("OP_DROP", this::opDrop);
        opcodeMap.put("OP_SWAP", this::opSwap);
        opcodeMap.put("OP_OVER", this::opOver);

        opcodeMap.put("OP_EQUAL", this::opEqual);
        opcodeMap.put("OP_EQUALVERIFY", () -> {
            if (!opEqual()) return false;
            return opVerify();
        });

        opcodeMap.put("OP_HASH160", this::opHash160);
        opcodeMap.put("OP_SHA256", this::opSha256);
        opcodeMap.put("OP_HASH256", this::opHash256);
        opcodeMap.put("OP_CHECKSIG", this::opCheckSig);
        opcodeMap.put("OP_CHECKSIGVERIFY", () -> {
            if (!opCheckSig()) return false;
            return opVerify();
        });

        opcodeMap.put("OP_ADD", this::opAdd);
        opcodeMap.put("OP_SUB", this::opSub);
        opcodeMap.put("OP_NUMEQUALVERIFY", this::opNumEqualVerify);
        opcodeMap.put("OP_LESSTHAN", this::opLessThan);
        opcodeMap.put("OP_GREATERTHAN", this::opGreaterThan);
        opcodeMap.put("OP_LESSTHANOREQUAL", this::opLessThanOrEqual);
        opcodeMap.put("OP_GREATERTHANOREQUAL", this::opGreaterThanOrEqual);
        opcodeMap.put("OP_NOT", this::opNot);
        opcodeMap.put("OP_BOOLAND", this::opBoolAnd);
        opcodeMap.put("OP_BOOLOR", this::opBoolOr);

        opcodeMap.put("OP_VERIFY", this::opVerify);
        opcodeMap.put("OP_RETURN", this::opReturn);
    }

    /**
     * Agrega un opcode OP_n que empuja un número a la pila.
     */
    private void addOp(int value) {
        opcodeMap.put("OP_" + value, () -> {
            stack.push(String.valueOf(value).getBytes());
            return true;
        });
    }

    /**
     * Ejecuta un script completo.
     *
     * @param script Lista de tokens (opcodes o datos).
     * @return true si el script es válido, false en caso contrario.
     */
    public boolean execute(List<String> script) {

        Deque<Boolean> executionStack = new ArrayDeque<>();

        for (String token : script) {

            boolean executing = !executionStack.contains(false);

            if (token.equals("OP_IF")) {

                if (!executing) {
                    executionStack.push(false);
                } else {
                    if (stack.isEmpty()) return false;
                    boolean condition = isTrue(stack.pop());
                    executionStack.push(condition);
                }
                traceStep(token);
                continue;
            }

            if (token.equals("OP_NOTIF")) {

                if (!executing) {
                    executionStack.push(false);
                } else {
                    if (stack.isEmpty()) return false;
                    executionStack.push(!isTrue(stack.pop()));
                }
                traceStep(token);
                continue;
            }

            if (token.equals("OP_ELSE")) {

                if (executionStack.isEmpty()) return false;
                boolean current = executionStack.pop();
                executionStack.push(!current);
                traceStep(token);
                continue;
            }

            if (token.equals("OP_ENDIF")) {

                if (executionStack.isEmpty()) return false;
                executionStack.pop();
                traceStep(token);
                continue;
            }

            if (!executing) {
                continue;
            }

            if (!processToken(token)) {
                return false;
            }

            if (trace) {
                System.out.println("\n>>> Procesando: " + token);
                printStack();

                if (stepByStep) {
                    System.out.print("Presione [Enter] para el siguiente paso...");
                    try {
                        Scanner sc = new Scanner(System.in);
                        sc.nextLine();
                    } catch (Exception e) { }
                }
            }
        }

        if (!executionStack.isEmpty()) return false;
        if (stack.isEmpty()) return false;
        return isTrue(stack.peek());
    }

    /**
     * Procesa un token individual.
     */
    private boolean processToken(String token) {

        Supplier<Boolean> op = opcodeMap.get(token);

        if (op != null) {
            return op.get();
        }

        stack.push(token.getBytes());
        return true;
    }

    /**
     * Duplica el elemento superior de la pila.
     */
    private boolean opDup() {

        if (stack.isEmpty()) return false;

        byte[] top = stack.peek();
        stack.push(Arrays.copyOf(top, top.length));

        return true;
    }

    /**
     * Elimina el elemento superior de la pila.
     */
    private boolean opDrop() {
        if (stack.isEmpty()) return false;
        stack.pop();
        return true;
    }

    /**
     * Intercambia los dos elementos superiores de la pila.
     */
    private boolean opSwap() {
        if (stack.size() < 2) return false;
        byte[] top    = stack.pop();
        byte[] second = stack.pop();
        stack.push(top);
        stack.push(second);
        return true;
    }

    /**
     * Copia el segundo elemento de la pila a la cima. Ejemplo: [a, b] -> [a, b, a]
     */
    private boolean opOver() {
        if (stack.size() < 2) return false;
        byte[] top    = stack.pop();
        byte[] second = stack.peek();
        stack.push(top);
        stack.push(Arrays.copyOf(second, second.length));
        return true;
    }

    /**
     * Compara los dos elementos superiores.
     */
    private boolean opEqual() {
        if (stack.size() < 2) return false;
        byte[] a = stack.pop();
        byte[] b = stack.pop();
        boolean result = Arrays.equals(a, b);
        stack.push(result ? "1".getBytes() : "0".getBytes());
        return true;
    }

    /**
     * Verifica que el valor superior sea verdadero.
     */
    private boolean opVerify() {
        if (stack.isEmpty()) return false;
        return isTrue(stack.pop());
    }

    /**
     * Detiene la ejecución.
     */
    private boolean opReturn() {
        return false;
    }

    /**
     * Simula HASH160.
     */
    private boolean opHash160() {
        if (stack.isEmpty()) return false;
        byte[] data = stack.pop();

        byte[] hash = ("HASH160_" + new String(data)).getBytes();

        stack.push(hash);

        return true;
    }

    /**
     * Simula SHA256.
     */
    private boolean opSha256() {
        if (stack.isEmpty()) return false;
        byte[] data = stack.pop();
        stack.push(("SHA256_" + new String(data)).getBytes());
        return true;
    }

    /**
     * Simula HASH256 (SHA256 doble).
     */
    private boolean opHash256() {
        if (stack.isEmpty()) return false;
        byte[] data = stack.pop();
        stack.push(("HASH256_" + new String(data)).getBytes());
        return true;
    }

    /**
     * Simula verificación de firma.
     */
    private boolean opCheckSig() {
        if (stack.size() < 2) return false;
        byte[] pubKey = stack.pop();
        byte[] signature = stack.pop();
        boolean valid = new String(signature).contains("VALID");
        stack.push(valid ? "1".getBytes() : "0".getBytes());
        return true;
    }

    /**
     * Suma dos valores.
     */
    private boolean opAdd() {
        if (stack.size() < 2) return false;

        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;

        stack.push(String.valueOf(a + b).getBytes());

        return true;
    }

    /**
     * Resta dos valores.
     */
    private boolean opSub() {
        if (stack.size() < 2) return false;

        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;

        stack.push(String.valueOf(a - b).getBytes());

        return true;
    }

    /**
     * Verifica que los dos elementos superiores sean numéricamente iguales.
     */
    private boolean opNumEqualVerify() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Menor que.
     */
    private boolean opLessThan() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt(a < b ? 1 : 0));
        return true;
    }

    /**
     * Mayor que.
     */
    private boolean opGreaterThan() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt(a > b ? 1 : 0));
        return true;
    }

    /**
     * Menor o igual que.
     */
    private boolean opLessThanOrEqual() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt(a <= b ? 1 : 0));
        return true;
    }

    /**
     * Mayor o igual que.
     */
    private boolean opGreaterThanOrEqual() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt(a >= b ? 1 : 0));
        return true;
    }

    /**
     * Negación lógica.
     */
    private boolean opNot() {
        if (stack.isEmpty()) return false;
        Integer val = toInt(stack.pop());
        if (val == null) return false;
        stack.push(fromInt(val == 0 ? 1 : 0));
        return true;
    }

    /**
     * AND lógico.
     */
    private boolean opBoolAnd() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt((a != 0 && b != 0) ? 1 : 0));
        return true;
    }

    /**
     * OR lógico.
     */
    private boolean opBoolOr() {
        if (stack.size() < 2) return false;
        Integer b = toInt(stack.pop());
        Integer a = toInt(stack.pop());
        if (a == null || b == null) return false;
        stack.push(fromInt((a != 0 || b != 0) ? 1 : 0));
        return true;
    }

    /**
     * Convierte byte[] a int de forma segura. Retorna null si no es numérico,
     * en lugar de lanzar NumberFormatException que colapsaría el intérprete.
     */
    private Integer toInt(byte[] val) {
        try {
            return Integer.parseInt(new String(val));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convierte int a byte[].
     */
    private byte[] fromInt(int val) {
        return String.valueOf(val).getBytes();
    }

    /**
     * Determina si un valor es verdadero.
     */
    private boolean isTrue(byte[] value) {
        if (value.length == 0) return false;
        String s = new String(value).trim();
        return !s.isEmpty() && !s.equals("0") && !s.equals("\u0000");
    }

    /**
     * Imprime el estado de la pila si el modo trace está activo.
     * Metodo extraído para cubrir también OP_IF, OP_NOTIF, OP_ELSE y OP_ENDIF.
     */
    private void traceStep(String token) {
        if (!trace) return;
        System.out.println("\n>>> Procesando: " + token);
        printStack();
        if (stepByStep) {
            System.out.print("Presione [Enter] para el siguiente paso...");
            try {
                Scanner sc = new Scanner(System.in);
                sc.nextLine();
            } catch (Exception e) { }
        }
    }

    /**
     * Imprime el estado de la pila.
     */
    private void printStack() {
        System.out.println("Stack Top ->");
        for (byte[] item : stack) {
            if (item.length == 1 && item[0] == 1) {
                System.out.println("   <TRUE> (0x01)");
            }

            else if (item.length == 1 && item[0] == 0) {
                System.out.println("   <FALSE> (0x00)");
            }

            else {
                System.out.println("   [" + new String(item) + "]");
            }
        }
        System.out.println("--------------");
    }
}