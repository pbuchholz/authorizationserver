package authorizationserver.dataaccess;

public interface Interpreter<I, O> {

	/**
	 * Interprets the input and returns the result as Type O.
	 * 
	 * @param input
	 * @return
	 */
	O interpret(I input);

}
