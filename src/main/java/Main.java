import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        Set<String> builtins = Set.of("echo", "exit", "type", "pwd", "cd");
        Path currentDirectory = Paths.get("").toAbsolutePath().normalize();

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.isBlank()) {
                continue;
            }

            List<String> tokens = new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));
            String command = tokens.get(0);

            if (command.equals("exit") && (tokens.size() == 1 || (tokens.size() == 2 && tokens.get(1).equals("0")))) {
                break;
            } else if (command.equals("type")) {
                String query = tokens.size() > 1 ? tokens.get(1) : "";
                if (query.isEmpty()) {
                    continue;
                }

                if (builtins.contains(query)) {
                    System.out.println(query + " is a shell builtin");
                } else {
                    String executablePath = findExecutableInPath(query);
                    if (executablePath != null) {
                        System.out.println(query + " is " + executablePath);
                    } else {
                        System.out.println(query + ": not found");
                    }
                }
            } else if (command.equals("echo")) {
                String output = tokens.size() > 1 ? String.join(" ", tokens.subList(1, tokens.size())) : "";
                System.out.println(output);
            } else if (command.equals("pwd")) {
                System.out.println(currentDirectory);
            } else if (command.equals("cd")) {
                String target = tokens.size() > 1 ? tokens.get(1) : "";
                Path targetPath = Path.of(target);

                if (targetPath.isAbsolute() && Files.isDirectory(targetPath)) {
                    currentDirectory = targetPath.normalize();
                } else {
                    System.out.println("cd: " + target + ": No such file or directory");
                }
            } else {
                String executablePath = findExecutableInPath(command);
                if (executablePath == null) {
                    System.out.println(command + ": command not found");
                    continue;
                }

                runExternalCommand(tokens, currentDirectory);
            }
        }
    }

    private static void runExternalCommand(List<String> tokens, Path currentDirectory)
            throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(tokens);
        builder.directory(currentDirectory.toFile());
        builder.redirectErrorStream(true);
        builder.inheritIO();
        Process process = builder.start();
        process.waitFor();
    }

    private static String findExecutableInPath(String command) {
        if (command.isEmpty()) {
            return null;
        }

        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return null;
        }

        String[] directories = pathEnv.split(File.pathSeparator);
        for (String directory : directories) {
            if (directory.isEmpty()) {
                continue;
            }

            Path candidate = Path.of(directory, command);
            if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                return candidate.toString();
            }
        }

        return null;
    }
}
