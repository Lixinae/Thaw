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

//TODO : doc ... JAVAdoc
public class Configuration {

    private final String configFilePathName;
    private Map<String, String> hashMap;

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
                    .collect(Collectors.toMap(tokens -> tokens[0], tokens -> "./botConfig/" + tokens[1]));
        }
    }

    public void printMap() {
        hashMap.forEach((k, v) -> System.out.println("Key = " + k + "\n" + "Value = " + v));
    }

//    public void load
    /*
        -> http://stackoverflow.com/questions/1318347/how-to-use-java-property-files#1318391

        How to use properties format from "java.util.Properties"

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("path/filename"));
        } catch (IOException e) {
            ...
        }


        for(String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.out.println(key + " => " + value);
        }



     */
}
