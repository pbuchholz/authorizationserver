package authorizationserver;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.Status;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import authorizationserver.code.AuthenticationCacheEntry;

/**
 * Central object which manages the cache for the AuthorizationServer.
 * 
 * @author Philipp Buchholz
 */
public class CacheAccess {

	public CacheManager cacheManager;

	public void init() {
		this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder() //
				.withCache(Configuration.AUTH_CACHE, CacheConfigurationBuilder //
						.newCacheConfigurationBuilder(String.class, AuthenticationCacheEntry.class,
								ResourcePoolsBuilder.heap(200))) //
				.build();
		cacheManager.init();
	}

	public Cache<String, AuthenticationCacheEntry> getAuthCache() {
		assert null != cacheManager;
		assert cacheManager.getStatus() == Status.AVAILABLE;

		return this.cacheManager.getCache(Configuration.AUTH_CACHE, String.class, //
				AuthenticationCacheEntry.class);
	}

	public AuthenticationCacheEntry lookupByAuthKey(String authKey) {
		return this.getAuthCache().get(authKey);
	}

}
