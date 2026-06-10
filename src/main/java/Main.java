// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Scanner;
// import java.util.Set; 

// public class Main {
//     public static void main(String[] args) throws Exception {
//         // TODO: Uncomment the code below to pass the first stage
//         Scanner scanner = new Scanner(System.in);
//         Set<String> builtins = Set.of("echo", "exit", "type", "pwd", "cd");
//         Path currentDirectory = Paths.get("").toAbsolutePath().normalize();

//         while (true) {
//             System.out.print("$ ");
//             String input = scanner.nextLine();
//             String trimmedInput = input.stripTrailing();
//             if (!trimmedInput.equals(input)) {
//                 String prefix = trimmedInput.trim();
//                 List<String> matches = new ArrayList<>();
//                 for (String builtin : Arrays.asList("echo", "exit")) {
//                     if (builtin.startsWith(prefix)) {
//                         matches.add(builtin);
//                     }
//                 }
//                 if (matches.size() == 1) {
//                     System.out.println("$ " + matches.get(0) + " ");
//                     continue;
//                 }
//             }

//             if (input.isBlank()) {
//                 continue;
//             }

//             List<String> tokens = parseTokens(input);
//             if (tokens.isEmpty()) {
//                 continue;
//             }

//             Path redirectOutput = null;
//             boolean appendOutput = false;
//             Path redirectError = null;
//             boolean appendError = false;
//             for (int i = 0; i < tokens.size() - 1; i++) {
//                 String token = tokens.get(i);
//                 if (token.equals(">>") || token.equals("1>>")) {
//                     Path redirectPath = Path.of(tokens.get(i + 1));
//                     redirectOutput = redirectPath.isAbsolute()
//                             ? redirectPath
//                             : currentDirectory.resolve(redirectPath);
//                     appendOutput = true;
//                     tokens.remove(i + 1);
//                     tokens.remove(i);
//                     break;
//                 }
//             }
//             for (int i = 0; i < tokens.size() - 1; i++) {
//                 String token = tokens.get(i);
//                 if (token.equals(">") || token.equals("1>")) {
//                     Path redirectPath = Path.of(tokens.get(i + 1));
//                     redirectOutput = redirectPath.isAbsolute()
//                             ? redirectPath
//                             : currentDirectory.resolve(redirectPath);
//                     appendOutput = false;
//                     tokens.remove(i + 1);
//                     tokens.remove(i);
//                     break;
//                 }
//             }
//             for (int i = 0; i < tokens.size() - 1; i++) {
//                 String token = tokens.get(i);
//                 if (token.equals("2>>")) {
//                     Path redirectPath = Path.of(tokens.get(i + 1));
//                     redirectError = redirectPath.isAbsolute()
//                             ? redirectPath
//                             : currentDirectory.resolve(redirectPath);
//                     appendError = true;
//                     tokens.remove(i + 1);
//                     tokens.remove(i);
//                     break;
//                 }
//             }
//             for (int i = 0; i < tokens.size() - 1; i++) {
//                 String token = tokens.get(i);
//                 if (token.equals("2>")) {
//                     Path redirectPath = Path.of(tokens.get(i + 1));
//                     redirectError = redirectPath.isAbsolute()
//                             ? redirectPath
//                             : currentDirectory.resolve(redirectPath);
//                     appendError = false;
//                     tokens.remove(i + 1);
//                     tokens.remove(i);
//                     break;
//                 }
//             }

//             if (tokens.isEmpty()) {
//                 continue;
//             }
//             String command = tokens.get(0);

//             if (command.equals("exit") && (tokens.size() == 1 || (tokens.size() == 2 && tokens.get(1).equals("0")))) {
//                 break;
//             } else if (command.equals("type")) {
//                 String query = tokens.size() > 1 ? tokens.get(1) : "";
//                 if (query.isEmpty()) {
//                     continue;
//                 }

