package br.com.guisi.poc.pocartifactmanager.util.storage;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
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

	private List<S3ObjectSummary> listFiles(String directoryName) {
		String prefix = this.bucketRootDir + DELIMITER + directoryName;
		ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(this.bucketName).withPrefix(prefix).withMaxKeys(2);
		return this.s3client.listObjectsV2(request).getObjectSummaries().stream().filter(o -> !o.getKey().endsWith(DELIMITER)).collect(Collectors.toList());
	}

	@Override
	public String generateDownloadURL(String directoryName, Date expiration) {
		List<S3ObjectSummary> objects = this.listFiles(directoryName);
		if (!objects.isEmpty()) {
			URL url = this.s3client.generatePresignedUrl(this.bucketName, objects.get(0).getKey(), expiration);
			return url.toExternalForm();
		}

		return null;
	}

}
