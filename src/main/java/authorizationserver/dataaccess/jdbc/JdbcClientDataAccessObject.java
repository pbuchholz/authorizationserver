package authorizationserver.dataaccess.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

import javax.sql.DataSource;

import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.model.Client;

/**
 * Implementation of {@link DataAccessObject} which enables access to
 * {@link Client}s using {@link Long} identifiers.
 * 
 * @author Philipp Buchholz
 */
public class JdbcClientDataAccessObject extends AbstractJdbcDataAccessObject<Client, Long> {

	public JdbcClientDataAccessObject(DataSource dataSource) {
		super(dataSource);
	}

	private static final String TABLE_NAME = "Client";
	private static final String REDIRECT_URL_TABLE_NAME = "RedirectUrl";

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

		private Column jdbcColumn;

		public Column jdbcColumn() {
			return this.jdbcColumn;
		}

		Columns(Column column) {
			this.jdbcColumn = column;
		}
	}

	@Override
	protected Client mapRow(ResultSet resultSet) {
		try {
			Client.Builder builder = Client.builder() //
					.id(resultSet.getLong(Columns.ID.jdbcColumn.getName())) //
					.externalClientId(
							UUID.fromString(resultSet.getString(Columns.EXTERNALCLIENTID.jdbcColumn.getName()))) //
					.name(resultSet.getString(Columns.NAME.jdbcColumn.getName()));

			while (resultSet.next()) {
				builder.url(new URL(resultSet.getString(Columns.REDIRECTURL.jdbcColumn.getName())));
			}

			return builder.build();
		} catch (SQLException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create(Client client) {
		try (Connection connection = dataSource.getConnection()) {
			try (PreparedStatement insertClientPs = connection.prepareStatement("INSERT INTO clients (name) " //
					+ "VALUE(?)");
					PreparedStatement insertRedirectUrlsPs = connection
							.prepareStatement("INSERT INTO redirecturls (clientid, redirectUrl) " //
									+ "VALUES(?, ?)");) {
				/* First insert client. */
				insertClientPs.setString(Columns.NAME.jdbcColumn.getInsertIndex(), client.getName());
				insertClientPs.executeUpdate();
				ResultSet resultSet = insertClientPs.getGeneratedKeys();

				if (!resultSet.first()) {
					throw new RuntimeException("ClientId has not been generated. Cannot insert Client.");
				}

				for (URL redirectUrl : client.getRegisteredRedirectUrls()) {
					insertRedirectUrlsPs.setLong(Columns.ID.jdbcColumn.getInsertIndex(), resultSet.getLong(0));
					insertRedirectUrlsPs.setString(Columns.REDIRECTURL.jdbcColumn.getInsertIndex(),
							redirectUrl.toString());
					insertRedirectUrlsPs.executeUpdate();
				}
			}
		} catch (SQLException e) {
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
				.map(c -> c.jdbcColumn);
	}

	@Override
	protected String provideTableName() {
		return TABLE_NAME;
	}

	/**
	 * The RedirectUrls of a {@link Client} are joined from a separate table so we
	 * alter the base find statement here to acomplish this.
	 */
	@Override
	protected void preFind(StringBuilder findStatement) {
		findStatement.append(" INNER JOIN ") //
				.append(REDIRECT_URL_TABLE_NAME) //
				.append(" ON ") //
				.append(TABLE_NAME) //
				.append(".")  //
				.append(this.provideIdentityColumn().getName()) //
				.append(" = ") //
				.append(REDIRECT_URL_TABLE_NAME) //
				.append(".clientid");

	}

	@Override
	protected void setIdentityValue(PreparedStatement preparedStatement, Column identityColumn, Long identityValue) {
		try {
			preparedStatement.setLong(identityColumn.getSelectIndex(), identityValue);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Column provideIdentityColumn() {
		return Columns.ID.jdbcColumn;
	}

}
