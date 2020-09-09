package authorizationserver.dataaccess.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.naming.NamingException;

import authorizationserver.DataSourceLookup;
import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.model.Client;

/**
 * Implementation of {@link DataAccessObject} which enables access to
 * {@link Client}s using {@link Long} identifiers.
 * 
 * @author Philipp Buchholz
 */
public class JdbcClientDataAccessObject extends AbstractJdbcDataAccessObject<Client, Long> {

	/**
	 * Describes the columns with its name, index to insert and index to select. If
	 * the index to insert is -1 the column is not inserted manually.
	 * 
	 * @author Philipp Buchohlz
	 */
	public enum Columns {
	ID("id", 1, -1), //
	EXTERNALCLIENTID("externalclientid", 2, 1), //
	NAME("name", 3, 2), //
	REDIRECTURL("redirecturl", 4, 3);

		private String column;
		private int selectIndex;
		private int insertIndex;

		Columns(String column, int selectIndex, int insertIndex) {
			this.column = column;
			this.selectIndex = selectIndex;
			this.insertIndex = insertIndex;
		}
	}

	@Override
	public Client findOneByIdentifier(Long id) {

		assert 0 == id : "Id for client search cannot be null.";

		try (Connection connection = DataSourceLookup.INSTANCE.getConnection(); //
				PreparedStatement ps = connection.prepareStatement("//
						+ "WHERE c.id = ?")) {

			ps.setLong(Columns.ID.selectIndex, id);
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.first()) {
				return null;
			}

			return this.map(resultSet);

		} catch (SQLException | NamingException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Client mapRow(ResultSet resultSet) {
		try {
			Client.Builder builder = Client.builder() //
					.id(resultSet.getLong(Columns.ID.column)) //
					.externalClientId(UUID.fromString(resultSet.getString(Columns.EXTERNALCLIENTID.column))) //
					.name(resultSet.getString(Columns.NAME.column));

			while (resultSet.next()) {
				builder.url(new URL(resultSet.getString(Columns.REDIRECTURL.column)));
			}

			return builder.build();
		} catch (SQLException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create(Client client) {
		try (Connection connection = DataSourceLookup.INSTANCE.getConnection()) {
			try (PreparedStatement insertClientPs = connection.prepareStatement("INSERT INTO clients (name) " //
					+ "VALUE(?)");
					PreparedStatement insertRedirectUrlsPs = connection
							.prepareStatement("INSERT INTO redirecturls (clientid, redirectUrl) " //
									+ "VALUES(?, ?)");) {
				/* First insert client. */
				insertClientPs.setString(Columns.NAME.insertIndex, client.getName());
				insertClientPs.executeUpdate();
				ResultSet resultSet = insertClientPs.getGeneratedKeys();

				if (!resultSet.first()) {
					throw new RuntimeException("ClientId has not been generated. Cannot insert Client.");
				}

				for (URL redirectUrl : client.getRegisteredRedirectUrls()) {
					insertRedirectUrlsPs.setLong(Columns.ID.insertIndex, resultSet.getLong(0));
					insertRedirectUrlsPs.setString(Columns.REDIRECTURL.insertIndex, redirectUrl.toString());
					insertRedirectUrlsPs.executeUpdate();
				}
			}
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void update(Client client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Client client) {
		// TODO Auto-generated method stub

	}

	@Override
	protected StringBuilder buildBaseFindStatement() {
		return new StringBuilder() //
				.append("SELECT id, externalid, name, redirecturl ") //
				.append("FROM clients c ") //
				.append("INNER JOIN redirecturls u ON c.id = u.clientid ");
	}

}
