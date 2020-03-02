package br.com.guisi.poc.pocartifactmanager.util.storage;

import java.util.Date;

public interface StorageClient {

	String generateDownloadURL(String directoryName, Date expiration);
}