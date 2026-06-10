import org.jline.reader.*;
public class test12 {
    public static void main(String[] args) {
        for (LineReader.Option opt : LineReader.Option.values()) {
            System.out.println(opt);
        }
    }
}
