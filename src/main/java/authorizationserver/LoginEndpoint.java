package authorizationserver;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
import authorizationserver.model.Client;

/**
 * Represents the login endpoint used by clients to request access to a system
 * 
 * @author Philipp Buchholz
 */
@WebServlet("/public/api/login")
public class LoginEndpoint extends HttpServlet {

	private static final long serialVersionUID = -4110790971964508374L;

	private DataAccessObject<Client, Long> clientDao; // TODO set it from outside.

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			RequestParameterExtractor rpe = RequestParameterExtractor.from(request);

			/* Validate for registered incoming client. */
			long clientId = Long.parseLong(rpe.getClientId().orElseThrow(() -> new CouldNotVerifyClientException()));
			if (!verifyClient(clientId)) {
				throw new CouldNotVerifyClientException(clientId);
			}

			// authKey = this.authenticateRequest(request);
			this.authenticate(request);

			/* Create authentication cookie. */
			UUID authKey = UUID.randomUUID();

			CacheAccess cacheAccess = (CacheAccess) request.getServletContext().getAttribute(Configuration.AUTH_CACHE);
			// TODO: This cannot be correct since the ServletContet is global to the vm /
			// webb application.
			// request.getServletContext().setAttribute(Configuration.AUTH_KEY, authKey);

			cacheAccess.getAuthCache().put(authKey.toString(), AuthenticationCacheEntry.builder() //
					.build());
			response.addCookie(this.createAuthCookie(authKey));

			AuthorizationCodeFlow authorizationCodeFlow = new AuthorizationCodeFlow();
			authorizationCodeFlow.proceed(request, response);
		} catch (FlowExecutionException | CouldNotVerifyClientException e) {
			throw new ServletException(e);
		}
	}

	private boolean authenticate(HttpServletRequest request) throws CouldNotVerifyClientException {
		assert null != request : "Request cannot be null.";

		RequestParameterExtractor rpe = RequestParameterExtractor.from(request);

		if (verifyClient(rpe)) {
			return false;
		}

		/* Client must be registered. */
		return false;
	}

	/**
	 * Verifies the incomming {@link Client} and redirect url against the registered
	 * {@link Client}s.
	 * 
	 * @param rpe Helper to extract valuee from the request.
	 * @return
	 */
	private boolean verifyClient(long clientId) {
		// TODO Check if criteria query works!
		Client client = clientDao.findOneByDataAccessCriteria(DataAccessCriteria.builder() //
				.name("id") //
				.value(clientId) //
				.operator(Operator.EQ) //
				.next(DataAccessCriteria.builder() //
						.name("redirecturl") //
						.value("") //
						.operator(Operator.EQ) //
						.build(), LogicalOperator.AND)
				.build());

		if (Objects.isNull(client)) {
			/* Unknown client. */
			return false;
		}

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

	/**
	 * Finds an existing AuthCookie in the {@link HttpServletRequest}.
	 * 
	 * @param request
	 * @return
	 */
	private Optional<Cookie> findAuthCookie(HttpServletRequest request) {
		Stream.of(request.getCookies()) //
				.filter(c -> c.getName().equals(Configuration.AUTH_COOKIE)).findFirst();
	}

}
