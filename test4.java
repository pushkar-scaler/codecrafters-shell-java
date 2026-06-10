import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

public class test4 {
    public static void main(String[] args) {
        new ArgumentCompleter(new StringsCompleter("echo"), NullCompleter.INSTANCE);
        System.out.println("OK");
    }
}