//                 if (builtins.contains(query)) {
//                     writeOutput(query + " is a shell builtin", redirectOutput, appendOutput);
//                 } else {
//                     String executablePath = findExecutableInPath(query);
//                     if (executablePath != null) {
//                         writeOutput(query + " is " + executablePath, redirectOutput, appendOutput);
//                     } else {
//                         writeOutput(query + ": not found", redirectOutput, appendOutput);
//                     }
//                 }
//                 if (redirectError != null) {
//                     if (appendError) {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.APPEND);
//                     } else {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
//                     }
//                 }
//             } else if (command.equals("echo")) {
//                 String output = tokens.size() > 1 ? String.join(" ", tokens.subList(1, tokens.size())) : "";
//                 writeOutput(output, redirectOutput, appendOutput);
//                 if (redirectError != null) {
//                     if (appendError) {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.APPEND);
//                     } else {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
//                     }
//                 }
//             } else if (command.equals("pwd")) {
//                 writeOutput(currentDirectory.toString(), redirectOutput, appendOutput);
//                 if (redirectError != null) {
//                     if (appendError) {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.APPEND);
//                     } else {
//                         Files.writeString(redirectError, "",
//                                 java.nio.file.StandardOpenOption.CREATE,
//                                 java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
//                     }
//                 }
//             } else if (command.equals("cd")) {
//                 String target = tokens.size() > 1 ? tokens.get(1) : "";
//                 if (target.isEmpty()) {
//                     System.out.println("cd: " + target + ": No such file or directory");
//                     continue;
//                 }

//                 Path targetPath;
//                 if (target.equals("~") || target.startsWith("~/")) {
//                     String home = System.getenv("HOME");
//                     if (home == null || home.isEmpty()) {
//                         System.out.println("cd: " + target + ": No such file or directory");
//                         continue;
//                     }

//                     String relative = target.equals("~") ? "" : target.substring(2);
//                     targetPath = relative.isEmpty() ? Path.of(home) : Path.of(home, relative);
//                 } else {
//                     targetPath = Path.of(target);
//                 }

//                 Path resolvedPath = targetPath.isAbsolute()
//                         ? targetPath
//                         : currentDirectory.resolve(targetPath);

//                 if (Files.isDirectory(resolvedPath)) {
//                     currentDirectory = resolvedPath.normalize();
//                 } else {
//                     System.out.println("cd: " + target + ": No such file or directory");
//                 }
//             } else {
//                 String executablePath = findExecutableInPath(command);
//                 if (executablePath == null) {
//                     System.out.println(command + ": command not found");
//                     continue;
//                 }

//                 runExternalCommand(tokens, currentDirectory, redirectOutput, appendOutput, redirectError, appendError);
//             }
//         }
//     }

//     private static void writeOutput(String output, Path redirectOutput, boolean appendOutput) throws IOException {
//         if (redirectOutput == null) {
//             System.out.println(output);
//         } else {
//             if (appendOutput) {
//                 Files.writeString(redirectOutput, output + System.lineSeparator(),
//                         java.nio.file.StandardOpenOption.CREATE,
//                         java.nio.file.StandardOpenOption.APPEND);
//             } else {
//                 Files.writeString(redirectOutput, output + System.lineSeparator(),
//                         java.nio.file.StandardOpenOption.CREATE,
//                         java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
//             }
//         }
//     }

//     private static void runExternalCommand(List<String> tokens, Path currentDirectory, Path redirectOutput, boolean appendOutput, Path redirectError, boolean appendError)
//             throws IOException, InterruptedException {
//         ProcessBuilder builder = new ProcessBuilder(tokens);
//         builder.directory(currentDirectory.toFile());
//         if (redirectOutput == null) {
//             builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//         } else if (appendOutput) {
//             builder.redirectOutput(ProcessBuilder.Redirect.appendTo(redirectOutput.toFile()));
//         } else {
//             builder.redirectOutput(redirectOutput.toFile());
//         }
//         if (redirectError == null) {
//             builder.redirectError(ProcessBuilder.Redirect.INHERIT);
//         } else if (appendError) {
//             builder.redirectError(ProcessBuilder.Redirect.appendTo(redirectError.toFile()));
//         } else {
//             builder.redirectError(redirectError.toFile());
//         }
//         Process process = builder.start();
//         process.waitFor();
//     }

