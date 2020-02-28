package br.com.guisi.poc.pocartifactmanager.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.guisi.poc.pocartifactmanager.service.ArtifactService;

@RestController
public class ArtifactApiController {

	@Autowired
	private ArtifactService artifactService;

	@GetMapping(value = "/artifact/download", produces = { "application/json" })
	public ResponseEntity<byte[]> downloadArtifactByName(@Valid @RequestParam(value = "filename", required = true) String filename) {
		byte[] bytes = this.artifactService.downloadArtifactByName(filename);
		return bytes != null ? ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename).body(bytes)
				: ResponseEntity.noContent().build();
	}

}
