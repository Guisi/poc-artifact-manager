package br.com.guisi.poc.pocartifactmanager.service;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.guisi.poc.pocartifactmanager.util.StorageClient;

@Service
public class ArtifactService {

	@Autowired
	private StorageClient storageClient;
	
	public byte[] downloadArtifactByName(String filename) {
		if (this.storageClient.fileExists(filename)) {
	    	try {
		    	InputStream stream = this.storageClient.readFile(filename);
		
		       	return IOUtils.toByteArray(stream);
	    	} catch (Exception e) {
	    		throw new IllegalStateException(e);
	    	}
		}
		return null;
	}
}
