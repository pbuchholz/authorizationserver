package authorizationserver.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the AuthorizationCode which is send to the client after sucessfull
 * login. A valid AuthorizationCode can be exchanged against an AccessToken. An
 * AuthorizationCode has restricted validity in a defined timespan.
 * 
 * @author Philipp Buchholz
 */
public class AuthorizationCode {

	private String value;
	private long createdMillis = System.currentTimeMillis();

	// True if has been exchanged against an AccessToken.
	private boolean exchanged;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getCreatedMillis() {
		return createdMillis;
	}

	public boolean getExchanged() {
		return this.exchanged;
	}

	public void setExchanged(boolean exchanged) {
		this.exchanged = exchanged;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other, false);
	}

}
