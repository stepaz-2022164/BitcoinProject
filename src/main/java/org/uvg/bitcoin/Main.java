package org.uvg.bitcoin;

import org.uvg.bitcoin.script.ScriptInterpreter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== INTÉRPRETE DE BITCOIN SCRIPT (GRUPO 9) ===");
        System.out.println("Seleccione el método de entrada:");
        System.out.println("1. Escribir script en consola");
        System.out.println("2. Leer script desde archivo (todo en una sola linea)");
        System.out.print("Opción: ");

        String opcion = scanner.nextLine();
        String scriptCrudo = "";

        if (opcion.equals("1")) {
            System.out.println("\nIngrese el script completo (scriptSig seguido de scriptPubKey) separado por espacios:");
            System.out.println("Ejemplo: VALID_SIGNATURE PUBKEY_123 OP_DUP OP_HASH160 HASH160_PUBKEY_123 OP_EQUALVERIFY OP_CHECKSIG");
            System.out.print("> ");
            scriptCrudo = scanner.nextLine();

        } else if (opcion.equals("2")) {
            String ruta = "script.txt";
            try {
                File archivo = new File(ruta);
                Scanner lectorArchivo = new Scanner(archivo);
                if (lectorArchivo.hasNextLine()) {
                    scriptCrudo = lectorArchivo.nextLine();
                }
                lectorArchivo.close();
                System.out.println("Script leído del archivo exitosamente.");
            } catch (FileNotFoundException e) {
                System.out.println("Error: No se encontró el archivo. Asegúrese de que la ruta sea correcta.");
                return;
            }
        } else {
            System.out.println("Opción no válida. Saliendo...");
            return;
        }
        scriptCrudo = scriptCrudo.trim();
        List<String> fullScript = new ArrayList<>(Arrays.asList(scriptCrudo.split("\\s+")));

        System.out.println("\n--- Ejecutando Script ---");
        System.out.println("Tokens: " + fullScript);
        ScriptInterpreter interpreter = new ScriptInterpreter(true);
        boolean result = interpreter.execute(fullScript);

        System.out.println("\nResultado Final de Validación: " + (result ? "EXITOSA (TRUE)" : "FALLIDA (FALSE)"));
        scanner.close();
    }
}