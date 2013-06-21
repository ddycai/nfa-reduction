package dcai.graph;

/**
 * Thrown when there are invalid operations the graph
 * @author duncan
 *
 */
public class GraphException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public GraphException(String msg) {
		super(msg);
	}
	
}
