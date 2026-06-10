import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;

public class test3 {
    public static void main(String[] args) throws Exception {
        Set<String> commandNames = new HashSet<>();
        String pathEnvExt = System.getenv("PATH");
        if (pathEnvExt != null && !pathEnvExt.isEmpty()) {
            String[] directories = pathEnvExt.split(File.pathSeparator);
            for (String directory : directories) {
                if (directory.isEmpty()) continue;
                Path dirPath = Path.of(directory);
                if (Files.isDirectory(dirPath)) {
                    try (java.util.stream.Stream<Path> stream = Files.list(dirPath)) {
                        stream.filter(Files::isRegularFile)
                              .filter(Files::isExecutable)
                              .forEach(p -> commandNames.add(p.getFileName().toString()));
                    } catch (IOException e) {}
                }
            }
        }
        System.out.println("custom_executable present: " + commandNames.contains("custom_executable"));
    }
}
