package nfa.generators;

import nfa.NFA;

/**
 * Interface for various methods of generating an NFA
 * @author duncan
 *
 */
public interface NFAGenerator {
	
	/**
	 * Generates a new NFA with approximately (see below) n states and specified alphabet
	 * It is not always possible to randomly generate an NFA with exactly n states
	 * @param n
	 * @param alphabet
	 * @return
	 */
	public NFA generate(int n, String alphabet);
	
}
