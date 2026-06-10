import org.jline.reader.*;
public class test10 {
    public static void main(String[] args) {
        LineReaderBuilder builder = LineReaderBuilder.builder();
        LineReader reader = builder.build();
        System.out.println("AUTO_LIST: " + reader.isSet(LineReader.Option.AUTO_LIST));
        System.out.println("AUTO_MENU: " + reader.isSet(LineReader.Option.AUTO_MENU));
        System.out.println("LIST_AMBIGUOUS: " + reader.isSet(LineReader.Option.LIST_AMBIGUOUS));
    }
}
