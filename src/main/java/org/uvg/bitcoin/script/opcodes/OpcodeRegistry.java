package org.uvg.bitcoin.script.opcodes;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registro central de todos los opcodes disponibles en el intérprete.
 * Utiliza un mapa para almacenar y recuperar opcodes por su nombre.
 */
public class OpcodeRegistry {
    private final Map<String, Opcode> opcodeMap;
    private final ScriptUtils utils;

    /**
     * Constructor del registro. Inicializa todos los opcodes.
     */
    public OpcodeRegistry() {
        this.opcodeMap = new HashMap<>();
        this.utils = new ScriptUtils();
        registerAllOpcodes();
    }

    /**
     * Registra todos los opcodes soportados por el intérprete.
     */
    private void registerAllOpcodes() {
        registerLiterals();

        register("OP_DUP", StackOpcodes::dup);
        register("OP_DROP", StackOpcodes::drop);
        register("OP_SWAP", StackOpcodes::swap);
        register("OP_OVER", StackOpcodes::over);

        register("OP_EQUAL", LogicalOpcodes::equal);
        register("OP_EQUALVERIFY", LogicalOpcodes::equalVerify);
        register("OP_NOT", LogicalOpcodes::not);
        register("OP_BOOLAND", LogicalOpcodes::boolAnd);
        register("OP_BOOLOR", LogicalOpcodes::boolOr);

        register("OP_ADD", ArithmeticOpcodes::add);
        register("OP_SUB", ArithmeticOpcodes::sub);
        register("OP_NUMEQUALVERIFY", ArithmeticOpcodes::numEqualVerify);
        register("OP_LESSTHAN", ArithmeticOpcodes::lessThan);
        register("OP_GREATERTHAN", ArithmeticOpcodes::greaterThan);
        register("OP_LESSTHANOREQUAL", ArithmeticOpcodes::lessThanOrEqual);
        register("OP_GREATERTHANOREQUAL", ArithmeticOpcodes::greaterThanOrEqual);

        register("OP_VERIFY", FlowControlOpcodes::verify);
        register("OP_RETURN", FlowControlOpcodes::opReturn);

        register("OP_HASH160", CryptoOpcodes::hash160);
        register("OP_SHA256", CryptoOpcodes::sha256);
        register("OP_HASH256", CryptoOpcodes::hash256);

        register("OP_CHECKSIG", CryptoOpcodes::checkSig);
        register("OP_CHECKSIGVERIFY", CryptoOpcodes::checkSigVerify);

        register("PUSHDATA1", (ctx, u) -> {
            ctx.push("<PUSHDATA1>".getBytes());
            return true;
        });
        register("PUSHDATA2", (ctx, u) -> {
            ctx.push("<PUSHDATA2>".getBytes());
            return true;
        });
    }

    /**
     * Registra los literales OP_0 a OP_16.
     */
    private void registerLiterals() {
        for (int i = 0; i <= 16; i++) {
            final int value = i;
            register("OP_" + i, (ctx, u) -> {
                if (value == 0) {
                    ctx.push(new byte[0]); // OP_0 empuja array vacío
                } else {
                    ctx.push(u.serializeNumber(value));
                }
                return true;
            });
        }
    }

    /**
     * Registra un opcode usando una función que recibe contexto y utils.
     */
    private void register(String name, BiFunction<ExecutionContext, ScriptUtils, Boolean> function) {
        opcodeMap.put(name, (ctx) -> function.apply(ctx, utils));
    }

    /**
     * Registra un opcode directamente.
     */
    public void register(String name, Opcode opcode) {
        opcodeMap.put(name, opcode);
    }

    /**
     * Obtiene un opcode por su nombre.
     *
     * @param name Nombre del opcode (ej. "OP_DUP")
     * @return El opcode correspondiente o null si no existe
     */
    public Opcode getOpcode(String name) {
        return opcodeMap.get(name);
    }

    /**
     * Verifica si un opcode existe en el registro.
     */
    public boolean containsOpcode(String name) {
        return opcodeMap.containsKey(name);
    }
}