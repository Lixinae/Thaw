package fr.umlv.thaw.server.handlers;

import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.logger.ThawLogger;
import fr.umlv.thaw.user.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Session;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Project :Thaw
 * Created by Narex on 05/12/2016.
 */
public class Tools {

    public static byte[] hashToSha256(String password) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        return sha256.digest(passBytes);
    }

    static void answerToRequest(HttpServerResponse response, int code, Object answer, ThawLogger thawLogger) {
        String tmp = Json.encodePrettily(answer);
        if (code > 200 && code < 300) {
            thawLogger.log(Level.INFO, "code: " + code + "\nanswer: " + tmp);
        } else {
            thawLogger.log(Level.WARNING, "code: " + code + "\nanswer: " + tmp);
        }

        response.setStatusCode(code)
                .putHeader("content-type", "application/json")
                .end(tmp);
    }

    static Optional<User> checkIfUserIsConnectedAndAuthorized(Session session, HttpServerResponse response, ThawLogger thawLogger) {
        User user = session.get("user");
        if (user == null) {
            Tools.answerToRequest(response, 400, "User not in authorized list", thawLogger);
            return Optional.empty();
        }
        return Optional.of(user);
    }


    static Optional<Channel> findChannelInList(List<Channel> channels, String channelName) {
        if (verifyEmptyOrNull(channelName)) {
            return Optional.empty();
        }
        return channels.stream()
                .filter(c -> c.getChannelName().contentEquals(channelName))
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
