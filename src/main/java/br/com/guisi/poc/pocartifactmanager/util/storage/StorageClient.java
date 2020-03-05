package br.com.guisi.poc.pocartifactmanager.util.storage;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public interface StorageClient {

	String generateDownloadURL(String directoryName, Date expiration);
	
	List<S3ObjectSummary> listFiles();
	
	void clearCache();
	
}