//     private static List<String> parseTokens(String input) {
//         List<String> tokens = new ArrayList<>();
//         StringBuilder current = new StringBuilder();
//         boolean inSingleQuote = false;
//         boolean inDoubleQuote = false;
//         boolean tokenStarted = false;
//         boolean escaping = false;

//         for (int i = 0; i < input.length(); i++) {
//             char c = input.charAt(i);
//             if (escaping) {
//                 current.append(c);
//                 tokenStarted = true;
//                 escaping = false;
//             } else if (inSingleQuote) {
//                 if (c == '\'') {
//                     inSingleQuote = false;
//                 } else {
//                     current.append(c);
//                 }
//                 tokenStarted = true;
//             } else if (inDoubleQuote) {
//                 if (c == '"') {
//                     inDoubleQuote = false;
//                 } else if (c == '\\' && i + 1 < input.length()) {
//                     char next = input.charAt(i + 1);
//                     if (next == '"' || next == '\\') {
//                         current.append(next);
//                         i++;
//                     } else {
//                         current.append(c);
//                     }
//                 } else {
//                     current.append(c);
//                 }
//                 tokenStarted = true;
//             } else if (c == '\\') {
//                 escaping = true;
//                 tokenStarted = true;
//             } else if (c == '\'') {
//                 inSingleQuote = true;
//                 tokenStarted = true;
//             } else if (c == '"') {
//                 inDoubleQuote = true;
//                 tokenStarted = true;
//             } else if (Character.isWhitespace(c)) {
//                 if (tokenStarted) {
//                     tokens.add(current.toString());
//                     current.setLength(0);
//                     tokenStarted = false;
//                 }
//             } else {
//                 current.append(c);
//                 tokenStarted = true;
//             }
//         }

//         if (escaping) {
//             current.append('\\');
//             tokenStarted = true;
//         }

//         if (tokenStarted) {
//             tokens.add(current.toString());
//         }

//         return tokens;
//     }

//     private static String findExecutableInPath(String command) {
//         if (command.isEmpty()) {
//             return null;
//         }

//         String pathEnv = System.getenv("PATH");
//         if (pathEnv == null || pathEnv.isEmpty()) {
//             return null;
//         }

//         String[] directories = pathEnv.split(File.pathSeparator);
//         for (String directory : directories) {
//             if (directory.isEmpty()) {
//                 continue;
//             }

//             Path candidate = Path.of(directory, command);
//             if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
//                 return candidate.toString();
//             }
//         }

//         return null;
//     }
// }


