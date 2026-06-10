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

            Path redirectOutput = null;
            boolean appendOutput = false;
            Path redirectError = null;
            for (int i = 0; i < tokens.size() - 1; i++) {
                String token = tokens.get(i);
                if (token.equals(">>") || token.equals("1>>")) {
                    Path redirectPath = Path.of(tokens.get(i + 1));
                    redirectOutput = redirectPath.isAbsolute()
                            ? redirectPath
                            : currentDirectory.resolve(redirectPath);
                    appendOutput = true;
                    tokens.remove(i + 1);
                    tokens.remove(i);
                    break;
                }
            }
            for (int i = 0; i < tokens.size() - 1; i++) {
                String token = tokens.get(i);
                if (token.equals(">") || token.equals("1>")) {
                    Path redirectPath = Path.of(tokens.get(i + 1));
                    redirectOutput = redirectPath.isAbsolute()
                            ? redirectPath
                            : currentDirectory.resolve(redirectPath);
                    appendOutput = false;
                    tokens.remove(i + 1);
                    tokens.remove(i);
                    break;
                }
            }
            for (int i = 0; i < tokens.size() - 1; i++) {
                String token = tokens.get(i);
                if (token.equals("2>")) {
                    Path redirectPath = Path.of(tokens.get(i + 1));
                    redirectError = redirectPath.isAbsolute()
                            ? redirectPath
                            : currentDirectory.resolve(redirectPath);
                    tokens.remove(i + 1);
                    tokens.remove(i);
                    break;
                }
            }

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
                    writeOutput(query + " is a shell builtin", redirectOutput, appendOutput);
                } else {
                    String executablePath = findExecutableInPath(query);
                    if (executablePath != null) {
                        writeOutput(query + " is " + executablePath, redirectOutput, appendOutput);
                    } else {
                        writeOutput(query + ": not found", redirectOutput, appendOutput);
                    }
                }
                if (redirectError != null) {
                    Files.writeString(redirectError, "",
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
                }
            } else if (command.equals("echo")) {
                String output = tokens.size() > 1 ? String.join(" ", tokens.subList(1, tokens.size())) : "";
                writeOutput(output, redirectOutput, appendOutput);
                if (redirectError != null) {
                    Files.writeString(redirectError, "",
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
                }
            } else if (command.equals("pwd")) {
                writeOutput(currentDirectory.toString(), redirectOutput, appendOutput);
                if (redirectError != null) {
                    Files.writeString(redirectError, "",
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
                }
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

                runExternalCommand(tokens, currentDirectory, redirectOutput, appendOutput, redirectError);
            }
        }
    }

    private static void writeOutput(String output, Path redirectOutput, boolean appendOutput) throws IOException {
        if (redirectOutput == null) {
            System.out.println(output);
        } else {
            if (appendOutput) {
                Files.writeString(redirectOutput, output + System.lineSeparator(),
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND);
            } else {
                Files.writeString(redirectOutput, output + System.lineSeparator(),
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

    private static void runExternalCommand(List<String> tokens, Path currentDirectory, Path redirectOutput, boolean appendOutput, Path redirectError)
            throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(tokens);
        builder.directory(currentDirectory.toFile());
        if (redirectOutput == null) {
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        } else if (appendOutput) {
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(redirectOutput.toFile()));
        } else {
            builder.redirectOutput(redirectOutput.toFile());
        }
        if (redirectError == null) {
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        } else {
            builder.redirectError(redirectError.toFile());
        }
        Process process = builder.start();
        process.waitFor();
    }

    private static List<String> parseTokens(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean tokenStarted = false;
        boolean escaping = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (escaping) {
                current.append(c);
                tokenStarted = true;
                escaping = false;
            } else if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                } else {
                    current.append(c);
                }
                tokenStarted = true;
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                } else if (c == '\\' && i + 1 < input.length()) {
                    char next = input.charAt(i + 1);
                    if (next == '"' || next == '\\') {
                        current.append(next);
                        i++;
                    } else {
                        current.append(c);
                    }
                } else {
                    current.append(c);
                }
                tokenStarted = true;
            } else if (c == '\\') {
                escaping = true;
                tokenStarted = true;
            } else if (c == '\'') {
                inSingleQuote = true;
                tokenStarted = true;
            } else if (c == '"') {
                inDoubleQuote = true;
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

        if (escaping) {
            current.append('\\');
            tokenStarted = true;
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
