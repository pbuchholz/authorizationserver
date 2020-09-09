package authorizationserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Looks up the {@link DataSource} configured for the AuthorizationServer and is
 * able to return {@link Connection}s from it.
 * 
 * @author Philipp Buchholz
 */
public enum DataSourceLookup {

	INSTANCE;

	private static final String DATASOURCE_NAME = "jdbc/AuthorizationServerDataSource";

	private InitialContext initialContext;
	private DataSource dataSource;

	public Connection getConnection() throws NamingException, SQLException {
		if (Objects.isNull(initialContext)) {
			this.lookupDataSource();
		}

		return dataSource.getConnection();
	}

	private void lookupDataSource() throws NamingException {
		initialContext = new InitialContext();
		dataSource = (DataSource) initialContext.lookup(DATASOURCE_NAME);
	}

}
