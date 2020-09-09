package authorizationserver;

import java.util.Optional;

import javax.servlet.ServletRequest;

/**
 * Represents the type of grant which has been requested from the autorization
 * server.
 * 
 * @author Philipp Buchholz
 */
public class RequestParameterExtractor {

	public enum RequestParameters {

		RESPONSE_TYPE("response_type"), //
		CLIENT_ID("client_id"), //
		REDIRECT_URI("redirect_uri"), //
		STATE("state"), //
		SCOPE("scope"), //
		CODE("code");

		private String parameterName;

		public String parameterName() {
			return this.parameterName;
		}

		private RequestParameters(String parameterName) {
			this.parameterName = parameterName;
		}

	}

	public enum ResponseTypes {
		CODE("code"), //
		TOKEN("token");

		private String responseType;

		public String responseType() {
			return this.responseType;
		}

		private ResponseTypes(String responseType) {
			this.responseType = responseType;
		}

		public static ResponseTypes fromResponseType(String responseType) {
			switch (responseType) {
			case "code":
				return ResponseTypes.CODE;
			case "token":
				return ResponseTypes.TOKEN;
			}
			throw new IllegalArgumentException();
		}

	}

	private RequestParameterExtractor() {

	}

	public static RequestParameterExtractor from(ServletRequest request) {
		RequestParameterExtractor factory = new RequestParameterExtractor();
		factory.request = request;
		return factory;
	}

	private ServletRequest request;

	public Optional<String> getResponseType() {
		return this.byNameFromRequest(RequestParameters.RESPONSE_TYPE.parameterName, request);
	}

	public Optional<String> getClientId() {
		return this.byNameFromRequest(RequestParameters.CLIENT_ID.parameterName, request);
	}

	public Optional<String> getRedirectUri() {
		return this.byNameFromRequest(RequestParameters.REDIRECT_URI.parameterName, request);
	}

	public Optional<String> getState() {
		return this.byNameFromRequest(RequestParameters.STATE.parameterName, request);
	}

	public Optional<String> getScope() {
		return this.byNameFromRequest(RequestParameters.SCOPE.parameterName, request);
	}

	public Optional<String> getCode() {
		return this.byNameFromRequest(RequestParameters.CODE.parameterName, request);
	}

	public Optional<String> byNameFromRequest(String parameterName, ServletRequest servletRequest) {
		if (isEmpty(parameterName, servletRequest)) {
			return Optional.empty();
		}
		String parameterValue = servletRequest.getParameter(parameterName);

		return Optional.of(parameterValue);
	}

	private boolean isEmpty(String parameterName, ServletRequest servletRequest) {
		return servletRequest.getParameter(parameterName) == null //
				|| servletRequest.getParameter(parameterName).equals("");
	}

}
