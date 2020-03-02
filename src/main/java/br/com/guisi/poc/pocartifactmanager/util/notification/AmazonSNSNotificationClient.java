package br.com.guisi.poc.pocartifactmanager.util.notification;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Component
public class AmazonSNSNotificationClient implements NotificationClient {

	private AmazonSNS snsClient;

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
		this.snsClient = AmazonSNSClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Regions.fromName(regionName)).build();
	}
	
	@Override
	public String publishMessage(String topic, String message) {
		final PublishRequest publishRequest = new PublishRequest(topic, message);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		return publishResponse.getMessageId();
	}
}