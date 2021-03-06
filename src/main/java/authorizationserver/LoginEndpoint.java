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

	private DataAccessObject<Client, Long> clientDao;

	public void setClientDao(DataAccessObject<Client, Long> clientDao) {
		this.clientDao = clientDao;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			RequestParameterExtractor rpe = RequestParameterExtractor.from(request);

			/*
			 * Verify incoming client. The clientId represents the externalClientId
			 * internally.
			 */
			UUID externalClientId = UUID
					.fromString(rpe.getClientId().orElseThrow(() -> new CouldNotVerifyClientException()));
			if (!verifyClient(externalClientId)) {
				throw new CouldNotVerifyClientException(externalClientId);
			}

			// authKey = this.authenticateRequest(request);
//			this.authenticate(request); TODO: Check concept.
			Optional<Cookie> authCookie = this.findAuthCookie(request);
			if (authCookie.isEmpty()) {
				CacheAccess cacheAccess = (CacheAccess) request.getServletContext()
						.getAttribute(Configuration.AUTH_CACHE);

				/* Prepare new authentication. */
				UUID authKey = UUID.randomUUID();
				request.setAttribute(Configuration.AUTH_KEY, authKey);
				cacheAccess.getAuthCache().put(authKey.toString(), AuthenticationCacheEntry.builder() //
						.build());
				response.addCookie(this.createAuthCookie(authKey));

			} else {
				/* Check if authentication is still valid or needs renewal. */

			}

			AuthorizationCodeFlow authorizationCodeFlow = new AuthorizationCodeFlow();
			authorizationCodeFlow.proceed(request, response);
		} catch (FlowExecutionException | CouldNotVerifyClientException e) {
			throw new ServletException(e);
		}
	}
//
//	private boolean authenticate(HttpServletRequest request) throws CouldNotVerifyClientException {
//		assert null != request : "Request cannot be null.";
//
//		RequestParameterExtractor rpe = RequestParameterExtractor.from(request);
//
//		if (verifyClient(rpe)) {
//			return false;
//		}
//
//		/* Client must be registered. */
//		return false;
//	}

	/**
	 * Verifies the incomming {@link Client} and redirect url against the registered
	 * {@link Client}s.
	 * 
	 * @param rpe Helper to extract valuee from the request.
	 * @return
	 */
	private boolean verifyClient(UUID externalClientId) {
		// TODO Check if criteria query works!
		Client client = clientDao.findOneByDataAccessCriteria(DataAccessCriteria.builder() //
				.start()//
				.name("externalClientId") //
				.value(externalClientId) //
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
		return Stream.of(request.getCookies()) //
				.filter(c -> c.getName().equals(Configuration.AUTH_COOKIE)).findFirst();
	}

}
