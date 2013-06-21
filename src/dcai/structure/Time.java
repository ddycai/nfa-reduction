package dcai.structure;


public class Time {

	long start;
	long end;
	String msg = null;
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void start(String label) {
		msg = label;
		System.out.format("%s...\n", msg);
		start();
	}
	
	public void end() {
		end = System.currentTimeMillis();
		if(msg != null) {
			System.out.format("Done %s. (%s)\n", msg, toString());
			msg = null;
		} else
			print();
	}
	
	public void print() { System.out.println("Time: " + toString()); }
	
	public String toString() { return String.valueOf(end - start); }

}
