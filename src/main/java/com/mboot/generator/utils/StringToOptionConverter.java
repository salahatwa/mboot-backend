package com.mboot.generator.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mboot.generator.models.OptionParameter;

import lombok.SneakyThrows;

@Component
public class StringToOptionConverter implements Converter<String, OptionParameter> {

	@Autowired
	private ObjectMapper objectMapper;
	TypeFactory factory = TypeFactory.defaultInstance();

	@Override
	@SneakyThrows
	public OptionParameter convert(String source) {
			return objectMapper.readValue(source, OptionParameter.class);
	}
}