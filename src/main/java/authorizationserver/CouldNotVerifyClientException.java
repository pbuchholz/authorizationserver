package authorizationserver;

/**
 * Thrown in case of an invalid client calling out API.
 * 
 * @author Philipp Buchholz
 */
public class CouldNotVerifyClientException extends Exception {

	private static final long serialVersionUID = 6330196500154045354L;
	private long clientId;

	public long getClientId() {
		return this.clientId;
	}

	public CouldNotVerifyClientException() {
		/* Default constructor in case the client id has not been supplied. */
	}

	public CouldNotVerifyClientException(long clientId) {
		this.clientId = clientId;
	}

}
