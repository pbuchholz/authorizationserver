package authorizationserver;

public interface FlowStep<O, I> {

	O execute(I input) throws FlowExecutionException;

}
