package authorizationserver;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import authorizationserver.code.AuthenticationCacheEntry;
import authorizationserver.code.AuthorizationCodeFlow;
import authorizationserver.dataaccess.DataAccessCriteria;
import authorizationserver.dataaccess.DataAccessCriteria.LogicalOperator;
import authorizationserver.dataaccess.DataAccessCriteria.Operator;
import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.dataaccess.jdbc.JdbcClientDataAccessObject;
import authorizationserver.model.Client;

/**
 * Represents the login endpoint used by clients to request access to a system
 * 
 * @author Philipp Buchholz
 */
@WebServlet("/public/api/login")
public class LoginEndpoint extends HttpServlet {

	private static final long serialVersionUID = -4110790971964508374L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			// TODO this.authenticateRequest(request); A result of the sucessfull
			// authentication is the returned authkey.

			// Validate ClientId

			/* Create authentication cookie. */
			UUID authKey = UUID.randomUUID();

			CacheAccess cacheAccess = (CacheAccess) request.getServletContext().getAttribute(Configuration.AUTH_CACHE);
			request.getServletContext().setAttribute(Configuration.AUTH_KEY, authKey);
			cacheAccess.getAuthCache().put(authKey.toString(), AuthenticationCacheEntry.builder() //
					.build());
			response.addCookie(this.createAuthCookie(authKey));

			AuthorizationCodeFlow authorizationCodeFlow = new AuthorizationCodeFlow();
			authorizationCodeFlow.proceed(request, response);
		} catch (FlowExecutionException e) {
			throw new ServletException(e);
		}
	}

	private boolean authenticate(HttpServletRequest request) {
		assert null != request : "Request cannot be null.";

		RequestParameterExtractor extractor = RequestParameterExtractor.from(request);

		if (verifyClient(extractor)) {
			return false;
		}

		/* Client must be registered. */
		return false;

	}

	/**
	 * Verifies the incomming {@link Client} and redirect url against the registered
	 * {@link Client}s.
	 * 
	 * @param extractor
	 * @return
	 * @throws CouldNotVerfiyClientException
	 */
	private boolean verifyClient(RequestParameterExtractor extractor) throws CouldNotVerfiyClientException {
		Optional<String> clientId = extractor.getClientId();
		if (clientId.isEmpty()) {
			return false;
		}

		DataAccessObject<Client, Long> clientDao = new JdbcClientDataAccessObject();
		Client client = clientDao.findOneByDataAccessCriteria(DataAccessCriteria.builder() //
				.name("externalclientid") //
				.value(clientId.get()) //
				.operator(Operator.EQ) //
				.next(DataAccessCriteria.builder() //
						.name("redirecturl") //
						.value("") //
						.operator(Operator.EQ) //
						.build(), LogicalOperator.AND)
				.build());

		/* Could not verify client. */
		if (Objects.isNull(client)) {
			throw new CouldNotVerfiyClientException(clientId.get());
		}

		/* If client its registered with id and redirect url its valid. */
		return true;
	}

	/**
	 * Creates the {@link Cookie} which is used to identity if the authentication
	 * has already been successful.
	 * 
	 * @param authKey
	 * @return
	 */
	private Cookie createAuthCookie(UUID authKey) {
		Cookie authCookie = new Cookie(Configuration.AUTH_COOKIE, authKey.toString());
		authCookie.setDomain("");
		authCookie.setPath("/");
		return authCookie;
	}

}
