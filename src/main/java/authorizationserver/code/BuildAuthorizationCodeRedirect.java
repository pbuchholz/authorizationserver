package authorizationserver.code;

import authorizationserver.FlowExecutionException;
import authorizationserver.FlowStep;
import authorizationserver.RequestParameterExtractor.RequestParameters;
import authorizationserver.model.AuthorizationCode;

/**
 * {@link FlowStep} which builds the query string to redirect with the
 * {@link AuthorizationCode} passed in.
 * 
 * @author Philipp Buchholz
 */
public class BuildAuthorizationCodeRedirect implements FlowStep<String, AuthorizationCode> {

	@Override
	public String execute(AuthorizationCode input) throws FlowExecutionException {

		assert null != input : "AuthorizationCode in RedirectWithAuthorizationCodeFlowStep cannot be null.";

		return "?" + RequestParameters.CODE.parameterName() + //
				"=" + input.getValue();
	}

}
