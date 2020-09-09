package authorizationserver;

public class CouldNotVerfiyClientException extends Exception {

	private String externalClientId;

	public CouldNotVerfiyClientException(String externalClientId) {
		this.externalClientId = externalClientId;
	}

}
