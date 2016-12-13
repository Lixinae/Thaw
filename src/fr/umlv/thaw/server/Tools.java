package fr.umlv.thaw.server;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class Tools {

//    /**
//     * This function take a String representation of a password and
//     * encrypt it with the SHA256 algorithm.
//     *
//     * @param password the password to encrypt
//     * @return the password encrypted with SHA256 algorithm
//     * @throws NoSuchAlgorithmException if the algorithm haven't been found
//     */
//    public static byte[] hashToSha256(String password) throws NoSuchAlgorithmException {
//        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
//        return sha256.digest(passBytes);
//    }

//    static boolean checkPassword(String shaDigest, String password) {
//        return shaDigest.equals(toSHA256(password));
//    }

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

//    /**
//     * This function take a byte Array that represented an encrypted message
//     * by SHA256 protocol and return a String representation of the message
//     * in hexadecimal format.
//     * The format is always 64 bits long
//     *
//     * @param cryptedMessage message encrypted by SHA256 protocol
//     * @return a String representation of the encrypted message
//     */
//    public static String sha256ToString(byte[] cryptedMessage) {
//        return String.format("%064x", new BigInteger(1, cryptedMessage));
//    }

    static void answerToRequest(HttpServerResponse response, int code, Object answer, ThawLogger thawLogger) {
        String tmp = Json.encodePrettily(answer);
        if (code >= 200 && code < 300) {
            thawLogger.log(Level.INFO, "code: " + code + "\nanswer: " + tmp);
        } else {
            thawLogger.log(Level.WARNING, "code: " + code + "\nanswer: " + tmp);
        }

        response.setStatusCode(code)
                .putHeader("content-type", "application/json")
                .end(tmp);
    }

    static Optional<Channel> findChannelInList(List<Channel> channels, String channelName) {
        if (verifyEmptyOrNull(channelName)) {
            return Optional.empty();
        }
        return channels.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .findFirst();
    }

    static boolean verifyEmptyOrNull(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
