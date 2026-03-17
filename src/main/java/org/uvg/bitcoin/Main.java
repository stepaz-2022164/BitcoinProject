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
            System.out.println("4. Salir");
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
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Opción no válida.");
                    continue;
                }
            }

            boolean trace       = false;
            boolean stepByStep  = false;

            System.out.println("\nSeleccione el modo de ejecución:");
            System.out.println("1. Ejecución Directa (Solo muestra el resultado final)");
            System.out.println("2. Trace Continuo (Imprime toda la ejecución de corrido)");
            System.out.println("3. Trace Paso a Paso (Pausa en cada token interactivo)");
            System.out.print("Opción: ");

            String modo = scanner.nextLine().trim();
            if (modo.equals("2")) {
                trace = true;
            } else if (modo.equals("3")) {
                trace      = true;
                stepByStep = true;
            }

            ScriptInterpreter interpreter = new ScriptInterpreter(trace, stepByStep);
            boolean result = interpreter.execute(fullScript);

            System.out.println("\nResultado Final de Validación: " + (result ? "EXITOSA (TRUE)" : "FALLIDA (FALSE)"));
            System.out.println("------------------------------------------------");
        }
    }

    // Demo P2PKH
    private static void runP2PKHDemo() {
        System.out.println("DEMO P2PKH — Pay-to-PubKey-Hash");
        System.out.println();
        System.out.println("Formato: scriptSig + scriptPubKey ejecutados en conjunto.");
        System.out.println("  scriptSig:    <firma> <pubKey>");
        System.out.println("  scriptPubKey: OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG");
        String pubKey     = "PUBKEY_UVG";
        String pubKeyHash = "HASH160_" + pubKey;

        System.out.println("\n--- CASO 1: firma válida y pubKeyHash correcto ---");
        List<String> scriptCorrecto = buildP2PKH("VALID_SIGNATURE", pubKey, pubKeyHash);
        System.out.println("Script: " + scriptCorrecto);
        ScriptInterpreter interp1 = new ScriptInterpreter(true, false);
        boolean res1 = interp1.execute(scriptCorrecto);
        System.out.println("Resultado: " + (res1 ? "VÁLIDO" : "INVÁLIDO"));

        System.out.println("\n--- CASO 2: firma inválida ---");
        List<String> scriptFirmaInvalida = buildP2PKH("BAD_SIGNATURE", pubKey, pubKeyHash);
        System.out.println("Script: " + scriptFirmaInvalida);
        ScriptInterpreter interp2 = new ScriptInterpreter(true, false);
        boolean res2 = interp2.execute(scriptFirmaInvalida);
        System.out.println("Resultado: " + (res2 ? "VÁLIDO" : "INVÁLIDO"));

        System.out.println("\n--- CASO 3: pubKeyHash incorrecto (clave pública diferente) ---");
        List<String> scriptHashInvalido = buildP2PKH("VALID_SIGNATURE", pubKey, "HASH160_OTRA_PUBKEY");
        System.out.println("Script: " + scriptHashInvalido);
        ScriptInterpreter interp3 = new ScriptInterpreter(true, false);
        boolean res3 = interp3.execute(scriptHashInvalido);
        System.out.println("Resultado: " + (res3 ? "VÁLIDO" : "INVÁLIDO"));
        System.out.println("\n------------------------------------------------");
    }

    /**
     * Construye el script completo P2PKH (scriptSig concatenado con scriptPubKey).
     *
     * @param firma      la firma a incluir en el scriptSig.
     * @param pubKey     la clave pública a incluir en el scriptSig.
     * @param pubKeyHash el hash de clave pública en el scriptPubKey (lo que está "bloqueado").
     * @return lista de tokens del script completo.
     */
    private static List<String> buildP2PKH(String firma, String pubKey, String pubKeyHash) {
        return Arrays.asList(
                firma, pubKey,
                "OP_DUP", "OP_HASH160", pubKeyHash, "OP_EQUALVERIFY", "OP_CHECKSIG"
        );
    }
}