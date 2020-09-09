package authorizationserver.dataaccess;

/**
 * Provides access to objects of type T by using identifiers of type I.
 * 
 * @author Philipp Buchholz
 */
public interface DataAccessObject<T, I> {

	T findOneByIdentifier(I id);

	T findOneByDataAccessCriteria(DataAccessCriteria dataAccessCriteria);

	void create(T candidate);

	void update(T candidate);

	void delete(T candidate);

}
