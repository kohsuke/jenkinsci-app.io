package org.jenkinsci.plugins.appio;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jenkinsci.plugins.appio.service.Zipper;
import org.junit.Test;

public class ZipperTest {

	// Test properties loaded via getClassLoader().getResourceAsStream()
	private String propertyPackage = ("org/jenkinsci/plugins/appio/");
	private String propertyFile = propertyPackage + "test.properties";
	
	private String unzippedPath = null;
	private String zippedPath = null;
	
	private Properties testProperties = new Properties();

	public ZipperTest() {
		super();
		loadTestProperties();
	}

	// Utility to load test properties
	public void loadTestProperties() {
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(propertyFile);

		try {
			testProperties.load(in);
			
			unzippedPath = testProperties.getProperty("Appio.unzippedPath");
			zippedPath = testProperties.getProperty("Appio.zippedPath");

		} catch (IOException e) {
			fail();
		}
	}	
	
	@Test
	public void zipAppFile() {
		Zipper zipper = new Zipper(unzippedPath);
		zipper.zipIt(zippedPath);
	}
}
