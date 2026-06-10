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

            List<String> tokens = parseTokens(input);
            if (tokens.isEmpty()) {
                continue;
            }
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
                if (target.isEmpty()) {
                    System.out.println("cd: " + target + ": No such file or directory");
                    continue;
                }

                Path targetPath;
                if (target.equals("~") || target.startsWith("~/")) {
                    String home = System.getenv("HOME");
                    if (home == null || home.isEmpty()) {
                        System.out.println("cd: " + target + ": No such file or directory");
                        continue;
                    }

                    String relative = target.equals("~") ? "" : target.substring(2);
                    targetPath = relative.isEmpty() ? Path.of(home) : Path.of(home, relative);
                } else {
                    targetPath = Path.of(target);
                }

                Path resolvedPath = targetPath.isAbsolute()
                        ? targetPath
                        : currentDirectory.resolve(targetPath);

                if (Files.isDirectory(resolvedPath)) {
                    currentDirectory = resolvedPath.normalize();
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

    private static List<String> parseTokens(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean tokenStarted = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                } else {
                    current.append(c);
                }
                tokenStarted = true;
            } else if (c == '\'') {
                inSingleQuote = true;
                tokenStarted = true;
            } else if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }
            } else {
                current.append(c);
                tokenStarted = true;
            }
        }

        if (tokenStarted) {
            tokens.add(current.toString());
        }

        return tokens;
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
