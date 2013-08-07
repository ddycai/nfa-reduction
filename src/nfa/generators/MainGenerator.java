package nfa.generators;

import nfa.NFA;

public class MainGenerator {

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Usage:		./generate [method] [size] [density] [alphabet]");
			System.out.println("method:		generation method, either partition, regex or spanning");
			System.out.println("size:		the size of the generated NFA");
			System.out.println("alphabet:	the alphabet");
			System.exit(0);
		}
		
		AbstractNFAGenerator generator;
		
		if(args[0].equalsIgnoreCase("partition")) {
			generator = new PartitionMethod();
		} else if(args[0].equalsIgnoreCase("regex")) {
			generator = new RegexMethod();
		} else {
			generator = new SpanningTreeMethod();
		}
		
		int len = Integer.parseInt(args[1]);
		double density = Double.parseDouble(args[2]);
		String alphabet = args[3];
		NFA m = generator.generate(len, density, alphabet);
		System.out.print(m.toString());
		
	}
	
}
