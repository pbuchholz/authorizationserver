package authorizationserver.dataaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Test;

import authorizationserver.dataaccess.jdbc.JdbcClientDataAccessObject;
import authorizationserver.dataaccess.jdbc.JdbcClientDataAccessObject.Columns;
import authorizationserver.model.Client;

/**
 * Contains tests for {@link JdbcClientDataAccessObject}.
 * 
 * @author Philipp Buchholz
 */
public class JdbcClientDataAccessObjectTest {

	/**
	 * Prepares the DataSourceLookup and JDBC objects for the passed in
	 * {@link Client}.
	 * 
	 * @param client The client to mock data access for.
	 * @throws NamingException
	 * @throws SQLException
	 */
	public DataSource mockDataSource(Client client) throws NamingException, SQLException {
		DataSource dataSource = mock(DataSource.class);
		Connection connection = mock(Connection.class);

		when(dataSource.getConnection()).thenAnswer(im -> connection);
		when(connection.prepareStatement(any(String.class))).thenAnswer(im -> {

			PreparedStatement ps = mock(PreparedStatement.class);
			when(ps.executeQuery()).thenAnswer((invocationMock) -> {
				ResultSet rs = mock(ResultSet.class);
				when(rs.getLong(Columns.ID.jdbcColumn().getName())) //
						.thenAnswer(iom -> client.getId());
				when(rs.getString(Columns.NAME.jdbcColumn().getName()))//
						.thenAnswer(iom -> client.getName());
				when(rs.getString(Columns.EXTERNALCLIENTID.jdbcColumn().getName())) //
						.thenAnswer(iom -> client.getExternalClientId().toString());

				// Cursor before first record.
				when(rs.first()).thenReturn(true);

				when(rs.next()).thenAnswer(iom -> {
					return client.getRegisteredRedirectUrls().size() >= this.invocationCount("next", rs);
				});

				// TODO verify next calls.
				when(rs.getString(Columns.REDIRECTURL.jdbcColumn().getName())) //
						.thenAnswer(iom -> {
							return client.getRegisteredRedirectUrls().get(this.invocationCount("next", rs) - 1)
									.toString();
						});

				return rs;
			});
			return ps;
		});
		return dataSource;
	}

	private int invocationCount(String methodName, Object mock) {
		return Long.valueOf(mockingDetails(mock).getInvocations().stream() //
				.filter(i -> i.getMethod().getName().equals(methodName)) //
				.count()) //
				.intValue();
	}

	@Test
	public void testFindById() throws MalformedURLException, NamingException, SQLException {

		Client client = Client.builder() //
				.id(1L) //
				.name("") //
				.externalClientId(UUID.randomUUID()) //
				.url(new URL("http://protected.resource")) //
				.url(new URL("http://protected.profile")) //
				.build();

		DataAccessObject<Client, Long> clientDao = new JdbcClientDataAccessObject(this.mockDataSource(client));
		Client foundClient = clientDao.findOneByIdentifier(1L);

		assertNotNull("Client could not be found by id.", foundClient);
		assertEquals("Queried client is not equal to built client.", client, foundClient);
	}

}
