package io.github.vort2014.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// https://stackoverflow.com/questions/367706/how-do-i-parse-command-line-arguments-in-java
// test
// docker container run --detach --name=kafka --publish 9092:9092 apache/kafka
// key detach is present in the response map
public interface CommandLineArgumentUtil {

    static Map<String, String> parseArguments(Set<String> requiredArgumentNames, String[] args) {

        // remove leading and trailing spaces
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                args[i] = args[i].strip();
            }
        }

        var res = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg != null && arg.startsWith("--")) {
                String key = arg.substring(2); // Remove the "--"
                String value = null;

                // Check if the next argument is a value (not another key)
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    value = args[i + 1];
                    if ("=".equals(value)) { // Handle cases like ["--end", "=", "Berlin"]
                        if (i + 2 < args.length) {
                            value = args[i + 2];
                        } else {
                            value = null; // if command line ends with =
                        }
                    }
                    i++; // Increment the index to skip the value
                } else if (key.contains("=")) {
                    // Handle cases like --transportation-method=diesel-car-medium
                    String[] parts = key.split("=", 2);
                    key = parts[0];
                    value = parts[1];
                }

                // Remove quotes from value if present
                if (value != null) {
                    value = value.replace("\"", "")
                            .strip();
                }

                res.put(key, value);
            }
        }

        // Check for required arguments
        if (requiredArgumentNames != null && !requiredArgumentNames.isEmpty()) {
            var sb = new StringBuilder();
            for (var requiredArgumentName: requiredArgumentNames) {
                var value = res.get(requiredArgumentName);
                if (value == null) sb.append("Missing required argument: --").append(requiredArgumentName).append("\n");
            }
            if (!sb.isEmpty()) throw new IllegalArgumentException(sb.toString());
        }

        return res;
    }
}
