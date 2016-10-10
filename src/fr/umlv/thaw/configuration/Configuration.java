package fr.umlv.thaw.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project :Thaw
 * Created by Narex on 10/10/2016.
 */
public class Configuration {

    private Map<String, String> hashMap;
    private final String configFilePathName;

    public Configuration(String configFilePathName) {
        this.hashMap = new HashMap<>();
        this.configFilePathName = Objects.requireNonNull(configFilePathName);
    }

    /**
     * Reads the configuration file and stores all the data in a map
     *
     * @throws IOException If the specified file doesn't exist
     */
    public void readConfigurationFromFile() throws IOException {
        Path configFilePath = Paths.get(configFilePathName); /* "./botConfig/botConfigurations.txt" */
        try (Stream<String> lines = Files.lines(configFilePath).filter(s -> !s.startsWith("#"))) {
            hashMap = lines
                    .map(l -> l.split(":"))
                    .collect(Collectors.toMap(tokens -> tokens[0], tokens -> tokens[1]));
        }
    }
}
