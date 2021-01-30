package authorizationserver;

import java.util.stream.Stream;

/**
 * Helper for {@link Stream}s.
 * 
 * @author Philipp Buchholz
 */
public class StreamHelper {

	public static <T> long last(Stream<T> stream) {
		return stream.count() - 1;
	}

}
