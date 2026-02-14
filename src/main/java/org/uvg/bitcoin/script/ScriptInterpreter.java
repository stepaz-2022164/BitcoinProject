package org.uvg.bitcoin.script;

import java.util.*;
import java.util.function.Supplier;

public class ScriptInterpreter {

    private final Deque<byte[]> stack;
    private final boolean trace;
    private final Map<String, Supplier<Boolean>> opcodeMap;

    public ScriptInterpreter(boolean trace) {
        this.stack = new ArrayDeque<>();
        this.trace = trace;
        this.opcodeMap = new HashMap<>();
        initOpcodes(); // Llenamos el mapa al iniciar
    }

    private void initOpcodes() {
        opcodeMap.put("OP_0", () -> { stack.push(new byte[]{0}); return true; });
        opcodeMap.put("OP_1", () -> { stack.push(new byte[]{1}); return true; });

        opcodeMap.put("OP_DUP", this::opDup);
        opcodeMap.put("OP_DROP", this::opDrop);

        opcodeMap.put("OP_EQUAL", this::opEqual);
        opcodeMap.put("OP_EQUALVERIFY", () -> {
            if (!opEqual()) return false;
            return opVerify();
        });

        opcodeMap.put("OP_HASH160", this::opHash160);
        opcodeMap.put("OP_CHECKSIG", this::opCheckSig);
    }

    public boolean execute(List<String> script) {
        for (String token : script) {
            if (!processToken(token)) {
                return false;
            }

            if (trace) {
                printStack();
            }
        }
        return !stack.isEmpty() && isTrue(stack.peek());
    }

    private boolean processToken(String token) {
        if (opcodeMap.containsKey(token)) {
            return opcodeMap.get(token).get();
        }

        stack.push(token.getBytes());
        return true;
    }


    private boolean opDup() {
        if (stack.isEmpty()) return false;
        stack.push(Arrays.copyOf(stack.peek(), stack.peek().length));
        return true;
    }

    private boolean opDrop() {
        if (stack.isEmpty()) return false;
        stack.pop();
        return true;
    }

    private boolean opEqual() {
        if (stack.size() < 2) return false;
        byte[] a = stack.pop();
        byte[] b = stack.pop();
        boolean result = Arrays.equals(a, b);
        stack.push(result ? new byte[]{1} : new byte[]{0});
        return true;
    }

    private boolean opVerify() {
        if (stack.isEmpty()) return false;
        return isTrue(stack.pop());
    }

    private boolean opHash160() {
        if (stack.isEmpty()) return false;
        byte[] data = stack.pop();
        byte[] hash = ("HASH160_" + new String(data)).getBytes();
        stack.push(hash);
        return true;
    }

    private boolean opCheckSig() {
        if (stack.size() < 2) return false;
        byte[] pubKey = stack.pop();
        byte[] signature = stack.pop();

        boolean valid = new String(signature).contains("VALID");
        stack.push(valid ? new byte[]{1} : new byte[]{0});
        return true;
    }

    private boolean isTrue(byte[] value) {
        if (value.length == 0) return false;
        for (byte b : value) {
            if (b != 0) return true;
        }
        return false;
    }

    private void printStack() {
        System.out.println("Stack Top ->");
        for (byte[] item : stack) {
            if (item.length == 1 && item[0] == 1) {
                System.out.println("   <TRUE> (0x01)");
            } else if (item.length == 1 && item[0] == 0) {
                System.out.println("   <FALSE> (0x00)");
            } else {
                System.out.println("   [" + new String(item) + "]");
            }
        }
        System.out.println("--------------");
    }
}