package authorizationserver;

import java.util.Objects;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Provides {@link DataSource}s through JNDI lookup using the configured
 * {@link InitialContext}.
 * 
 * @author Philipp Buchholz
 *
 */
public class JndiDataSourceProvider implements DataSourceProvider {

	private static final String DATASOURCE_NAME = "jdbc/AuthorizationServerDataSource";
	private InitialContext initialContext;
	private DataSource dataSource;

	public JndiDataSourceProvider(InitialContext initialContext) {
		this.initialContext = initialContext;
	}

	private DataSource lookupDataSource() throws NamingException {
		initialContext = new InitialContext();
		return (DataSource) initialContext.lookup(DATASOURCE_NAME);
	}

	@Override
	public DataSource provideDataSource() {
		if (Objects.isNull(dataSource)) {
			try {
				dataSource = this.lookupDataSource();
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
			return this.dataSource;
		}
	}
}
