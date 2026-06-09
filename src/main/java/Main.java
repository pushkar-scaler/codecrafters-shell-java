import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        Set<String> builtins = Set.of("echo", "exit", "type");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.equals("exit") || input.equals("exit 0")) {
                break;
            } else if (input.startsWith("type ")) {
                String command = input.substring(5).trim();
                if (builtins.contains(command)) {
                    System.out.println(command + " is a shell builtin");
                } else {
                    String executablePath = findExecutableInPath(command);
                    if (executablePath != null) {
                        System.out.println(command + " is " + executablePath);
                    } else {
                        System.out.println(command + ": not found");
                    }
                }
            } else if (input.startsWith("echo ")) {
                System.out.println(input.substring(5));
            } else {
                System.out.println(input + ": command not found");
            }
        }
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
