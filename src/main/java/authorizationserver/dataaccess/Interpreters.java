package authorizationserver.dataaccess;

import authorizationserver.dataaccess.DataAccessCriteria.CriteriaLink;

public final class Interpreters {

	private Interpreters() {

	}

	public static DataAccessCriteriaInterpreter forDataAccessCriteria() {
		return new DataAccessCriteriaInterpreter();
	}

	public static CriteriaLinkInterpreter forCriteriaLink() {
		return new CriteriaLinkInterpreter();
	}

	public static class DataAccessCriteriaInterpreter implements Interpreter<DataAccessCriteria, StringBuilder> {

		@Override
		public StringBuilder interpret(DataAccessCriteria input) {
			return new StringBuilder() //
					.append(input.getName()) //
					.append(" ") //
					.append(input.getOperator().operator()) //
					.append(" ") //
					.append(String.valueOf(input.getValue()));
		}

	}

	public static class CriteriaLinkInterpreter implements Interpreter<CriteriaLink, StringBuilder> {

		@Override
		public StringBuilder interpret(CriteriaLink input) {
			return new StringBuilder() //
					.append(" ") //
					.append(input.getLogicalOperator().logicalOperator()) //
					.append(" ");
		}

	}

}
