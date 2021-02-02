package authorizationserver.dataaccess;

import java.util.Iterator;
import java.util.Objects;

import authorizationserver.dataaccess.DataAccessCriteria.CriteriaLink;

/**
 * Represents a criteria used to describe which data should be accessed.
 * 
 * @author Philipp Buchholz
 */
public class DataAccessCriteria implements Iterable<CriteriaLink> {

	private String name;
	private Object value;
	private Operator operator;
	private CriteriaLink next;

	public static class CriteriaLink {
		private DataAccessCriteria next;
		private LogicalOperator logicalOperator;

		public CriteriaLink(DataAccessCriteria next, LogicalOperator logicalOperator) {
			this.next = next;
			this.logicalOperator = logicalOperator;
		}

		public DataAccessCriteria getNext() {
			return next;
		}

		public LogicalOperator getLogicalOperator() {
			return logicalOperator;
		}

	}

	public enum LogicalOperator {
		AND("AND");

		private String logicalOperator;

		public String logicalOperator() {
			return this.logicalOperator;
		}

		LogicalOperator(String logicalOperator) {
			this.logicalOperator = logicalOperator;
		}
	}

	public enum Operator {
		GT(">"), //
		LT("<"), //
		EQ("="), //
		GTOEQ(">="), //
		LTOEQ("<=");

		private String operator;

		public String operator() {
			return this.operator;
		}

		Operator(String operator) {
			this.operator = operator;
		}
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	public Operator getOperator() {
		return this.operator;
	}

	public static class Builder {
		private DataAccessCriteria current;
		private DataAccessCriteria first;

		public Builder start() {
			this.first = new DataAccessCriteria();
			this.current = this.first;
			return this;
		}

		public Builder name(String name) {
			this.current.name = name;
			return this;
		}

		public Builder value(Object value) {
			this.current.value = value;
			return this;
		}

		public Builder operator(Operator operator) {
			this.current.operator = operator;
			return this;
		}

		public Builder next(DataAccessCriteria next, LogicalOperator logicalOperator) {
			this.current.next = new CriteriaLink(next, logicalOperator);
			this.current = next;
			return this;
		}

		public DataAccessCriteria build() {
			return first;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public Iterator<CriteriaLink> iterator() {
		return new Iterator<CriteriaLink>() {

			private CriteriaLink current = DataAccessCriteria.this.next;

			@Override
			public boolean hasNext() {
				return Objects.nonNull(current.next);
			}

			@Override
			public CriteriaLink next() {
				this.current = DataAccessCriteria.this.next;
				return this.current;
			}

		};
	}

}
