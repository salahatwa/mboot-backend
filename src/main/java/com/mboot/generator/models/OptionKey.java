package com.mboot.generator.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptionKey {
	@JsonProperty(value = "key")
	private final String key;

	public OptionKey(String ketName) {
		this.key = ketName;
	}

	public String name() {
		return key;
	}

	@Override
	public String toString() {
		return key ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		OptionKey fortKey = (OptionKey) o;
		return Objects.equals(key, fortKey.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}
}
