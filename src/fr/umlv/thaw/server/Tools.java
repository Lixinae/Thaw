package fr.umlv.thaw.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tools {

    /**
     * This function take a String representation of a password and
     * encrypt it with the SHA256 algorithm.
     *
     * @param password the password to encrypt
     * @return the password encrypted with SHA256 algorithm
     */
    public static String toSHA256(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        md.update(new byte[]{5, 3, 3, 'd'});
        byte[] data = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return IntStream.range(0, data.length)
                .mapToObj(i -> String.format("%02x", data[i]))
                .collect(Collectors.joining());
    }
}
