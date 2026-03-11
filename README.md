# Intérprete de Bitcoin Script - Proyecto 1.

Este proyecto implementa un intérprete simplificado del lenguaje de script de Bitcoin utilizando Java. Permite simular el proceso de validación de transacciones P2PKH (Pay-to-Public-Key-Hash) en un entorno académico.

## Estructura del Proyecto para la fase 1
* **Main.java**: Punto de entrada que ejecuta una demostración de transacción P2PKH.
* **script/ScriptInterpreter.java**: Núcleo del intérprete. Contiene la pila (`ArrayDeque`) y la lógica de los Opcodes.

## Ejecución
Compilar y ejecutar `Main.java`. El programa mostrará el flujo de la pila y el resultado de la validación: