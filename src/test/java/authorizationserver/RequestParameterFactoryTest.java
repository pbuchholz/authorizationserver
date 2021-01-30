package authorizationserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import authorizationserver.RequestParameterExtractor.RequestParameters;
import authorizationserver.RequestParameterExtractor.ResponseTypes;

/**
 * Contains tests for {@link RequestParameterExtractor}.
 * 
 * @author Philipp Buchholz
 */
public class RequestParameterFactoryTest {

	@Test
	public void shouldFindResponseCodeRequestParameter() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(RequestParameters.RESPONSE_TYPE.parameterName())) //
				.thenReturn(ResponseTypes.CODE.responseType());
		RequestParameterExtractor rpe = RequestParameterExtractor.from(request);

		Optional<String> parameter = rpe.getResponseType();

		assertNotNull("RequestParameter for ResponseType is null.", parameter);
		assertTrue("RequestParameter for ResponseType not available.", parameter.isPresent());
		assertEquals("RequestParameter for ResponseType has wrong value.", ResponseTypes.CODE.responseType(),
				parameter.get());
	}

}
