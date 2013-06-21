package nfa;

import java.io.File;
import java.io.IOException;

public class NFAToDot {

	public static void main(String[] args) throws IOException {
		NFA M = new NFA(new File(args[0]));
		System.out.println(M.toDot());
	}
	
}
