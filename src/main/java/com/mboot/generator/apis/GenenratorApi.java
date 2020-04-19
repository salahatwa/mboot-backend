package com.mboot.generator.apis;

import static org.openapitools.codegen.Constants.CLI_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.openapitools.codegen.cmd.CompletionCommand;
import org.openapitools.codegen.cmd.ConfigHelp;
import org.openapitools.codegen.cmd.Generate;
import org.openapitools.codegen.cmd.GenerateBatch;
import org.openapitools.codegen.cmd.HelpCommand;
import org.openapitools.codegen.cmd.ListGenerators;
import org.openapitools.codegen.cmd.Meta;
import org.openapitools.codegen.cmd.OpenApiGeneratorCommand;
import org.openapitools.codegen.cmd.Validate;
import org.openapitools.codegen.cmd.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mboot.generator.models.OptionKeys;
import com.mboot.generator.models.OptionParameter;
import com.mboot.generator.utils.ZipUtils;

import io.airlift.airline.Cli;
import io.airlift.airline.ParseArgumentsUnexpectedException;
import io.airlift.airline.ParseOptionMissingException;
import io.airlift.airline.ParseOptionMissingValueException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/generate/by")
public class GenenratorApi {

	private Cli.CliBuilder<OpenApiGeneratorCommand> builder = Cli.<OpenApiGeneratorCommand>builder(CLI_NAME)
			.withDefaultCommand(HelpCommand.class).withCommands(ListGenerators.class, Generate.class, Meta.class,
					HelpCommand.class, ConfigHelp.class, Validate.class, Version.class, CompletionCommand.class,
					GenerateBatch.class);
	private ZipUtils ZipUtils = new ZipUtils();

	@PostMapping("/url")
	public ResponseEntity<?> submit(HttpServletResponse response, @RequestBody OptionParameter options) {

		response.setContentType("application/octet-stream");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Disposition", "attachment;filename=" + "test.zip");

		log.info("Options:{}", options);
//		log.info("Options:{}",options.get(OptionKeys.ACCESS_CODE));

		try {
			
			generate(response, options.get(OptionKeys.JSON_PATH).toString(),options);
		} catch (ParseArgumentsUnexpectedException e) {
			System.err.printf(Locale.ROOT, "[error] %s%n%nSee '%s help' for usage.%n", e.getMessage(), CLI_NAME);
		} catch (ParseOptionMissingException | ParseOptionMissingValueException e) {
			System.err.printf(Locale.ROOT, "[error] %s%n", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping("/file")
	public ResponseEntity<?> submit(HttpServletResponse response, @RequestParam String options,
			@RequestParam("file") MultipartFile file) {

		response.setContentType("application/octet-stream");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-Disposition", "attachment;filename=" + "test.zip");
		log.info("Options:{}", options);
//		log.info("Options:{}",options.get(OptionKeys.ACCESS_CODE));
		try {
			File resultFile = Files.createTempFile(UUID.randomUUID().toString().replaceAll("-", ""), "tmp").toFile();
			FileUtils.copyInputStreamToFile(file.getInputStream(), resultFile);
			generate(response, resultFile.getAbsolutePath(),objectMapper.readValue(options, OptionParameter.class));
			resultFile.deleteOnExit();
		} catch (ParseArgumentsUnexpectedException e) {
			System.err.printf(Locale.ROOT, "[error] %s%n%nSee '%s help' for usage.%n", e.getMessage(), CLI_NAME);
		} catch (ParseOptionMissingException | ParseOptionMissingValueException e) {
			System.err.printf(Locale.ROOT, "[error] %s%n", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void generate(HttpServletResponse response, String input,OptionParameter options) throws IOException {
		String uniqueName = UUID.randomUUID().toString().replaceAll("-", "");
		File resultFolder = Files.createTempDirectory(uniqueName).toFile();
		log.info(resultFolder.getAbsolutePath());
		String[] run = { "generate", "-i", input, "-g", options.get(OptionKeys.LANGUAGE).toString(), "-o", resultFolder.getAbsolutePath() };
		builder.build().parse(run).run();

		ZipUtils.zipDirectoryStream(resultFolder, response);

		resultFolder.deleteOnExit();
	}
}
