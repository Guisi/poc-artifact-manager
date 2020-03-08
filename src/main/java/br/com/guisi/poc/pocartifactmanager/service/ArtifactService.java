package br.com.guisi.poc.pocartifactmanager.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import br.com.guisi.poc.pocartifactmanager.model.Artifact;
import br.com.guisi.poc.pocartifactmanager.model.ArtifactResponse;
import br.com.guisi.poc.pocartifactmanager.util.notification.NotificationClient;
import br.com.guisi.poc.pocartifactmanager.util.storage.StorageClient;

@Service
public class ArtifactService {
	
	private static final String TIPO_ARQUIVO = "TIPO_ARQUIVO";
	private static final String TIPO_ARQUIVO_EXCEL = "EXCEL";
	private static final String SISTEMA = "SISTEMA";
	private static final String MODELO_ARQUIVO = "MODELO_ARQUIVO";

	@Autowired
	private StorageClient storageClient;

	@Autowired
	private NotificationClient notificationClient;

	public ArtifactResponse getArtifactByFilter(List<String> artifactIds) {
		ArtifactResponse response = new ArtifactResponse();

		Instant expiration = Instant.now().plus(5, ChronoUnit.MINUTES);

		for (String artifactId : artifactIds) {
			List<S3ObjectSummary> objects = this.storageClient.listFiles();
			Optional<S3ObjectSummary> optional = objects.stream().filter(o -> o.getKey().contains(artifactId))
					.findFirst();
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

	public ArtifactResponse generateArtifact(@Valid String sistema, @Valid String modeloArquivo,
			@Valid String jsonParametros) {

		Map<String, String> attributes = new HashMap<>();
		attributes.put(TIPO_ARQUIVO, TIPO_ARQUIVO_EXCEL);
		attributes.put(SISTEMA, sistema);
		attributes.put(MODELO_ARQUIVO, modeloArquivo);

		String artifactId = this.notificationClient
				.publishMessage("arn:aws:sns:sa-east-1:060877748249:poc-artifact-topic", jsonParametros, attributes);

		Artifact artifact = new Artifact();
		artifact.setArtifactId(artifactId);

		ArtifactResponse response = new ArtifactResponse();
		response.addArtifact(artifact);

		return response;

	}

}
