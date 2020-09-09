package authorizationserver;

import java.security.SecureRandom;

public enum RandomStringGenerator {

	INSTANCE;

	private static final String CHARACTERS = "ABCDEFGJKLMNPRSTUVWXYZabcdefghijklmnopqrstuvwzyz0123456789";
	private static final int LNEGTH = 20;

	public String generate() {
		SecureRandom secureRandom = new SecureRandom();

		char[] charBuffer = new char[LNEGTH];
		for (int i = 0; i < LNEGTH; i++) {
			charBuffer[i] = CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length()));
		}
		return String.valueOf(charBuffer);
	}

}
