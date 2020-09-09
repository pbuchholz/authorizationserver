package authorizationserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ConfigurationInitializer implements ServletContextListener {

	private static final String APPLICATION_PROPERTIES = "application.properties";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String applicationProperties = sce.getServletContext().getInitParameter(APPLICATION_PROPERTIES);

		try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(applicationProperties)) {
			Properties properties = new Properties();
			properties.load(is);
			Configuration.INSTANCE.initialize(properties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
