package test.nfa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Scanner;

import nfa.*;
import nfa.generators.*;

import org.junit.Test;

public class NFATest {

	@Test
	public void testAccepts() throws IOException {
		Scanner sc = new Scanner(System.in);
		AbstractNFAGenerator gen = new RegexMethod();
		NFA m = gen.generate(10, 0.4, "actg");
		while(true) {
//			System.out.println("your regex is " + regex);
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
