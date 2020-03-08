package br.com.guisi.poc.pocartifactmanager.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping(value = "/excel/{sistema}/{tipo}", consumes = { "application/json" }, produces = { "application/json" })
	public ResponseEntity<ArtifactResponse> generateArtifact(
			@PathVariable(value = "sistema", required = true) @Valid String sistema,
    		@PathVariable(value = "tipo", required = true) @Valid String modeloArquivo,
    		@Valid @RequestBody(required = true) String jsonParametros) {

		ArtifactResponse response = this.artifactService.generateArtifact(sistema, modeloArquivo, jsonParametros);
		return ResponseEntity.ok(response);
	}

}
