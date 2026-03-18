package org.uvg.bitcoin.script;

import org.uvg.bitcoin.script.model.ExecutionContext;
import org.uvg.bitcoin.script.opcodes.Opcode;
import org.uvg.bitcoin.script.opcodes.OpcodeRegistry;
import org.uvg.bitcoin.script.util.ScriptUtils;

import java.util.List;
import java.util.Scanner;

/**
 * Intérprete principal de Bitcoin Script.
 * Esta clase coordina la ejecución de scripts utilizando un contexto de ejecución
 * y un registro de opcodes.
 */
public class ScriptInterpreter {

    private final ExecutionContext context;
    private final OpcodeRegistry opcodeRegistry;
    private final ScriptUtils utils;

    /**
     * Constructor del intérprete.
     *
     * @param trace       Indica si se imprime el estado de la pila tras cada operación
     * @param stepByStep  Indica si se pausa tras cada operación
     */
    public ScriptInterpreter(boolean trace, boolean stepByStep) {
        this.context = new ExecutionContext(trace, stepByStep);
        this.opcodeRegistry = new OpcodeRegistry();
        this.utils = new ScriptUtils();
    }

    /**
     * Constructor de conveniencia sin modo paso a paso.
     *
     * @param trace Indica si se imprime el estado de la pila tras cada operación
     */
    public ScriptInterpreter(boolean trace) {
        this(trace, false);
    }

    /**
     * Ejecuta un script completo con soporte para control de flujo anidado.
     *
     * @param script Lista de tokens (opcodes o datos)
     * @return true si el script es válido, false en caso contrario
     */
    public boolean execute(List<String> script) {
        context.reset();

        if (context.isTrace()) {
            System.out.println("\n=== INICIANDO EJECUCIÓN DEL SCRIPT ===");
            System.out.println("Script: " + script);
            System.out.println("======================================");
        }

        for (int i = 0; i < script.size(); i++) {
            String token = script.get(i);

            if (context.isTrace()) {
                System.out.println("\n>>> Procesando [" + i + "]: " + token);
            }

            if (token.equals("OP_IF") || token.equals("OP_NOTIF")) {
                if (!handleIf(token)) {
                    return false;
                }
                continue;
            }

            if (token.equals("OP_ELSE")) {
                if (!handleElse()) {
                    return false;
                }
                continue;
            }

            if (token.equals("OP_ENDIF")) {
                if (!handleEndIf()) {
                    return false;
                }
                continue;
            }

            if (context.isExecuting()) {
                if (!processToken(token)) {
                    if (context.isTrace()) {
                        System.out.println(" ERROR: Falló al procesar " + token);
                    }
                    return false;
                }
            } else {
                if (context.isTrace() && !token.startsWith("OP_")) {
                    System.out.println(" Saltando dato: " + token);
                }
            }

            if (context.isTrace()) {
                if (!token.startsWith("OP_") && context.isExecuting()) {
                    System.out.println(" Dato empujado: " + token);
                }
                printStack();

                if (context.isStepByStep()) {
                    System.out.print("Presione Enter para continuar...");
                    try {
                        new Scanner(System.in).nextLine();
                    } catch (Exception e) { }
                }
            }
        }

        if (!context.isIfStackEmpty()) {
            if (context.isTrace()) {
                System.out.println(" ERROR: Faltan OP_ENDIF (" +
                        context.getIfStackSize() + " niveles sin cerrar)");
            }
            return false;
        }

        if (context.isStackEmpty()) {
            if (context.isTrace()) {
                System.out.println(" ERROR: Pila vacía al final");
            }
            return false;
        }

        boolean result = utils.isTrue(context.peek());
        if (context.isTrace()) {
            System.out.println("\n=== RESULTADO FINAL ===");
            System.out.println("Pila final:");
            printStack();
            System.out.println("¿Válido? " + (result ? "SÍ" : "NO"));
        }
        return result;
    }

    /**
     * Maneja OP_IF y OP_NOTIF.
     *
     * @param token El comando condicional a evaluar (OP_IF o OP_NOTIF).
     * @return true si pudo evaluar correctamente la instrucción, false si hay errores como pila vacía.
     */
    private boolean handleIf(String token) {
        context.pushIf(context.isExecuting());

        if (context.isExecuting()) {
            if (context.isStackEmpty()) {
                if (context.isTrace()) {
                    System.out.println(" ERROR: Pila vacía para condición");
                }
                return false;
            }

            boolean condition = utils.isTrue(context.pop());
            if (token.equals("OP_NOTIF")) {
                condition = !condition;
            }
            context.setExecuting(condition);
            context.setSkipElse(false);
        } else {
            context.setExecuting(false);
            context.setSkipElse(true);
        }

        if (context.isTrace()) {
            System.out.println(" IF/NOTIF: executing=" + context.isExecuting() +
                    ", skipElse=" + context.isSkipElse());
            printStack();
        }

        return true;
    }

    /**
     * Maneja OP_ELSE.
     *
     * @return true si OP_ELSE se ejecutó dentro de un bloque condicional válido, false si no hay un OP_IF previo.
     */
    private boolean handleElse() {
        if (context.isIfStackEmpty()) {
            if (context.isTrace()) {
                System.out.println(" ERROR: OP_ELSE sin OP_IF");
            }
            return false;
        }

        if (!context.isSkipElse()) {
            context.setExecuting(!context.isExecuting());
        }

        if (context.isTrace()) {
            System.out.println(" ELSE: executing=" + context.isExecuting());
            printStack();
        }

        return true;
    }

    /**
     * Maneja OP_ENDIF.
     *
     * @return true si se cerró correctamente un bloque condicional, false si no había bloque abierto.
     */
    private boolean handleEndIf() {
        if (context.isIfStackEmpty()) {
            if (context.isTrace()) {
                System.out.println(" ERROR: OP_ENDIF sin OP_IF");
            }
            return false;
        }

        context.setExecuting(context.popIf());
        context.setSkipElse(false);

        if (context.isTrace()) {
            System.out.println(" ENDIF: executing=" + context.isExecuting());
            printStack();
        }

        return true;
    }

    /**
     * Procesa un token individual (opcode o dato).
     *
     * @param token Cadena que representa el opcode o dato literal a ejecutar/empujar.
     * @return true si el opcode finalizó sin errores o si el dato se empujó a la pila, false en fallos lógicos del opcode.
     */
    private boolean processToken(String token) {
        Opcode opcode = opcodeRegistry.getOpcode(token);

        if (opcode != null) {
            return opcode.execute(context);
        }
        context.push(token.getBytes());
        return true;
    }

    /**
     * Imprime el estado actual de la pila.
     */
    private void printStack() {
        System.out.println("Stack:");
        if (context.isStackEmpty()) {
            System.out.println("   [vacía]");
        } else {
            int i = 0;
            for (byte[] item : context.getStack()) {
                String display;
                if (item.length == 0) {
                    display = "EMPTY (OP_0/FALSE)";
                } else {
                    display = new String(item);
                    if (display.length() > 30) {
                        display = display.substring(0, 27) + "...";
                    }
                }
                System.out.println("   " + (i == 0 ? "→ " : "  ") + display);
                i++;
            }
        }
        System.out.println("   " + "-".repeat(40));
    }

    /**
     * Obtiene el tamaño actual de la pila (útil para pruebas).
     *
     * @return la cantidad actual de elementos disponibles en la pila.
     */
    public int getStackSize() {
        return context.stackSize();
    }

    /**
     * Limpia la pila (útil para pruebas).
     */
    public void clearStack() {
        context.clearStack();
    }
}