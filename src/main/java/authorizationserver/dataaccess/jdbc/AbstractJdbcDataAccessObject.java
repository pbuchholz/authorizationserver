package authorizationserver.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

import javax.sql.DataSource;

import authorizationserver.dataaccess.DataAccessCriteria;
import authorizationserver.dataaccess.DataAccessCriteria.CriteriaLink;
import authorizationserver.dataaccess.DataAccessObject;
import authorizationserver.dataaccess.Interpreters;

public abstract class AbstractJdbcDataAccessObject<T, I> implements DataAccessObject<T, I> {

	protected DataSource dataSource;

	protected final static String COLUMN_SEPARATOR = ",";

	public AbstractJdbcDataAccessObject(DataSource dataSource) {
		this.dataSource = dataSource;
	}

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
		try (Connection connection = dataSource.getConnection(); //
				PreparedStatement ps = connection.prepareStatement(statement)) {
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.first()) {
				return null;
			}
			return resultSet;
		} catch (SQLException e) {
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
	 * Provides the {@link Column} used to identify the object.
	 * 
	 * @return
	 */
	protected abstract Column provideIdentityColumn();

	/**
	 * Maps the current row of the passed in ResultSet to an instance of type T.
	 * 
	 * @param resultSet
	 * @return
	 */
	protected abstract T mapRow(ResultSet resultSet);

	@Override
	public T findOneByIdentifier(I id) {
		assert Objects.nonNull(id) : "Id cannot be null.";

		Column identityColumn = this.provideIdentityColumn();

		StringBuilder baseFindStatement = this.buildBaseFindStatement();
		this.preFind(baseFindStatement);
		this.appendIdentityWhere(baseFindStatement, identityColumn);

		try (Connection connection = dataSource.getConnection(); //
				PreparedStatement ps = connection.prepareStatement(baseFindStatement.toString())) {
			this.setIdentityValue(ps, identityColumn, id);
			ResultSet resultSet = ps.executeQuery();

			if (!resultSet.first()) {
				return null;
			}

			return this.mapRow(resultSet);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void appendIdentityWhere(StringBuilder baseFindStatement, Column identityColumn) {
		baseFindStatement.append(" WHERE ") //
				.append(identityColumn.getName()) //
				.append(" = ?");
	}

	/**
	 * Called prior to execution of the find statement given.
	 */
	protected abstract void preFind(StringBuilder findStatement);

	/**
	 * Overwritten to provide the identity value in {@link PreparedStatement}s.
	 * 
	 * @param preparedStatement
	 */
	protected abstract void setIdentityValue(PreparedStatement preparedStatement, Column identityColumn,
			I identityValue);

	protected StringBuilder buildBaseFindStatement() {
		StringBuilder baseFindStatement = new StringBuilder();
		baseFindStatement.append("SELECT ");

		this.provideColumns().forEach(c -> baseFindStatement //
				.append(c.getName())//
				.append(COLUMN_SEPARATOR)//
				.append(" "));
		baseFindStatement.delete(baseFindStatement.length() - 2, baseFindStatement.length());
		baseFindStatement.append(" FROM ") //
				.append(this.provideTableName());
		return baseFindStatement;
	}

}
