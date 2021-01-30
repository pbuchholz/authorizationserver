package authorizationserver.dataaccess.jdbc;

/**
 * Represents a columns.
 * 
 * @author Philipp Buchholz
 */
public class Column {

	private String name;
	private int selectIndex;
	private int insertIndex;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSelectIndex() {
		return this.selectIndex;
	}

	public void settSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}

	public int getInsertIndex() {
		return this.insertIndex;
	}

	public void setInsertIndex(int insertIndex) {
		this.insertIndex = insertIndex;
	}

	public Column(String name, int selectIndex, int insertIndex) {
		this.name = name;
		this.selectIndex = selectIndex;
		this.insertIndex = insertIndex;
	}

}
