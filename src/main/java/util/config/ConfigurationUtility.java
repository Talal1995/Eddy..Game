package util.config;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Reads a configuration file and exposes its content to the Java application.
 * It is intended to be used as a Singleton for performance reasons. Should a
 * need for different configuration files arise, the best course of action is
 * to extend the class, expose its protected constructors and reimplement the
 * getInstance() method to something else.
 * 
 * @author Johan Holmberg, Malmö University
 */
public class ConfigurationUtility {
	
//	final static Logger logger = LoggerFactory.getLogger(ConfigurationUtility.class);
	private JsonObject config;
	
	/**
	 * Creates an instance of ConfigurationReader.
	 * 
	 * @param location The URL of a configuration file. This could be a local
	 * 		file, as well as a remote HTTP resource.
	 * @param localResource Is the config file in the project's resources folder?
	 * @throws MissingConfigurationException if the configuration file wasn't
	 * 		found. 
	 */
	public ConfigurationUtility(String location, boolean localResource)
			throws MissingConfigurationException {
		openConfig(location, localResource);
	}
	
	public ConfigurationUtility(ConfigurationUtility cUtil){
		JsonParser parser = new JsonParser();
		config = (JsonObject) parser.parse(cUtil.config.toString());
	}
	
	private void openConfig(String location, boolean localResource) throws MissingConfigurationException{
		Reader file = null;
		if(localResource){
			ClassLoader loader = ConfigurationUtility.class.getClassLoader();
			try {
				file = new FileReader(loader.getResource(location).getFile());
			} catch (FileNotFoundException e) {
				file = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(location)));
			}
			readFile(file);
		} else {
			URL url;
			try {
				if (location.startsWith("http://") ||
						location.startsWith("https://")) {
					url = new URL(location);
				} else {
					url = Paths.get(location).toUri().toURL();
				}
				file = new BufferedReader(new InputStreamReader(url.openStream()));
			} catch (MalformedURLException e) {
				throw new MissingConfigurationException();
			} catch (IOException e) {
//				logger.error("Couldn't read the configuration file: " + e.getMessage());
			}
			readFile(file);
		}
		
	}
	
	/**
	 * Returns the entire config tree as a JSON object.
	 * 
	 * @return The config tree.
	 */
	public JsonObject getTree() {
		return config;
	}
	
	/**
	 * Reads a configuration value and tries to parse it as a double.
	 * 
	 * @param path The configuration path, e.g. map.collector.path.
	 * @return A configuration value.
	 */
	public double getDouble(String path) {
		return traverse(path).getAsDouble();
	}
	
	/**
	 * Reads a configuration value and tries to parse it as an integer.
	 * 
	 * @param path The configuration path, e.g. map.collector.path.
	 * @return A configuration value.
	 */
	public int getInt(String path) {
		return traverse(path).getAsInt();
	}
	
	/**
	 * Reads a configuration value and tries to parse it as a string.
	 * 
	 * @param path The configuration path, e.g. map.collector.path.
	 * @return A configuration value.
	 */
	public String getString(String path) {
		return traverse(path).getAsString();
	}
	
	/**
	 * Reads a configuration value and tries to parse it as a boolean.
	 * 
	 * @param path The configuration path, e.g. map.collector.path.
	 * @return A configuration value.
	 */
	public boolean getBoolean(String path) {
		return traverse(path).getAsBoolean();
	}
	
	/**
	 * Updates a configuration value.
	 * 
	 * @param path The path of the configuration value.
	 * @param value The new value
	 */
	public void updateValue(String path, String value) {
		JsonObject parent = traverse(getParentOf(path)).getAsJsonObject();
		parent.remove(getPropertyName(path));
		parent.addProperty(getPropertyName(path), value);
	}
	
	/**
	 * Updates a configuration value.
	 * 
	 * @param path The path of the configuration value.
	 * @param value The new value
	 */
	public void updateValue(String path, Number value) {
		JsonObject parent = traverse(getParentOf(path)).getAsJsonObject();
		parent.addProperty(getPropertyName(path), value);
	}
	
	/**
	 * Updates a configuration value.
	 * 
	 * @param path The path of the configuration value.
	 * @param value The new value
	 */
	public void updateValue(String path, Boolean value) {
		JsonObject parent = traverse(getParentOf(path)).getAsJsonObject();
		parent.addProperty(getPropertyName(path), value);
	}
	
	/**
	 * Reads a configuration file.
	 * 
	 * @param file The configuration file.
	 */
	private void readFile(Reader file) {
		JsonParser parser = new JsonParser();
		config = (JsonObject) parser.parse(file);
	}
	
	/**
	 * Finds a JSON element based on a path by looking for the composite
	 * keys.
	 * 
	 * @param path A path, e.g. "game.profiles.default".
	 * @return A raw JSON element, which can then be accessed by this class.
	 */
	private JsonElement traverse(String path) {
		String[] elems = path.split("\\.");
		JsonObject o = config;
		
		if (elems.length > 1) {
			for (int i = 0; i < elems.length - 1; i++) {
				o = o.get(elems[i]).getAsJsonObject();
			}
		}
		
		return o.get(elems[elems.length - 1]);
	}
	
	/**
	 * Returns the first parent of a JSON node.
	 * 
	 * @param path The path of the JSON node.
	 * @return The JSON node's parent's path.
	 */
	private String getParentOf(String path) {
		int i = path.lastIndexOf('.');
		
		return path.substring(0, i);
	}
	
	/**
	 * Returns the property name of a JSON node.
	 * 
	 * @param path The path of the JSON node.
	 * @return The JSON node's property name.
	 */
	private String getPropertyName(String path) {
		int i = path.lastIndexOf('.');
		
		return path.substring(i + 1);
	}
}
