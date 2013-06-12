package org.jenkinsci.plugins.appio;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jenkinsci.plugins.appio.service.ZipService;
import org.junit.Test;

public class ZipServiceTest {

	// Test properties loaded via getClassLoader().getResourceAsStream()
	private String propertyPackage = ("org/jenkinsci/plugins/appio/");
	private String propertyFile = propertyPackage + "test.properties";
	
	private String unzippedPath = null;
	private String zippedPath = null;
	
	private Properties testProperties = new Properties();

	public ZipServiceTest() {
		super();
		loadTestProperties();
	}

	// Utility to load test properties
	public void loadTestProperties() {
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(propertyFile);

		try {
			testProperties.load(in);
			
			unzippedPath = testProperties.getProperty("Zip.unzippedPath");
			zippedPath = testProperties.getProperty("Zip.zippedPath");

		} catch (IOException e) {
			fail();
		}
	}	
	
	@Test
	public void zipFile() {
		ZipService zipService = new ZipService();
		try {
			// App.io expects the containing folder
			zipService.zipFile(unzippedPath, zippedPath, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
