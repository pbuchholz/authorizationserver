package authorizationserver.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import javax.naming.NamingException;

import authorizationserver.DataSourceLookup;
import authorizationserver.StreamHelper;
import authorizationserver.dataaccess.DataAccessCriteria;
import authorizationserver.dataaccess.DataAccessCriteria.CriteriaLink;
import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.dataaccess.Interpreters;

public abstract class AbstractJdbcDataAccessObject<T, I> implements DataAccessObject<T, I> {

	protected final static String COLUMN_SEPARATOR = ",";

	@Override
	public T findOneByDataAccessCriteria(DataAccessCriteria dataAccessCriteria) {
		StringBuilder findStatement = this.buildBaseFindStatement() //
				.append(this.interpretDataAccessCriterias(dataAccessCriteria));

		ResultSet resultSet = this.executeQuery(findStatement.toString());
		if (Objects.isNull(resultSet)) {
			return null;
		}

		return this.mapRow(resultSet);
	}

	protected ResultSet executeQuery(String statement) {
		try (Connection connection = DataSourceLookup.INSTANCE.getConnection(); //
				PreparedStatement ps = connection.prepareStatement(statement)) {
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.first()) {
				return null;
			}
			return resultSet;
		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private StringBuilder interpretDataAccessCriterias(DataAccessCriteria dataAccessCriteria) {
		StringBuilder interpreted = Interpreters.forDataAccessCriteria().interpret(dataAccessCriteria);

		for (CriteriaLink link : dataAccessCriteria) {
			interpreted.append(Interpreters.forCriteriaLink().interpret(link)) //
					.append(Interpreters.forDataAccessCriteria().interpret(link.getNext()));
		}

		return interpreted;
	}

	/**
	 * Provides the name of the underlying database tabe.
	 * 
	 * @return
	 */
	protected abstract String provideTableName();

	/**
	 * Provides the {@link Stream} of {@link Column} to map.
	 * 
	 * @return
	 */
	protected abstract Stream<Column> provideColumns();

	/**
	 * Maps the current row of the passed in ResultSet to an instance of type T.
	 * 
	 * @param resultSet
	 * @return
	 */
	protected abstract T mapRow(ResultSet resultSet);

	protected StringBuilder buildBaseFindStatement() {
		StringBuilder baseFindStatement = new StringBuilder();
		baseFindStatement.append("SELECT ");

		Stream<Column> columnStream = this.provideColumns();

		Stream.concat( //
				columnStream.sorted(Comparator.comparingInt(c -> c.getSelectIndex())) //
						.skip(StreamHelper.last(columnStream)) //
						.map(c -> c.getName().concat(COLUMN_SEPARATOR)), //
				columnStream.filter(c -> c.getSelectIndex() == StreamHelper.last(columnStream)) //
						.map(Column::getName)) //
				.forEach(c -> baseFindStatement.append(c));

		baseFindStatement.append(" FROM ") //
				.append(this.provideTableName());
		return baseFindStatement;
	}

}
