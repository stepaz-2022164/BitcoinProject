package org.uvg.bitcoin;

import org.uvg.bitcoin.script.ScriptInterpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * Metodo principal. Inicia el loop interactivo del intérprete.
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== INTÉRPRETE DE BITCOIN SCRIPT (GRUPO 9) ===");

        while (true) {
            System.out.println("\nSeleccione el método de entrada:");
            System.out.println("1. Escribir script en consola");
            System.out.println("2. Leer script desde archivo script.txt (todo en una sola linea)");
            System.out.println("3. Demo P2PKH (Pay-to-Public-Key-Hash)");
            System.out.println("4. Demo Condicionales");
            System.out.println("5. Demo Operaciones Aritméticas");
            System.out.println("6. Salir");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine().trim();
            List<String> fullScript = new ArrayList<>();

            switch (opcion) {
                case "1" -> {
                    System.out.println("\nIngrese el script completo separado por espacios:");
                    System.out.print("> ");
                    String raw = scanner.nextLine().trim();
                    if (!raw.isEmpty()) fullScript = Arrays.asList(raw.split("\\s+"));
                }
                case "2" -> {
                    try {
                        File archivo = new File("script.txt");
                        Scanner lector = new Scanner(archivo);
                        if (lector.hasNextLine()) {
                            String raw = lector.nextLine().trim();
                            if (!raw.isEmpty()) fullScript = Arrays.asList(raw.split("\\s+"));
                        }
                        lector.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("Error: No se encontró el archivo script.txt.");
                        continue;
                    }
                }
                case "3" -> {
                    runP2PKHDemo();
                    continue;
                }
                case "4" -> {
                    runConditionalDemo();
                    continue;
                }
                case "5" -> {
                    runArithmeticDemo();
                    continue;
                }
                case "6" -> {
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Opción no válida.");
                    continue;
                }
            }

            if (fullScript.isEmpty()) {
                System.out.println("Script vacío.");
                continue;
            }

            boolean trace = false;
            boolean stepByStep = false;

            System.out.println("\nSeleccione el modo de ejecución:");
            System.out.println("1. Ejecución Directa (Solo muestra el resultado final)");
            System.out.println("2. Trace Continuo (Imprime toda la ejecución de corrido)");
            System.out.println("3. Trace Paso a Paso (Pausa en cada token interactivo)");
            System.out.print("Opción: ");

            String modo = scanner.nextLine().trim();
            if (modo.equals("2")) {
                trace = true;
            } else if (modo.equals("3")) {
                trace = true;
                stepByStep = true;
            }

            ScriptInterpreter interpreter = new ScriptInterpreter(trace, stepByStep);
            boolean result = interpreter.execute(fullScript);

            System.out.println("\nResultado Final de Validación: " + (result ? "EXITOSA (TRUE)" : "FALLIDA (FALSE)"));
            System.out.println("------------------------------------------------");
        }
    }

    private static void runP2PKHDemo() {
        System.out.println("\n📋 DEMO P2PKH — Pay-to-PubKey-Hash");
        System.out.println("Formato: scriptSig + scriptPubKey ejecutados en conjunto.");
        System.out.println("  scriptSig:    <firma> <pubKey>");
        System.out.println("  scriptPubKey: OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG");

        String pubKey = "PUBKEY_UVG";
        String pubKeyHash = "HASH160_PUBKEY_UVG";

        System.out.println("\n--- CASO 1: firma válida y pubKeyHash correcto ---");
        List<String> scriptCorrecto = Arrays.asList(
                "VALID_SIGNATURE", pubKey,
                "OP_DUP", "OP_HASH160", pubKeyHash, "OP_EQUALVERIFY", "OP_CHECKSIG"
        );
        System.out.println("Script: " + scriptCorrecto);
        ScriptInterpreter interp1 = new ScriptInterpreter(true, false);
        boolean res1 = interp1.execute(scriptCorrecto);
        System.out.println("Resultado: " + (res1 ? "VÁLIDO" : "INVÁLIDO"));

        System.out.println("\n--- CASO 2: firma inválida ---");
        List<String> scriptFirmaInvalida = Arrays.asList(
                "BAD_SIGNATURE", pubKey,
                "OP_DUP", "OP_HASH160", pubKeyHash, "OP_EQUALVERIFY", "OP_CHECKSIG"
        );
        System.out.println("Script: " + scriptFirmaInvalida);
        ScriptInterpreter interp2 = new ScriptInterpreter(true, false);
        boolean res2 = interp2.execute(scriptFirmaInvalida);
        System.out.println("Resultado: " + (res2 ? "VÁLIDO" : "INVÁLIDO"));

        System.out.println("\n--- CASO 3: pubKeyHash incorrecto ---");
        List<String> scriptHashInvalido = Arrays.asList(
                "VALID_SIGNATURE", pubKey,
                "OP_DUP", "OP_HASH160", "HASH160_OTRA_PUBKEY", "OP_EQUALVERIFY", "OP_CHECKSIG"
        );
        System.out.println("Script: " + scriptHashInvalido);
        ScriptInterpreter interp3 = new ScriptInterpreter(true, false);
        boolean res3 = interp3.execute(scriptHashInvalido);
        System.out.println("Resultado: " + (res3 ? "VÁLIDO" : "INVÁLIDO"));
        System.out.println("\n------------------------------------------------");
    }


    private static void runConditionalDemo() {
        System.out.println("\n📋 DEMO CONDICIONALES");

        System.out.println("\n--- CASO 1: IF simple (verdadero) ---");
        List<String> script1 = Arrays.asList("OP_1", "OP_IF", "OP_42", "OP_ENDIF");
        System.out.println("Script: " + script1);
        ScriptInterpreter interp1 = new ScriptInterpreter(true, false);
        boolean res1 = interp1.execute(script1);
        System.out.println("Resultado: " + (res1 ? "VÁLIDO (debe tener 42 en pila)" : "INVÁLIDO"));

        System.out.println("\n--- CASO 2: IF simple (falso) ---");
        List<String> script2 = Arrays.asList("OP_0", "OP_IF", "OP_42", "OP_ENDIF", "OP_1");
        System.out.println("Script: " + script2);
        ScriptInterpreter interp2 = new ScriptInterpreter(true, false);
        boolean res2 = interp2.execute(script2);
        System.out.println("Resultado: " + (res2 ? "VÁLIDO (debe tener 1 en pila)" : "INVÁLIDO"));

        System.out.println("\n--- CASO 3: IF-ELSE (verdadero) ---");
        List<String> script3 = Arrays.asList("OP_1", "OP_IF", "OP_42", "OP_ELSE", "OP_24", "OP_ENDIF");
        System.out.println("Script: " + script3);
        ScriptInterpreter interp3 = new ScriptInterpreter(true, false);
        boolean res3 = interp3.execute(script3);
        System.out.println("Resultado: " + (res3 ? "VÁLIDO (debe tener 42)" : "INVÁLIDO"));

        System.out.println("\n--- CASO 4: IF-ELSE (falso) ---");
        List<String> script4 = Arrays.asList("OP_0", "OP_IF", "OP_42", "OP_ELSE", "OP_24", "OP_ENDIF");
        System.out.println("Script: " + script4);
        ScriptInterpreter interp4 = new ScriptInterpreter(true, false);
        boolean res4 = interp4.execute(script4);
        System.out.println("Resultado: " + (res4 ? "VÁLIDO (debe tener 24)" : "INVÁLIDO"));

        System.out.println("\n--- CASO 5: Condicionales anidados ---");
        List<String> script5 = Arrays.asList(
                "OP_1", "OP_IF",
                "OP_1", "OP_IF",
                "OP_100",
                "OP_ELSE",
                "OP_200",
                "OP_ENDIF",
                "OP_ELSE",
                "OP_0", "OP_IF",
                "OP_300",
                "OP_ELSE",
                "OP_400",
                "OP_ENDIF",
                "OP_ENDIF"
        );
        System.out.println("Script: " + script5);
        ScriptInterpreter interp5 = new ScriptInterpreter(true, false);
        boolean res5 = interp5.execute(script5);
        System.out.println("Resultado: " + (res5 ? "VÁLIDO" : "INVÁLIDO"));
        System.out.println("\n------------------------------------------------");
    }

    private static void runArithmeticDemo() {
        System.out.println("\n📋 DEMO OPERACIONES ARITMÉTICAS");

        System.out.println("\n--- SUMA: 5 + 3 = 8 ---");
        List<String> suma = Arrays.asList("OP_5", "OP_3", "OP_ADD");
        ejecutarDemo(suma);

        System.out.println("\n--- RESTA: 10 - 4 = 6 ---");
        List<String> resta = Arrays.asList("OP_10", "OP_4", "OP_SUB");
        ejecutarDemo(resta);

        System.out.println("\n--- MENOR QUE: 3 < 5 ? true ---");
        List<String> menor = Arrays.asList("OP_3", "OP_5", "OP_LESSTHAN");
        ejecutarDemo(menor);

        System.out.println("\n--- MAYOR QUE: 7 > 2 ? true ---");
        List<String> mayor = Arrays.asList("OP_7", "OP_2", "OP_GREATERTHAN");
        ejecutarDemo(mayor);

        System.out.println("\n--- IGUALDAD NUMÉRICA: 42 == 42 ? true ---");
        List<String> igual = Arrays.asList("OP_42", "OP_42", "OP_NUMEQUALVERIFY", "OP_1");
        ejecutarDemo(igual);
    }

    private static void ejecutarDemo(List<String> script) {
        System.out.println("Script: " + script);
        ScriptInterpreter interp = new ScriptInterpreter(true, false);
        boolean result = interp.execute(script);
        System.out.println("Resultado: " + (result ? "VÁLIDO" : "INVÁLIDO"));
        System.out.println("-".repeat(50));
    }
}