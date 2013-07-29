package nfa.generators;

import java.util.Random;

import nfa.NFA;

/**
 * Generates a random NFA from a random regular expression
 * @author duncan
 *
 */
public class RegexNFAGenerator implements NFAGenerator {

	private double density;
	private Random rand;
	
	/**
	 * @param operatorDensity the operator density of the regular expression
	 */
	public RegexNFAGenerator(double operatorDensity) {
		this.density = operatorDensity;
		rand = new Random();
	}
	
	/**
	 * Generates a random NFA from a random regex
	 * The random regex is generated from a string of length n and the specified alphabet
	 * The resulting NFA will have at least n states, bounded by O(n)
	 */
	public NFA generate(int n, String alphabet) {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < n; i++) {
			int index = rand.nextInt(alphabet.length());
			s.append(alphabet.charAt(index));
		}
		String regex = randomRegex(s.toString());
		return new NFA(regex, alphabet);
	}
	
	/**
	 * Generates a random regex from a random string using the algorithm by Ilie et al
	 * @param s	the string to generate the regex
	 * @return randomly generated regex
	 */
	public String randomRegex(String s) {
		return randomRegex(s, '.');
	}
	
	private String randomRegex(String s, char parent) {
		if(s.length() <= 1)
			return s;
		int l;
		if(Math.random() > density) {
			l = rand.nextInt(s.length() - 1) + 1;
			String e1 = randomRegex(s.substring(0, l), '.');
			String e2 = randomRegex(s.substring(l), '.');
			return e1 + e2;
		}
		boolean union = rand.nextInt(2) == 0;
		if(parent == '*' || union) {
			l = rand.nextInt(s.length()) + 1;
			String e1 = randomRegex(s.substring(0, l), '|');
			String e2 = randomRegex(s.substring(l), '|');
			if(e1.length() == 0 || e2.length() == 0)
				return String.format("(%s|%s)", e1, e2);
			else
				return String.format("%s|%s", e1, e2);
		} else {
			String e = randomRegex(s, '*');
			if(e.charAt(e.length() - 1) != ')')
				return String.format("(%s)*", e);
			else
				return String.format("%s*", e);
		}
	}
	
	public static void main(String[] args) {
		NFAGenerator rand = new RegexNFAGenerator(0.25);
		System.out.println(rand.generate(10, "actg"));
	}
	
}
