package authorizationserver.code;

import java.time.Duration;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

import authorizationserver.Configuration;
import authorizationserver.model.AuthorizationCode;

/**
 * Implements of {@link ExpiryPolicy} which is used to cache
 * {@link AuthenticationCacheEntry}s.
 * 
 * @author Philipp Buchholz
 */
public class AuthorizationCodeExpiry implements ExpiryPolicy<String, AuthorizationCode> {

	@Override
	public Duration getExpiryForCreation(String key, AuthorizationCode value) {
		return Configuration.INSTANCE.getAuthorizationCodeExpiration();
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends AuthorizationCode> value) {
		return null;
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends AuthorizationCode> oldValue,
			AuthorizationCode newValue) {
		return null;
	}

}
