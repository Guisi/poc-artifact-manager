package br.com.guisi.poc.pocartifactmanager.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArtifactTypeEnum {
	
	EXCEL_CDP_LOTE_SALVADO;

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(name());
	}

	@JsonCreator
	public static ArtifactTypeEnum fromValue(String text) {
		for (ArtifactTypeEnum b : ArtifactTypeEnum.values()) {
			if (String.valueOf(b.name()).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