import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    private static final String HOME = "~";
    private static final String PATH = "PATH";
    private static Path pwd = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();

        DefaultParser parser = new DefaultParser();
        parser.setEscapeChars(new char[0]);

        StringsCompleter stringsCompleter = new StringsCompleter("echo", "exit");

        LineReader lineReader =
                LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(stringsCompleter)
                        .parser(parser)
                        .build();

        String prompt = "$ ";

        while (true) {
            String line = lineReader.readLine(prompt);
            if (line != null && !line.isEmpty()) {
                Command command = parse(line);
                run(command);
            }
        }
    }

    enum CommandName {
        exit,
        echo,
        type,
        pwd,
        cd;

        static CommandName of(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    static class Command {
        final String command;
        final String[] args;
        final String[] commandWithArgs;
        final RedirectType redirectType;
        final String redirectTo;

        Command(String command,
                String[] args,
                String[] commandWithArgs,
                RedirectType redirectType,
                String redirectTo) {
            this.command = command;
            this.args = args;
            this.commandWithArgs = commandWithArgs;
            this.redirectType = redirectType;
            this.redirectTo = redirectTo;
        }
    }

    static class Redirect {
        final RedirectType redirectType;
        final int redirectAt;

        Redirect(RedirectType redirectType, int redirectAt) {
            this.redirectType = redirectType;
            this.redirectAt = redirectAt;
        }
    }

    private enum RedirectType {
        stdout,
        stderr,
        stdout_append,
        stderr_append
    }

    private enum QuteMode {
        singleQuote,
        doubleQuote
    }

    private static Command parse(String command) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("command cannot be null or empty");
        }

        List<String> split = splitCommand(command);
        if (split.isEmpty()) {
            throw new IllegalArgumentException("command cannot be empty");
        }

        String[] splitArray = split.toArray(new String[0]);

        if (splitArray.length == 1) {
            return new Command(split.get(0), new String[0], splitArray, null, "");
        }

        Redirect redirect = getRedirect(splitArray);
        int redirectAt = redirect.redirectAt;
        String[] args = Arrays.copyOfRange(splitArray, 1, redirectAt);
        String[] commandWithArgs = Arrays.copyOf(splitArray, redirectAt);
        String redirectTo = redirect.redirectType != null ? splitArray[redirectAt + 1] : "";

        return new Command(split.get(0), args, commandWithArgs, redirect.redirectType, redirectTo);
    }

    private static Redirect getRedirect(String[] split) {
        int redirectAt = split.length;
        RedirectType type = null;
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.equals(">") || s.equals("1>")) {
                redirectAt = i;
                type = RedirectType.stdout;
                break;
            }
            if (s.equals("2>")) {
                redirectAt = i;
                type = RedirectType.stderr;
                break;
            }
            if (s.equals(">>") || s.equals("1>>")) {
                redirectAt = i;
                type = RedirectType.stdout_append;
                break;
            }
            if (s.equals("2>>")) {
                redirectAt = i;
                type = RedirectType.stderr_append;
                break;
            }
        }
        return new Redirect(type, redirectAt);
    }

    private static List<String> splitCommand(String command) {
        List<String> result = new ArrayList<String>();
        StringBuilder temp = new StringBuilder();
        QuteMode quteMode = null;
        boolean escape = false;

        for (char ch : command.toCharArray()) {
            if (quteMode == QuteMode.singleQuote) {
                if (ch == '\'') {
                    quteMode = null;
                } else {
                    temp.append(ch);
                }
            } else if (quteMode == QuteMode.doubleQuote) {
                if (escape) {
                    if (ch != '"' && ch != '\\' && ch != '$' && ch != '`') {
                        temp.append('\\');
                    }
                    temp.append(ch);
                    escape = false;
                } else {
                    if (ch == '"') {
                        quteMode = null;
                    } else if (ch == '\\') {
                        escape = true;
                    } else {
                        temp.append(ch);
                    }
                }
            } else {
                if (escape) {
                    temp.append(ch);
                    escape = false;
                } else {
                    if (ch == '\'') {
                        quteMode = QuteMode.singleQuote;
                    } else if (ch == '"') {
                        quteMode = QuteMode.doubleQuote;
                    } else if (ch == ' ') {
                        addTemp(result, temp);
                    } else if (ch == '\\') {
                        escape = true;
                    } else {
                        temp.append(ch);
                    }
                }
            }
        }

        if (quteMode != null) {
            throw new IllegalArgumentException("Unclosed quote.");
        }

        addTemp(result, temp);

        return result;
    }

    private static void addTemp(List<String> result, StringBuilder temp) {
        if (temp.length() > 0) {
            result.add(temp.toString());
            temp.setLength(0);
        }
    }

    private static void run(Command command) throws IOException, InterruptedException {
        CommandName commandName = CommandName.of(command.command);

        if (Objects.isNull(commandName)) {
            runNotBuiltin(command);
            return;
        }

        switch (commandName) {
            case exit:
                int status = 0;
                if (command.args.length != 0) {
                    status = Integer.parseInt(command.args[0]);
                }
                System.exit(status);
                break;
            case echo:
                runEcho(command);
                break;
            case type:
                runType(command);
                break;
            case pwd:
                System.out.println(pwd);
                break;
            case cd:
                runCd(command);
                break;
        }
    }

    private static void runEcho(Command command) throws IOException {
        String message = String.join(" ", command.args);
        if (command.redirectType != null) {
            Path path = Paths.get(command.redirectTo);
            switch (command.redirectType) {
                case stdout:
                    byte[] bytes = String.format("%s%n", message).getBytes();
                    Files.write(
                            path,
                            bytes,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                    break;
                case stderr:
                    Files.write(
                            path,
                            new byte[0],
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                    System.out.println(message);
                    break;
                case stdout_append:
                    byte[] bytes2 = String.format("%s%n", message).getBytes();
                    Files.write(path, bytes2, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    break;
                case stderr_append:
                    Files.write(
                            path,
                            new byte[0],
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                    System.out.println(message);
                    break;
            }
        } else {
            System.out.println(message);
        }
    }

    private static void runCd(Command command) {
        if (command.args.length == 0) {
            return;
        }
        String targetPath = command.args[0];
        String separator = System.getProperty("file.separator");
        if (targetPath.equals(HOME) || targetPath.startsWith(HOME + separator)) {
            String homeDir = System.getenv("HOME");
            targetPath = targetPath.replaceFirst(HOME, homeDir);
        }

        Path newPath = pwd.resolve(targetPath).normalize();
        if (!Files.isDirectory(newPath)) {
            String error = String.format("cd: %s: No such file or directory", newPath);
            System.out.println(error);
        } else {
            pwd = newPath;
        }
    }

    private static void runNotBuiltin(Command command) throws IOException, InterruptedException {
        String executable = findExecutable(command.command);
        if (executable != null) {
            ProcessBuilder processBuilder = new ProcessBuilder(command.commandWithArgs);
            RedirectType redirectType = command.redirectType;
            if (redirectType != null) {
                java.io.File file = Paths.get(command.redirectTo).toFile();
                switch (redirectType) {
                    case stdout:
                        processBuilder.redirectOutput(file);
                        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                        break;
                    case stderr:
                        processBuilder.redirectError(file);
                        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                        break;
                    case stdout_append:
                        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(file));
                        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                        break;
                    case stderr_append:
                        processBuilder.redirectError(ProcessBuilder.Redirect.appendTo(file));
                        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                        break;
                }
            } else {
                processBuilder.inheritIO();
            }
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
            }
        } else {
            String error = String.format("%s: command not found", command.command);
            System.out.println(error);
        }
    }

    private static void runType(Command command) {
        if (command.args.length == 0) {
            System.out.println("type command requires an argument");
            return;
        }
        String arg0 = command.args[0];
        CommandName toType = CommandName.of(arg0);
        if (toType == null) {
            String executable = findExecutable(arg0);
            if (executable != null) {
                String message = String.format("%s is %s", arg0, executable);
                System.out.println(message);
            } else {
                String error = String.format("%s: not found", arg0);
                System.out.println(error);
            }
        } else {
            String message = String.format("%s is a shell builtin", toType);
            System.out.println(message);
        }
    }

    private static String findExecutable(String commandName) {
        String pathEnv = System.getenv(PATH);
        if (pathEnv == null) {
            return null;
        }
        String[] directories = pathEnv.split(System.getProperty("path.separator"));

        for (String dir : directories) {
            Path filePath = Paths.get(dir, commandName);
            if (Files.isExecutable(filePath)) {
                return filePath.toAbsolutePath().toString();
            }
        }

        return null;
    }
}