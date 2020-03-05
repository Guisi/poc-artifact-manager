package br.com.guisi.poc.pocartifactmanager.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		
		Instant expiration = Instant.now().plus(5, ChronoUnit.MINUTES);

		for (String artifactId : artifactIds) {
			List<S3ObjectSummary> objects = this.storageClient.listFiles();
			Optional<S3ObjectSummary> optional = objects.stream().filter(o -> o.getKey().contains(artifactId)).findFirst();
			if (optional.isPresent()) {
				String url = this.storageClient.generateDownloadURL(artifactId, Date.from(expiration));
				
				if (url != null) {
					Artifact artifact = new Artifact();
					artifact.setArtifactId(artifactId);
					artifact.setUrl(url);
					response.addArtifact(artifact);
				}
			}
		}

		return response;
	}

	public ArtifactResponse generateArtifact(@Valid @PathVariable("artifactType") String artifactType,
			@Valid @RequestBody(required = true) Map<String, String> parameters) {

		try {
			parameters.put("TIPO_ARQUIVO", artifactType);
			
			String artifactId = this.notificationClient.publishMessage("arn:aws:sns:sa-east-1:060877748249:poc-artifact-topic",
					new ObjectMapper().writeValueAsString(parameters));
			
			Artifact artifact = new Artifact();
			artifact.setArtifactId(artifactId);

			ArtifactResponse response = new ArtifactResponse();
			response.addArtifact(artifact);

			return response;
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Erro ao enviar solicitação de geração do arquivo.", e);
		}

	}

}
