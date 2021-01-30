package authorizationserver.dataaccess.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

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

	private static final String TABLE_NAME = "Client";

	/**
	 * Describes the columns with its name, index to insert and index to select. If
	 * the index to insert is -1 the column is not inserted manually.
	 * 
	 * @author Philipp Buchohlz
	 */
	public enum Columns {
		ID(new Column("id", 1, -1)), //
		EXTERNALCLIENTID(new Column("externalclientid", 2, 1)), //
		NAME(new Column("name", 3, 2)), //
		REDIRECTURL(new Column("redirecturl", 4, 3));

		private Column column;

		Columns(Column column) {
			this.column = column;
		}
	}

	@Override
	public Client findOneByIdentifier(Long id) {

		assert 0 == id : "Id for client search cannot be null.";

		try (Connection connection = DataSourceLookup.INSTANCE.getConnection(); //
				PreparedStatement ps = connection.prepareStatement(this.buildBaseFindStatement() //
						.append(" WHERE c.id = ?") //
						.toString())) {

			ps.setLong(Columns.ID.column.getSelectIndex(), id);
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.first()) {
				return null;
			}

			return this.mapRow(resultSet);

		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Client mapRow(ResultSet resultSet) {
		try {
			Client.Builder builder = Client.builder() //
					.id(resultSet.getLong(Columns.ID.column.getName())) //
					.externalClientId(UUID.fromString(resultSet.getString(Columns.EXTERNALCLIENTID.column.getName()))) //
					.name(resultSet.getString(Columns.NAME.column.getName()));

			while (resultSet.next()) {
				builder.url(new URL(resultSet.getString(Columns.REDIRECTURL.column.getName())));
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
				insertClientPs.setString(Columns.NAME.column.getInsertIndex(), client.getName());
				insertClientPs.executeUpdate();
				ResultSet resultSet = insertClientPs.getGeneratedKeys();

				if (!resultSet.first()) {
					throw new RuntimeException("ClientId has not been generated. Cannot insert Client.");
				}

				for (URL redirectUrl : client.getRegisteredRedirectUrls()) {
					insertRedirectUrlsPs.setLong(Columns.ID.column.getInsertIndex(), resultSet.getLong(0));
					insertRedirectUrlsPs.setString(Columns.REDIRECTURL.column.getInsertIndex(), redirectUrl.toString());
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
	protected Stream<Column> provideColumns() {
		return Stream.of(Columns.values()) //
				.map(c -> c.column);
	}

	@Override
	protected String provideTableName() {
		return TABLE_NAME;
	}

}
