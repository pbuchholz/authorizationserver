package authorizationserver;

import javax.sql.DataSource;

/**
 * Provides {@link DataSource}s to enable data access.
 * 
 * @author Philipp Buchholz
 */
public interface DataSourceProvider {

	DataSource provideDataSource();

}
