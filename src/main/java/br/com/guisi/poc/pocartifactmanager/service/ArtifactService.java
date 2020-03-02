package br.com.guisi.poc.pocartifactmanager.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.guisi.poc.pocartifactmanager.constants.ArtifactTypeEnum;
import br.com.guisi.poc.pocartifactmanager.model.Artifact;
import br.com.guisi.poc.pocartifactmanager.model.ArtifactResponse;
import br.com.guisi.poc.pocartifactmanager.util.notification.NotificationClient;
import br.com.guisi.poc.pocartifactmanager.util.storage.StorageClient;

@Service
public class ArtifactService {

	@Autowired
	private StorageClient storageClient;
	
	@Autowired
	private NotificationClient notificationClient;
	
	public ArtifactResponse getArtifactByFilter(List<String> artifactIds) {
		ArtifactResponse response = new ArtifactResponse();
		
		//TODO incluir parametro em configmap
		Instant expiration = Instant.now().plus(5, ChronoUnit.MINUTES);
		
		for (String artifactId : artifactIds) {
			String url = this.storageClient.generateDownloadURL(artifactId, Date.from(expiration));
			if (url != null) {
				Artifact artifact = new Artifact();
				artifact.setArtifactId(artifactId);
				artifact.setUrl(url);
				response.addArtifact(artifact);
			}
		}
		
		return response;
	}
	
	public ArtifactResponse generateArtifact(@Valid @PathVariable("artifactType") ArtifactTypeEnum artifactType,
			@Valid @RequestBody(required = true) Map<String, String> parameters) {

		String messageId = this.notificationClient.publishMessage("arn:aws:sns:sa-east-1:060877748249:poc-artifact-topic", parameters.toString());
		System.out.println(messageId);
		
		Artifact artifact = new Artifact();
		artifact.setArtifactId(UUID.randomUUID().toString());

		ArtifactResponse response = new ArtifactResponse();
		response.addArtifact(artifact);
		
		return response;
	}
}
