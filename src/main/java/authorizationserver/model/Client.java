package authorizationserver.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents the registration of a client.
 * 
 * @author Philipp Buchholz
 */
public class Client {

	/* Internal id of the client. */
	private long id;

	/* Externally known id of the client. */
	private UUID externalClientId;

	private String name;
	private List<URL> registeredRedirectUrls;

	public long getId() {
		return this.id;
	}

	public UUID getExternalClientId() {
		return this.externalClientId;
	}

	public String getName() {
		return this.name;
	}

	public List<URL> getRegisteredRedirectUrls() {
		return this.registeredRedirectUrls;
	}

	private Client() {

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Client current;

		public Builder id(long id) {
			this.current.id = id;
			return this;
		}

		public Builder externalClientId(UUID externalClientId) {
			this.current.externalClientId = externalClientId;
			return this;
		}

		public Builder name(String name) {
			this.current.name = name;
			return this;
		}

		public Builder url(URL url) {
			if (Objects.isNull(this.current.registeredRedirectUrls)) {
				this.current.registeredRedirectUrls = new ArrayList<>();
			}
			this.current.registeredRedirectUrls.add(url);
			return this;
		}

		public Client build() {
			return this.current;
		}

	}

}
