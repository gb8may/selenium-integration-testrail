package sample.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads properties from a property file 
 */
public class LoadProperties {
    private Properties props;
    private String filePath;
    
    
    /**
     * Loads the properties from the default property file: dados.properties
     */
    public LoadProperties(){
    	filePath = "./config/dados.properties";
    	props = new Properties();
    	loadFile(filePath);
    }
    
    /**
     * Loads the properties from a given property file
     * 
     * @param filePath the path where the properties file is
     */
    public LoadProperties(String filePath){
    	this.filePath = filePath;
    	props = new Properties();
    	loadFile(filePath);
    }
    
    private void loadFile(String filePath) {
    	FileInputStream file;
    	
    	try {
			file = new FileInputStream(filePath);
			props.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
	 * Returns the corresponding <strong>key value</strong>, obtained in
	 * properties file.
	 * 
	 * @param key
	 *            the key name in the properties file
	 * @return a string with the value of the property that corresponds to the
	 *         given key
	 */
    public String getValue(String key){  
    	return (String)props.getProperty(key);  
    } 
    
    public void saveValue(String key, String value){ 
    	File configFile = new File("./config/new.properties");
    	
    	try {
	        Properties props = new Properties();
	        props.setProperty(key, value);
	        FileWriter writer = new FileWriter(configFile);
	        props.store(writer, "host settings");
	        writer.close();
	    } catch (FileNotFoundException e) {
	        System.out.println("File does not exist: " + e.getMessage());
	    } catch (IOException e) {
	    	System.out.println("I/O Error: " + e.getMessage());
	    }
    }
}

