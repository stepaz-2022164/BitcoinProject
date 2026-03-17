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
        initOpcodes();
    }

    private void initOpcodes() {

        for (int i = 0; i <= 16; i++) {
            addOp(i);
        }

        opcodeMap.put("OP_DUP", this::opDup);
        opcodeMap.put("OP_DROP", this::opDrop);

        opcodeMap.put("OP_EQUAL", this::opEqual);
        opcodeMap.put("OP_EQUALVERIFY", () -> {
            if (!opEqual()) return false;
            return opVerify();
        });

        opcodeMap.put("OP_HASH160", this::opHash160);
        opcodeMap.put("OP_CHECKSIG", this::opCheckSig);

        opcodeMap.put("OP_ADD", this::opAdd);
        opcodeMap.put("OP_SUB", this::opSub);
        opcodeMap.put("OP_LESSTHAN", this::opLessThan);
        opcodeMap.put("OP_GREATERTHAN", this::opGreaterThan);
        opcodeMap.put("OP_NOT", this::opNot);
        opcodeMap.put("OP_BOOLAND", this::opBoolAnd);
        opcodeMap.put("OP_BOOLOR", this::opBoolOr);

        opcodeMap.put("OP_VERIFY", this::opVerify);
        opcodeMap.put("OP_RETURN", this::opReturn);
    }

    private void addOp(int value) {
        opcodeMap.put("OP_" + value, () -> {
            stack.push(new byte[]{(byte) value});
            return true;
        });
    }

    public boolean execute(List<String> script) {

        Deque<Boolean> executionStack = new ArrayDeque<>();

        for (String token : script) {

            boolean executing = !executionStack.contains(false);

            if (token.equals("OP_IF")) {

                if (!executing) {
                    executionStack.push(false);
                    continue;
                }

                if (stack.isEmpty()) return false;

                boolean condition = isTrue(stack.pop());

                executionStack.push(condition);
                continue;
            }

            if (token.equals("OP_ELSE")) {

                if (executionStack.isEmpty()) return false;

                boolean current = executionStack.pop();
                executionStack.push(!current);
                continue;
            }

            if (token.equals("OP_ENDIF")) {

                if (executionStack.isEmpty()) return false;

                executionStack.pop();
                continue;
            }

            if (!executing) {
                continue;
            }

            if (!processToken(token)) {
                return false;
            }

            if (trace) {
                printStack();
            }
        }

        if (!executionStack.isEmpty()) return false;
        if (stack.isEmpty()) return false;
        return isTrue(stack.peek());
    }

    private boolean processToken(String token) {

        Supplier<Boolean> op = opcodeMap.get(token);

        if (op != null) {
            return op.get();
        }

        stack.push(token.getBytes());
        return true;
    }

    private boolean opDup() {

        if (stack.isEmpty()) return false;

        byte[] top = stack.peek();
        stack.push(Arrays.copyOf(top, top.length));

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

    private boolean opReturn() {
        return false;
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

    private int toInt(byte[] val) {
        return Integer.parseInt(new String(val));
    }

    private byte[] fromInt(int val) {
        return String.valueOf(val).getBytes();
    }

    private boolean opAdd() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt(a + b));
        return true;
    }

    private boolean opSub() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt(a - b));
        return true;
    }

    private boolean opLessThan() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt(a < b ? 1 : 0));
        return true;
    }

    private boolean opGreaterThan() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt(a > b ? 1 : 0));
        return true;
    }

    private boolean opNot() {
        if (stack.isEmpty()) return false;

        int val = toInt(stack.pop());
        stack.push(fromInt(val == 0 ? 1 : 0));
        return true;
    }

    private boolean opBoolAnd() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt((a != 0 && b != 0) ? 1 : 0));
        return true;
    }

    private boolean opBoolOr() {
        if (stack.size() < 2) return false;

        int b = toInt(stack.pop());
        int a = toInt(stack.pop());

        stack.push(fromInt((a != 0 || b != 0) ? 1 : 0));
        return true;
    }
}