package authorizationserver;

/**
 * {@link Exception} which is thrown if a FlowStep cannot be executed because of
 * an exception.
 * 
 * @author Philipp Buchholz
 */
public class FlowExecutionException extends Exception {

	private static final long serialVersionUID = 5100788754200641420L;

	public FlowExecutionException() {

	}

	public FlowExecutionException(Exception cause) {
		super(cause);
	}

}
