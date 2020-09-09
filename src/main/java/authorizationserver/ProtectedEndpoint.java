package authorizationserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link HttpServlet} endpoint which authenticates users.
 * 
 * @author Philipp Buchholz
 */
@WebServlet("/protected/api/auth")
public class ProtectedEndpoint extends HttpServlet {

	private static final long serialVersionUID = -2336869190724295523L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().write("Access to protected area granted.");
	}

}
