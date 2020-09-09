package authorizationserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import authorizationserver.RequestParameterExtractor.RequestParameters;

/**
 * {@link Filter} which ensures the current {@link ServletRequest} is
 * authenticated and the encrypted authentication {@link Cookie} has been set.
 * 
 * To encrypt authentication {@link Cookie}s a dynamic generated key is used.
 * This will invalidate all authentication {@link Cookie}s when the service is
 * restarted.
 * 
 * @author Philipp Buchholz
 */
public class AuthenticationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletResponse response = (HttpServletResponse) servletResponse;
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			ServletContext context = servletRequest.getServletContext();

			/* Only protected paths needs authentication. */
			if (!request.getServletPath().contains(Configuration.INSTANCE.getProtectedPath())) {
				chain.doFilter(request, response);
				return;
			}

			Optional<Cookie> authCookie = this.findAuthCookie(request);

			/* Redirect to login if not authenticated. */
			if (!authCookie.isPresent() //
					|| !this.knownAuthKey(request, context, authCookie.get())) {
				this.redirectToLogin(request, response);
				return;
			}

			/* User is authenticated to proceed. */
			context.setAttribute(Configuration.AUTH_KEY, authCookie.get().getValue());
			chain.doFilter(request, response);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
			throws IOException, UnsupportedEncodingException {
		RequestParameterExtractor rpf = RequestParameterExtractor.INSTANCE;

		String responseTypeParam = rpf.getResponseType(request).orElseThrow();
		String redirectUri = rpf.getRedirectUri(request).orElseThrow();
		String clientId = rpf.getClientId(request).orElseThrow();

		response.sendRedirect(request.getServletContext().getContextPath() + //
				Configuration.INSTANCE.getLoginEndpoint() + //
				"?" + RequestParameters.RESPONSE_TYPE.parameterName() + //
				"=" + responseTypeParam + //
				"&" + RequestParameters.REDIRECT_URI.parameterName() + //
				"=" + URLEncoder.encode(redirectUri, "UTF-8") + //
				"&" + RequestParameters.CLIENT_ID.parameterName() + //
				"=" + clientId);
	}

	private Optional<Cookie> findAuthCookie(HttpServletRequest request) {
		if (Objects.isNull(request.getCookies())) {
			return Optional.empty();
		}

		return Stream.of(request.getCookies()) //
				.filter((cookie) -> cookie.getName().equals(Configuration.AUTH_COOKIE)) //
				.findFirst();
	}

	private boolean knownAuthKey(HttpServletRequest request, ServletContext servletContext, Cookie authCookie)
			throws GeneralSecurityException {

		/* Create authentication cookie. */
		CacheAccess cacheAccess = (CacheAccess) servletContext.getAttribute(Configuration.AUTH_CACHE);

		return cacheAccess.getAuthCache().containsKey(authCookie.getValue());
	}

}
