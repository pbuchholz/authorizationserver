package authorizationserver.code;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import authorizationserver.CacheAccess;
import authorizationserver.Configuration;
import authorizationserver.FlowExecutionException;
import authorizationserver.RequestParameterExtractor;
import authorizationserver.RequestParameterExtractor.ResponseTypes;
import authorizationserver.model.AuthorizationCode;

/**
 * Represents the AuthorizationCodeFlow from OAuth2.
 * 
 * @author Philipp Buchholz
 */
public class AuthorizationCodeFlow {

	private RequestParameterExtractor requestParameterFactory = RequestParameterExtractor.INSTANCE;

	public void proceed(HttpServletRequest request, HttpServletResponse response) throws FlowExecutionException {
		try {
			ResponseTypes responseType = ResponseTypes
					.fromResponseType(requestParameterFactory.getResponseType(request).orElseThrow());

			ServletContext context = request.getServletContext();
			CacheAccess cache = (CacheAccess) context.getAttribute(Configuration.AUTH_CACHE);
			String authKey = String.valueOf(context.getAttribute(Configuration.AUTH_KEY));
			AuthenticationCacheEntry cacheEntry = cache.lookupByAuthKey(authKey);

			switch (responseType) {
			case CODE:

				/* Generate and associated new AuthorizationCode. */
				GenerateAndAssociateAuthorizationCode generateAndAssociateAuthorizationCode = new GenerateAndAssociateAuthorizationCode();
				AuthorizationCode authorizationCode = generateAndAssociateAuthorizationCode.execute(cacheEntry);

				String redirectUri = requestParameterFactory.getRedirectUri(request).orElseThrow();
				String authorizationCodeRedirect = new BuildAuthorizationCodeRedirect().execute(authorizationCode);
				response.sendRedirect(redirectUri + authorizationCodeRedirect);

				return;
			case TOKEN:
				/* Validate incoming AuthorizationCode and state. */

				/* Generate and return new AccessToken. */
				return;
			}
		} catch (Exception e) {
			throw new FlowExecutionException(e);
		}
	}

}
