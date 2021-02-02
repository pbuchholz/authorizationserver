package authorizationserver.code;

import authorizationserver.FlowExecutionException;
import authorizationserver.FlowStep;
import authorizationserver.RandomStringGenerator;
import authorizationserver.model.AuthorizationCode;

/**
 * {@link FlowStep} which creates an new {@link AuthorizationCode} and
 * associates it with the passed in {@link AuthenticationCacheEntry}.
 * 
 * @author Philipp Buchholz
 */
public class AuthorizationCodeFlowStep implements FlowStep<AuthorizationCode, AuthenticationCacheEntry> {

	@Override
	public AuthorizationCode execute(AuthenticationCacheEntry input) throws FlowExecutionException {

		assert null != input : "AuthenticationCacheEntry cannot be null in GenerateAndAssociateAuthorizationCode.";

		AuthorizationCode authorizationCode = new AuthorizationCode();
		authorizationCode.setValue(RandomStringGenerator.INSTANCE.generate());
		input.setAuthorizationCode(authorizationCode);
		return authorizationCode;
	}

}
