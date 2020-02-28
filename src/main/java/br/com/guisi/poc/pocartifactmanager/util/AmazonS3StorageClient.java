package br.com.guisi.poc.pocartifactmanager.util;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Component
public class AmazonS3StorageClient implements StorageClient {

	private static final Logger logger = LogManager.getLogger(AmazonS3StorageClient.class);

	private AmazonS3 s3client;

	@Value("${amazon-aws.bucketName}")
	private String bucketName;

	@Value("${amazon-aws.accessKey}")
	private String accessKey;

	@Value("${amazon-aws.secretKey}")
	private String secretKey;

	@Value("${amazon-aws.regionName}")
	private String regionName;

	@PostConstruct
	private void initializeAmazon() {
		AWSStaticCredentialsProvider credentials = getCredential();
		this.s3client = getClient(credentials);
	}
	
	@Override
	public Void uploadFile(String filename, InputStream file) {
		try {
			ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
			s3ObjectMetadata.setContentLength(file.available());

			s3client.putObject(new PutObjectRequest(bucketName, filename, file, s3ObjectMetadata));
		} catch (Exception e) {
			String error = "ERROR - Problema ao fazer upload do arquivo: " + filename;
			logger.error("uploadFile fileName: " + filename, e);
			throw new IllegalStateException(error, e);
		}
		return null;
	}
	
	@Override
	public InputStream readFile(String filename) {
		S3Object s3Object = this.s3client.getObject(this.bucketName, filename);
		return s3Object.getObjectContent();
	}
	
	@Override
	public boolean fileExists(String filename) {
		return this.s3client.doesObjectExist(this.bucketName, filename);
	}
	
	/**
	 * Método para gerar credeciais da amazon
	 * 
	 * @return AWSStaticCredentialsProvider
	 */
	private AWSStaticCredentialsProvider getCredential() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		return new AWSStaticCredentialsProvider(credentials);
	}
	
	/**
	 * Método para fazer conexão com AWS S3 com base na credencial e região solicitada
	 * 
	 * @param credentials
	 * @return AmazonS3
	 */
	private AmazonS3 getClient(AWSStaticCredentialsProvider credentials) {
		return AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(credentials)
				  .withRegion(Regions.fromName(regionName))
				  .build();
	}
	
}
