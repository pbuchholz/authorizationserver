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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalClientId == null) ? 0 : externalClientId.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((registeredRedirectUrls == null) ? 0 : registeredRedirectUrls.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (externalClientId == null) {
			if (other.externalClientId != null)
				return false;
		} else if (!externalClientId.equals(other.externalClientId))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (registeredRedirectUrls == null) {
			if (other.registeredRedirectUrls != null)
				return false;
		} else if (!registeredRedirectUrls.equals(other.registeredRedirectUrls))
			return false;
		return true;
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

		private Builder() {
			this.current = new Client();
		}

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
