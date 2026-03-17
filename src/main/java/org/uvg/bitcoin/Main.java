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

        while (true) {
            System.out.println("\nSeleccione el método de entrada:");
            System.out.println("1. Escribir script en consola");
            System.out.println("2. Leer script desde archivo script.txt (todo en una sola linea)");
            System.out.println("3. Salir");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine();
            String scriptCrudo = "";

            if (opcion.equals("1")) {
                System.out.println("\nIngrese el script completo separado por espacios:");
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
                } catch (FileNotFoundException e) {
                    System.out.println("Error: No se encontró el archivo.");
                    continue;
                }
            } else if (opcion.equals("3")) {
                System.out.println("Saliendo del programa...");
                break;
            } else {
                System.out.println("Opción no válida.");
                continue;
            }

            System.out.println("\nSeleccione el modo de ejecución:");
            System.out.println("1. Ejecución Directa (Solo muestra el resultado final)");
            System.out.println("2. Trace Continuo (Imprime toda la ejecución de corrido)");
            System.out.println("3. Trace Paso a Paso (Pausa en cada token interactivo)");
            System.out.print("Opción: ");

            String modoEjecucion = scanner.nextLine();
            boolean trace = false;
            boolean stepByStep = false;

            if (modoEjecucion.equals("2")) {
                trace = true;
            } else if (modoEjecucion.equals("3")) {
                trace = true;
                stepByStep = true;
            }

            scriptCrudo = scriptCrudo.trim();
            List<String> fullScript = new ArrayList<>(Arrays.asList(scriptCrudo.split("\\s+")));
            if (scriptCrudo.isEmpty()) fullScript.clear(); 

            System.out.println("\n--- Iniciando Máquina Virtual ---");

            ScriptInterpreter interpreter = new ScriptInterpreter(trace, stepByStep);
            boolean result = interpreter.execute(fullScript);

            System.out.println("\nResultado Final de Validación: " + (result ? "EXITOSA (TRUE)" : "FALLIDA (FALSE)"));
            System.out.println("------------------------------------------------");
        }
        scanner.close();
    }
}