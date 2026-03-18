# Intérprete de Bitcoin Script - Máquina Virtual en Java


Este proyecto implementa una **Máquina Virtual (Intérprete) del lenguaje de script de Bitcoin**, desarrollada en Java. Permite evaluar y simular el proceso de validación de transacciones bajo el estándar P2PKH, así como operaciones matemáticas, lógicas y de control de flujo anidado.


## Equipo de Desarrollo (Grupo 9)
* **Kenett Ortega** - 25777
* **Cristian Orellana** - 25664
* **Sergio Tepaz** - 25787

---

## Arquitectura y Patrones de Diseño

El intérprete fue construido priorizando la eficiencia asintótica y el código limpio:

* **Patrón Registry (Command):** Se implementó un `OpcodeRegistry` que mapea cada instrucción (Opcode) a su lógica de ejecución en tiempo $O(1)$. Esto evita estructuras *switch-case* masivas y cumple con el principio Open/Closed.
* **Modelo Basado en Pila (Stack-Based):** Se utiliza `ArrayDeque<byte[]>` del Java Collections Framework para la pila principal y la pila de ejecución, garantizando operaciones LIFO en tiempo constante $O(1)$ sin bloqueos de sincronización innecesarios.
* **Contexto de Ejecución (`ExecutionContext`):** Un gestor de estado independiente que permite soportar **Control de Flujo Anidado** (múltiples `OP_IF` dentro de otros `OP_IF`) sin perder el rastro de la ejecución lógica de las ramas.

---

## Opcodes Implementados

La máquina virtual soporta los siguientes subconjuntos del lenguaje original de Bitcoin:

| Categoría | Opcodes |
| :--- | :--- |
| **Literales** | `OP_0` al `OP_16`, `PUSHDATA`  |
| **Manipulación de Pila**| `OP_DUP`, `OP_DROP`, `OP_SWAP`, `OP_OVER` |
| **Aritmética** | `OP_ADD`, `OP_SUB` |
| **Lógica Booleana** | `OP_NOT`, `OP_BOOLAND`, `OP_BOOLOR` |
| **Comparación** | `OP_EQUAL`, `OP_EQUALVERIFY`, `OP_NUMEQUALVERIFY`, `OP_LESSTHAN`, `OP_GREATERTHAN`, `OP_LESSTHANOREQUAL`, `OP_GREATERTHANOREQUAL` |
| **Control de Flujo** | `OP_IF`, `OP_NOTIF`, `OP_ELSE`, `OP_ENDIF`, `OP_VERIFY`, `OP_RETURN` |
| **Criptografía (Mocks)**| `OP_HASH160`, `OP_SHA256`, `OP_HASH256`, `OP_CHECKSIG`, `OP_CHECKSIGVERIFY` |

---

## Compilación y Ejecución

El proyecto utiliza **Maven** para la gestión de dependencias y empaquetado.

### Requisitos
* JDK 24 (o versión compatible superior a Java 17)
* Apache Maven

### Pasos

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd BitcoinProject

# 2. Compilar el proyecto
mvn compile

# 3. Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="org.uvg.bitcoin.Main"

# 4. Ejecutar las pruebas unitarias
mvn test
```

> También puede ejecutarse directamente desde IntelliJ IDEA abriendo el proyecto y corriendo `Main.java`.

---

## Uso Interactivo

Al iniciar, el programa presenta un menú con las siguientes opciones:

```
=== INTÉRPRETE DE BITCOIN SCRIPT (GRUPO 9) ===

