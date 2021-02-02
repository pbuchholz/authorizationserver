package authorizationserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import authorizationserver.dataaccess.DataAccessCriteria;
import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.dataaccess.jdbc.JdbcClientDataAccessObject;
import authorizationserver.model.Client;

/**
 * Tests for {@link LoginEndpoint}.
 * 
 * @author Philipp Buchholz
 */
public class LoginEndpointTest {

	private HttpServletResponse mockResponse() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		return response;

	}

	private DataAccessObject<Client, Long> mockClientDao(Client client) {
		DataAccessObject<Client, Long> clientDao = mock(JdbcClientDataAccessObject.class);
		when(clientDao.findOneByDataAccessCriteria(any(DataAccessCriteria.class))).thenAnswer(im -> client);
		return clientDao;
	}

	private HttpServletRequest mockRequest(UUID externalClientId) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(RequestParameterExtractor.RequestParameters.CLIENT_ID.parameterName()))
				.then(im -> externalClientId.toString());
		when(request.getParameter(RequestParameterExtractor.RequestParameters.REDIRECT_URI.parameterName()))
				.then(im -> "https://protected.profile");
		when(request.getParameter(RequestParameterExtractor.RequestParameters.SCOPE.parameterName()))
				.then(im -> "read");
		return request;
	}

	@Test
	public void testLogin() throws ServletException, IOException {
		Client client = Client.builder() //
				.id(1L) //
				.externalClientId(UUID.randomUUID()) //
				.name("SinglePageApplication") //
				.url(new URL("https://proctected.profile")) //
				.build();

		LoginEndpoint loginEndpoint = new LoginEndpoint();
		loginEndpoint.setClientDao(this.mockClientDao(client));
		loginEndpoint.doPost(this.mockRequest(client.getExternalClientId()), this.mockResponse());

	}

}
