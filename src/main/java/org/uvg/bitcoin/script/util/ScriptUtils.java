package org.uvg.bitcoin.script.util;

import java.util.Arrays;
import java.util.Optional;

/**
 * Clase de utilidades para el intérprete de Bitcoin Script.
 * Proporciona métodos auxiliares para manipulación de bytes,
 * conversiones y validaciones.
 */
public class ScriptUtils {

    /**
     * Convierte un byte array a Integer de forma segura.
     * Retorna Optional.empty() si no es un número válido.
     *
     * @param val Byte array a convertir
     * @return Optional con el valor numérico o empty
     */
    public Optional<Integer> toInt(byte[] val) {
        if (val == null || val.length == 0) {
            return Optional.empty();
        }
        try {
            String str = new String(val).trim();
            // Limpia el prefijo OP_ para poder hacer cálculos con cualquier número
            if (str.startsWith("OP_")) {
                str = str.substring(3);
            }
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Serializa un número a byte array.
     *
     * @param num Número a serializar
     * @return Byte array con la representación del número
     */
    public byte[] serializeNumber(int num) {
        return String.valueOf(num).getBytes();
    }

    /**
     * Copia un byte array de forma segura.
     *
     * @param original Array original
     * @return Nueva copia del array
     */
    public byte[] copyOf(byte[] original) {
        if (original == null) {
            return new byte[0];
        }
        return Arrays.copyOf(original, original.length);
    }

    /**
     * Determina si un valor es verdadero según Bitcoin Script:
     * - Cualquier valor diferente de cero es verdadero
     * - Array vacío es falso
     * - Representación numérica: 0 es falso, cualquier otro es verdadero
     *
     * @param value Valor a evaluar
     * @return true si el valor es verdadero, false si es falso
     */
    public boolean isTrue(byte[] value) {
        if (value == null || value.length == 0) {
            return false;
        }

        Optional<Integer> num = toInt(value);
        if (num.isPresent()) {
            return num.get() != 0;
        }

        for (byte b : value) {
            if (b != 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Compara dos byte arrays.
     *
     * @param a Primer array
     * @param b Segundo array
     * @return true si son iguales
     */
    public boolean equals(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    /**
     * Convierte un byte array a String, manejando nulls.
     *
     * @param data Byte array
     * @return String representation
     */
    public String toString(byte[] data) {
        if (data == null) {
            return "null";
        }
        if (data.length == 0) {
            return "EMPTY";
        }
        return new String(data);
    }
}