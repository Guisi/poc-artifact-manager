package br.com.guisi.poc.pocartifactmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class PocArtifactManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocArtifactManagerApplication.class, args);
	}

}
