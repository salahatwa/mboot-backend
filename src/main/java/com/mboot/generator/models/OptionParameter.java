package com.mboot.generator.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Container for all request keys and values values maybe single value or list
 */
public class OptionParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws JsonProcessingException {
		OptionParameter param=new OptionParameter();
		param.add(new OptionKey(OptionKeys.ACCESS_CODE.name()),"oo");
	ObjectMapper map=new ObjectMapper();
	System.out.println(map.writeValueAsString(param));
	
	String tt="{\"params\":{\"access_code\":\"oo\"}}";
	OptionParameter d=map.readValue(tt, OptionParameter.class);
	System.out.println(d.get(OptionKeys.ACCESS_CODE));
	}
	@JsonProperty(value = "params")
	private Map<OptionKey, Object> parameter = new HashMap<>();

	public OptionParameter add(OptionKey OptionKey, Object value) {
		if (value != null && parameter.get(OptionKey) == null)
			parameter.put(OptionKey, value);
		return this;
	}

	public Object get(OptionKey OptionKey) {
		return parameter.get(OptionKey);
	}

	public void iterator(ParameterIterator iterator) {
		parameter.entrySet().stream().sorted((k, v) -> k.getKey().name().compareTo(v.getKey().name()))
				.forEach((e) -> iterator.parameter(e.getKey(), e.getValue()));
	}

	public interface ParameterIterator {
		void parameter(OptionKey key, Object value);
	}

	@Override
	public String toString() {
		return "OptionParameter{" + "parameter=" + parameter + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		OptionParameter parameter1 = (OptionParameter) o;
		return Objects.equals(parameter, parameter1.parameter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameter);
	}
}
