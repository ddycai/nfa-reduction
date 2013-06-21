package test.nfa;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import nfa.*;

import org.junit.Test;

public class NFATest {

	@Test
	public void testAccepts() throws IOException {
		Scanner sc = new Scanner(System.in);
		RandomRegex rand = new RandomRegex(new File("ecoli.txt"), .4);
		String regex = rand.generate(10);
		NFA m = new NFA(regex, "atcg");
		while(true) {
			System.out.println("your regex is " + regex);
			String input = sc.nextLine();
			if(input.isEmpty()) {
				System.out.println("Goodbye.");
				break;
			}
			assertTrue(m.accepts(input));
			System.out.println("accepted.");
		}
		sc.close();
	}

}
