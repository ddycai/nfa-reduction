package nfa.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class RandomRegex {

	private Random rand = new Random();
	private String text;
	private double density;
	
	public RandomRegex(double density) {
		this.density = density;
	}
	
	/**
	 * Creates a regex generator from a file
	 * @param file	the file name
	 * @param density	the operator density
	 * @throws IOException
	 */
	public RandomRegex(File file, double density) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder contents = new StringBuilder();
		String line;
		while((line = br.readLine()) != null)
			contents.append(line);
		br.close();
		text = contents.toString();
		if(0 <= density && density <= 1)
			this.density = density;
		else
			throw new RuntimeException("Density must be between 0 and 1 (inclusive)");
	}
	
	/**
	 * Creates a regex generator from some text
	 * @param text	text to generate regex from
	 * @param density	the operator density
	 */
	public RandomRegex(String text, double density) {
		this.text = text;
		if(0 <= density && density <= 1)
			this.density = density;
		else
			throw new RuntimeException("Density must be between 0 and 1 (inclusive)");
	}
	
	/**
	 * Generates a regex from a random substring of the text of length n
	 * @param n	the size of substring to obtain from text
	 * @return randomly generated regex
	 */
	public String generate(int n) {
		
		if(n > text.length())
			throw new RuntimeException("Text sample is not long enough to generate a string of length " + n);
		
		int startIndex = rand.nextInt(text.length() - n);
		return generate(text.substring(startIndex, startIndex + n), '\0');
	}
	
	/**
	 * Generates a regex from a string
	 * @param s	the string to generate the regex
	 * @param parent the parent operator
	 * @return randomly generated regex
	 */
	public String generate(String s, char parent) {
		if(s.length() <= 1)
			return s;
		int l;
		if(Math.random() > density) {
			l = rand.nextInt(s.length() - 1) + 1;
			String e1 = generate(s.substring(0, l), '.');
			String e2 = generate(s.substring(l), '.');
			return e1 + e2;
		}
		boolean union = rand.nextInt(2) == 0;
		if(parent == '*' || union) {
			l = rand.nextInt(s.length()) + 1;
			String e1 = generate(s.substring(0, l), '|');
			String e2 = generate(s.substring(l), '|');
			if(e1.length() == 0 || e2.length() == 0)
				return String.format("(%s|%s)", e1, e2);
			else
				return String.format("%s|%s", e1, e2);
		} else {
			String e = generate(s, '*');
			if(e.charAt(e.length() - 1) != ')')
				return String.format("(%s)*", e);
			else
				return String.format("%s*", e);
		}
	}
	
	public static void main(String[] args) {
		
		try {
			RandomRegex rand = new RandomRegex(new File("data/ecoli.txt"), 0.4);
			String regex = rand.generate(30);
			System.out.println(regex);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
}
