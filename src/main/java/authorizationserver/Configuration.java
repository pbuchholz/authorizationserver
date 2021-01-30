package authorizationserver;

import java.time.Duration;
import java.util.Properties;

/**
 * Defines the {@link Configuration} of the AuthorizationServer.
 * 
 * TODO: (PB) Abstract configuration to clear interface and provide implementations for static configuration (mostly used for tests) and for external configuratiton (on dev or prod)!
 * 
 * @author Philipp Buchholz
 */
public enum Configuration {

	INSTANCE;

	public static final String AUTH_KEY = "auth-key";
	public static final String AUTH_COOKIE = "auth-cookie";
	public static final String AUTH_CACHE = "auth-cache";

	private Duration authorizationCodeExpiration;
	private String loginEndpoint;
	private String protectedPath;
	private String publicPath;

	public void initialize(Properties properties) {
		// Authentication settings
		this.authorizationCodeExpiration = Duration
				.ofMinutes(Long.valueOf(properties.getProperty("AuthorizationCodeExpirationInMinutes", "10")));
		this.loginEndpoint = properties.getProperty("LoginEndpoint", "/login.html");
		this.protectedPath = properties.getProperty("ProtectedPath", "/protected");
		this.publicPath = properties.getProperty("PublicPath", "/public");
	}

	public Duration getAuthorizationCodeExpiration() {
		return this.authorizationCodeExpiration;
	}

	public String getLoginEndpoint() {
		return loginEndpoint;
	}

	public String getProtectedPath() {
		return this.protectedPath;
	}

	public String getPublicPath() {
		return this.publicPath;
	}

}
