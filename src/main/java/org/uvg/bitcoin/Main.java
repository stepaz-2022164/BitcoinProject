package org.uvg.bitcoin;

import org.uvg.bitcoin.script.ScriptInterpreter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Prueba P2PKH ---");

        List<String> scriptSig = Arrays.asList("VALID_SIGNATURE", "PUBKEY_123");

        List<String> scriptPubKey = Arrays.asList(
                "OP_DUP",
                "OP_HASH160",
                "HASH160_PUBKEY_123",
                "OP_EQUALVERIFY",
                "OP_CHECKSIG"
        );

        List<String> fullScript = new ArrayList<>(scriptSig);
        fullScript.addAll(scriptPubKey);

        System.out.println("Ejecutando Script: " + fullScript);

        ScriptInterpreter interpreter = new ScriptInterpreter(true);
        boolean result = interpreter.execute(fullScript);

        System.out.println("Resultado de validación P2PKH: " + (result ? "EXITOSA" : "FALLIDA"));
    }
}
