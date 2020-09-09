package authorizationserver.code;

import authorizationserver.model.AuthorizationCode;

public class AuthenticationCacheEntry {

	private String hash;
	private AuthorizationCode authorizationCode;

	public AuthorizationCode getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(AuthorizationCode authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash =hash;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private AuthenticationCacheEntry authenticationCacheEntry = new AuthenticationCacheEntry();

		public Builder hash(String identifier) {
			this.authenticationCacheEntry.hash = identifier;
			return this;
		}

		public Builder authorizationCode(AuthorizationCode authorizationCode) {
			this.authenticationCacheEntry.authorizationCode = authorizationCode;
			return this;
		}

		public AuthenticationCacheEntry build() {
			return this.authenticationCacheEntry;
		}

	}

}
