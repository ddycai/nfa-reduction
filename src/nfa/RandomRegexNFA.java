package nfa;

import java.io.File;
import java.io.IOException;

public class RandomRegexNFA {

	public static void main(String[] args) throws IOException {
		
		if(args.length != 2) {
			System.out.println("Args: length operator-density");
			System.exit(0);
		}
		
		int length = Integer.parseInt(args[0]);
		double density = Double.parseDouble(args[1]);
		
		RandomRegex rand = new RandomRegex(new File("data/ecoli.txt"), density);
		String regex = rand.generate(length);
		System.out.println("REGEX: ");
		System.out.println(regex);
		System.out.println();
		NFA M = new NFA(regex, "atcg");
		System.out.println("NFA: ");
		System.out.print(M.toString());
	}
	
}
