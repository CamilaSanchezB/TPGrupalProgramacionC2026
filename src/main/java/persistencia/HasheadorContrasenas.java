package persistencia;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashea y verifica contraseñas con PBKDF2
 * <pre>iteraciones:salt:hash</pre>
 */
public class HasheadorContrasenas {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";


    private static final int ITERACIONES = 210000;

    private static final int BYTES_SALT = 16;
    private static final int BITS_HASH = 256;
    private static final String SEPARADOR = ":";

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private HasheadorContrasenas() {
    }

    /**
     * <b>PRE</b>
     * - contrasena != null y no vacía
     * <b>POST</b>
     * - devuelve "iteraciones:salt:hash" listo para guardar en la base
     * - dos llamadas con la misma contraseña dan resultados distintos (salt aleatorio)
     *
     * @param contrasena texto plano; es char[] porque es lo que pide PBEKeySpec
     *                    y lo que devuelve JPasswordField.getPassword()
     */
    public static String hashear(char[] contrasena) {
        assert contrasena != null && contrasena.length > 0
                : "La contrasena no puede estar vacia.";
        byte[] salt = new byte[BYTES_SALT];
        ALEATORIO.nextBytes(salt);
        byte[] hash = calcular(contrasena, salt, ITERACIONES);
        return ITERACIONES + SEPARADOR + aHex(salt) + SEPARADOR + aHex(hash);
    }

    /**
     * <b>PRE</b>
     * - contrasena != null
     * - guardado tiene el formato que devuelve hashear()
     * <b>POST</b>
     * - devuelve true si y solo si la contraseña corresponde al hash guardado
     *
     * @param guardado el valor de la columna contraseña
     * @throws IllegalArgumentException si guardado no tiene el formato esperado
     */
    public static boolean coincide(char[] contrasena, String guardado) {
        assert contrasena != null : "La contrasena no puede ser nula.";
        String[] partes = guardado == null ? new String[0] : guardado.split(SEPARADOR);
        if (partes.length != 3) {
            throw new IllegalArgumentException(
                    "El hash guardado no tiene el formato iteraciones:salt:hash");
        }
        int iteraciones = Integer.parseInt(partes[0]);
        byte[] salt = deHex(partes[1]);
        byte[] esperado = deHex(partes[2]);
        // Se recalcula con el salt y las iteraciones con que se creó el hash original.
        byte[] calculado = calcular(contrasena, salt, iteraciones);
        return sonIguales(esperado, calculado);
    }

    private static byte[] calcular(char[] contrasena, byte[] salt, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(contrasena, salt, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El JDK no tiene " + ALGORITMO, e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Parametros invalidos para " + ALGORITMO, e);
        } finally {
            spec.clearPassword();
        }
    }

    private static boolean sonIguales(byte[] esperado, byte[] calculado) {
        int diferencia = esperado.length ^ calculado.length;
        for (int i = 0; i < esperado.length && i < calculado.length; i++) {
            diferencia |= esperado[i] ^ calculado[i];
        }
        return diferencia == 0;
    }

    private static String aHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(Character.forDigit((b >> 4) & 0xF, 16));
            hex.append(Character.forDigit(b & 0xF, 16));
        }
        return hex.toString();
    }

    private static byte[] deHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
