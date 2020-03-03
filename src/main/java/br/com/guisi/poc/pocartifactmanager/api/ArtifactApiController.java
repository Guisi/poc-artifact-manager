package br.com.guisi.poc.pocartifactmanager.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.guisi.poc.pocartifactmanager.constants.ArtifactTypeEnum;
import br.com.guisi.poc.pocartifactmanager.model.ArtifactResponse;
import br.com.guisi.poc.pocartifactmanager.service.ArtifactService;

@RestController
public class ArtifactApiController {

	@Autowired
	private ArtifactService artifactService;

	@GetMapping(value = "/artifact", produces = { "application/json" })
	public ResponseEntity<ArtifactResponse> getArtifactByFilter(@Valid @RequestParam(value = "artifactIds", required = true) List<String> artifactIds) {

		ArtifactResponse response = this.artifactService.getArtifactByFilter(artifactIds);
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/artifact/{artifactType}", consumes = { "application/json" }, produces = { "application/json" })
	public ResponseEntity<ArtifactResponse> generateArtifact(@Valid @PathVariable("artifactType") ArtifactTypeEnum artifactType,
			@Valid @RequestBody(required = true) Map<String, String> parameters) throws Exception {

		ArtifactResponse response = this.artifactService.generateArtifact(artifactType, parameters);
		return ResponseEntity.ok(response);
	}

}
