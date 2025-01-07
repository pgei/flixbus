package main.java.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    /**
     * Hashes das Passwort mit dem SHA-256-Algorithmus.
     *
     * @param password Das Klartext-Passwort, das gehasht werden soll.
     * @return Das gehashte Passwort als hexadezimale Zeichenkette.
     */
    public static String hashPassword(String password) {
        try {
            // Initialisierung des SHA-256-Digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Berechnung des Hash-Werts aus den Passwort-Bytes
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Umwandlung des Hash-Werts in eine hexadezimale Zeichenkette
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Mistake occured when hashing password!", e);
        }
    }
}