Seleccione el método de entrada:
1. Escribir script en consola
2. Leer script desde archivo script.txt
3. Demo P2PKH (Pay-to-Public-Key-Hash)
4. Demo Condicionales
5. Demo Operaciones Aritméticas
6. Salir
```

Luego de seleccionar una fuente de script, se elige el modo de ejecución:

| Modo | Descripción |
| :--- | :--- |
| **Ejecución Directa** | Muestra únicamente el resultado final (`EXITOSA` / `FALLIDA`) |
| **Trace Continuo** | Imprime el estado completo de la pila después de cada token procesado |
| **Trace Paso a Paso** | Pausa la ejecución en cada operación esperando que el usuario presione Enter |

### Escribir un script manualmente (Opción 1)

```
Ingrese el script completo separado por espacios:
> OP_3 OP_4 OP_ADD OP_7 OP_EQUAL OP_VERIFY OP_1
Resultado Final de Validación: EXITOSA (TRUE)
```

### Leer desde archivo (Opción 2)

Edite el archivo `script.txt` en la raíz del proyecto con un script en una sola línea y seleccione la opción 2. Ejemplo del archivo incluido:

```
OP_3 OP_4 OP_ADD OP_7 OP_NUMEQUALVERIFY OP_5 OP_DUP OP_EQUAL OP_VERIFY ... OP_1
```

---

## Demo P2PKH

La demo **Pay-to-Public-Key-Hash** simula el mecanismo de validación de transacciones más común en Bitcoin. El script combina un `scriptSig` (aportado por el emisor) con un `scriptPubKey` (establecido por el receptor):

```
scriptSig:    VALID_SIGNATURE  PUBKEY
scriptPubKey: OP_DUP  OP_HASH160  HASH160_PUBKEY  OP_EQUALVERIFY  OP_CHECKSIG
```

Evolución de la pila paso a paso:

```
Inicio          → [VALID_SIGNATURE, PUBKEY]
OP_DUP          → [VALID_SIGNATURE, PUBKEY, PUBKEY]
OP_HASH160      → [VALID_SIGNATURE, PUBKEY, HASH160_PUBKEY]
<hash esperado> → [VALID_SIGNATURE, PUBKEY, HASH160_PUBKEY, HASH160_PUBKEY]
OP_EQUALVERIFY  → [VALID_SIGNATURE, PUBKEY]   ✓ los hashes coinciden
OP_CHECKSIG     → [1]                          ✓ firma válida
```

La demo ejecuta automáticamente tres escenarios:

| Caso | Firma | Hash de clave pública | Resultado esperado |
| :--- | :--- | :--- | :--- |
| ✅ Caso 1 | `VALID_SIGNATURE` | Correcto | `VÁLIDO` |
| ❌ Caso 2 | `BAD_SIGNATURE` | Correcto | `INVÁLIDO` |
| ❌ Caso 3 | `VALID_SIGNATURE` | Incorrecto | `INVÁLIDO` |

---

## Estructura del Proyecto

```
BitcoinProject/
├── src/
│   ├── main/java/org/uvg/bitcoin/
│   │   ├── Main.java                          # Interfaz interactiva de consola
│   │   └── script/
│   │       ├── ScriptInterpreter.java         # Motor principal del intérprete
│   │       ├── model/
│   │       │   └── ExecutionContext.java      # Pila + control de flujo anidado
│   │       ├── opcodes/
│   │       │   ├── Opcode.java                # Interfaz funcional (@FunctionalInterface)
│   │       │   ├── OpcodeRegistry.java        # Registro central (patrón Command)
│   │       │   ├── ArithmeticOpcodes.java     # ADD, SUB, comparaciones numéricas
│   │       │   ├── LogicalOpcodes.java        # EQUAL, NOT, BOOLAND, BOOLOR
│   │       │   ├── StackOpcodes.java          # DUP, DROP, SWAP, OVER
│   │       │   ├── FlowControlOpcodes.java    # VERIFY, RETURN
│   │       │   └── CryptoOpcodes.java         # HASH160, SHA256, CHECKSIG (simulados)
│   │       └── util/
│   │           └── ScriptUtils.java           # Conversión de bytes, isTrue, toInt
│   └── test/java/org/uvg/bitcoin/script/
│       ├── ScriptInterpreterTest.java
│       ├── ScriptInterpreterControlFlowTest.java
│       ├── ArithmeticOpcodesTest.java
│       ├── LogicalOpcodesTest.java
│       ├── StackOpcodesTest.java
│       ├── CryptoOpcodesTest.java
│       ├── FlowControlOpcodesTest.java
│       ├── ExecutionContextTest.java
│       ├── OpcodeRegistryTest.java
│       └── ScriptUtilsTest.java
├── script.txt                                 # Script de prueba integral
└── pom.xml
```

---

## Pruebas Unitarias

Las pruebas están escritas con **JUnit 5** y cubren todos los módulos del sistema:

| Clase de prueba | Qué valida |
| :--- | :--- |
| `ScriptInterpreterTest` | Flujos P2PKH completos, IF/ELSE, aritmética integrada, pila vacía |
| `ScriptInterpreterControlFlowTest` | VERIFY, RETURN, IF anidados, operandos insuficientes, tipos inválidos |
| `ArithmeticOpcodesTest` | ADD, SUB, NUMEQUALVERIFY con igualdad y desigualdad |
| `LogicalOpcodesTest` | EQUAL, NOT, BOOLAND con distintas combinaciones |
| `StackOpcodesTest` | DUP, DROP, SWAP, OVER con verificación byte a byte |
| `CryptoOpcodesTest` | Prefijos de hash simulados, CHECKSIG válido e inválido |
| `FlowControlOpcodesTest` | VERIFY con `true`, `false` y pila vacía; RETURN siempre falla |
| `ExecutionContextTest` | Push/pop de pila, gestión de ifStack, reset de estado |
| `OpcodeRegistryTest` | Presencia de todos los opcodes, valores de literales OP_0–OP_16 |
| `ScriptUtilsTest` | `toInt` con enteros válidos e inválidos, `isTrue` con cero y vacío |

```bash
mvn test
```
