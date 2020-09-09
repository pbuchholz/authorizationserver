package authorizationserver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import authorizationserver.code.AuthenticationCacheEntry;

/**
 * {@link Filter} which ensures the needed cache to hold
 * {@link AuthenticationCacheEntry}s.
 * 
 * @author Philipp Buchholz
 */
public class CacheFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/* Create and cache CacheAccess if needed. */
		ServletContext servletContext = request.getServletContext();
		if (null == servletContext.getAttribute(Configuration.AUTH_CACHE)) {
			CacheAccess cacheAccess = new CacheAccess();
			cacheAccess.init();
			servletContext.setAttribute(Configuration.AUTH_CACHE, cacheAccess);
		}

		/* Continue the filter chain. */
		chain.doFilter(request, response);
	}

}
