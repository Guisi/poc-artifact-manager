package br.com.guisi.poc.pocartifactmanager.util.storage;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Component
public class AmazonS3StorageClient implements StorageClient {

	private static final String DELIMITER = "/";

	private AmazonS3 s3client;
	
	@Autowired
	private CacheManager cacheManager;

	@Value("${amazon-aws.bucketName}")
	private String bucketName;
	
	@Value("${amazon-aws.bucketRootDir}")
	private String bucketRootDir;

	@Value("${amazon-aws.accessKey}")
	private String accessKey;

	@Value("${amazon-aws.secretKey}")
	private String secretKey;

	@Value("${amazon-aws.regionName}")
	private String regionName;

	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Regions.fromName(regionName)).build();
	}
	
	@Override
	@Cacheable(value = "poc-artifacts/temp")
	public List<S3ObjectSummary> listFiles() {
		System.out.println("S3 Listing files");

		ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(this.bucketName).withPrefix(this.bucketRootDir);
		return this.s3client.listObjectsV2(request).getObjectSummaries().stream().filter(o -> !o.getKey().endsWith(DELIMITER)).collect(Collectors.toList());
	}
	
	@Scheduled(initialDelay = 10000, fixedRate = 10000)
	public void clearCache() {
		System.out.println("Limpando cache");
		this.cacheManager.getCacheNames().forEach(name -> this.cacheManager.getCache(name).clear());
	}

	@Override
	public String generateDownloadURL(String key, Date expiration) {
		URL url = this.s3client.generatePresignedUrl(this.bucketName, key, expiration);
		return url.toExternalForm();
	}

}
