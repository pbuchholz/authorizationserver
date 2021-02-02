package authorizationserver;

import java.util.UUID;

/**
 * Thrown in case of an invalid client calling out API.
 * 
 * @author Philipp Buchholz
 */
public class CouldNotVerifyClientException extends Exception {

	private static final long serialVersionUID = 6330196500154045354L;
	private UUID externalClientId;

	public UUID getExternalClientId() {
		return this.externalClientId;
	}

	public CouldNotVerifyClientException() {
		/* Default constructor in case the client id has not been supplied. */
	}

	public CouldNotVerifyClientException(UUID externalClientId) {
		this.externalClientId = externalClientId;
	}

}
