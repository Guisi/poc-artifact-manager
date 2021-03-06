package br.com.guisi.poc.pocartifactmanager.util.notification;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Component
public class AmazonSNSNotificationClient implements NotificationClient {

	private AmazonSNS snsClient;

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
	public String publishMessage(String topic, String message, Map<String, String> attributes) {
		final PublishRequest publishRequest = new PublishRequest(topic, message);
		attributes.entrySet().forEach(entry -> {
			MessageAttributeValue value = new MessageAttributeValue();
			value.setDataType("String");
			value.setStringValue(entry.getValue());
			publishRequest.addMessageAttributesEntry(entry.getKey(), value);	
		});
		
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		return publishResponse.getMessageId();
	}
}