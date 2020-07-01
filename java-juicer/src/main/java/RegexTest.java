import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		
		String test = "This is a sentence.";
		
		//Not working \ has a special meaning
		String result = test.replaceAll("s", "\\ ");
		System.out.println(result);
		
		//Working
		String replacement = Matcher.quoteReplacement("\\ ");
		result = Pattern.compile("s").matcher(test).replaceAll(replacement);
		System.out.println(result);
		
		//Here the escaping is working too.
		System.out.println("Next Test \\ with escape");
	}

}
