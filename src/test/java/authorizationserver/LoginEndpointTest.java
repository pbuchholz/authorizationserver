package authorizationserver;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class LoginEndpointTest {

	private HttpServletResponse mockResponse() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		return response;

	}

	private HttpServletRequest mockRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		return request;
	}

	@Test
	public void testLogin() throws ServletException, IOException {
		LoginEndpoint loginEndpoint = new LoginEndpoint();
		loginEndpoint.doPost(this.mockRequest(), this.mockResponse());

	}

}
