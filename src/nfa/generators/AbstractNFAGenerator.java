package nfa.generators;

import java.util.Random;

import nfa.NFA;

/**
 * Interface for various methods of generating an NFA
 * @author duncan
 *
 */
public abstract class AbstractNFAGenerator {
	
	//for generating random numbers
	protected Random rand = new Random();
	
	/**
	 * Generates a new NFA with approximately (see below) n states and specified alphabet
	 * It is not always possible to randomly generate an NFA with exactly n states
	 * @param n
	 * @param alphabet
	 * @return
	 */
	public abstract NFA generate(int n, double density, String alphabet);
	
}
