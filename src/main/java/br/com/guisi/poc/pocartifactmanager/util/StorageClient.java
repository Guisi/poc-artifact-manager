package br.com.guisi.poc.pocartifactmanager.util;

import java.io.InputStream;

public interface StorageClient {

	InputStream readFile(String filename);
	
	Void uploadFile(String filename, InputStream file);
	
	boolean fileExists(String filename);
}