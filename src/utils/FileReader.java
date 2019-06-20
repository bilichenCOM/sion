package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileReader {

	private static final Logger LOGGER = Logger.getLogger(FileReader.class);
	
	public String readFromResourceFolder() {
		String path = getClass().getClassLoader().getResource("message.txt").getPath();
		path = path.replaceAll("^\\/", "");
		return readFile(path);
	}

	public static String readFile(String uri) {
		StringBuilder result = new StringBuilder();
		Path path = Paths.get(uri);
		try (InputStream inputStream = Files.newInputStream(path);
				Scanner scanner = new Scanner(inputStream)) {

			while (scanner.hasNextLine()) {
				result.append(scanner.nextLine())
					.append("\r\n");
			}
		} catch (IOException e) {
			LOGGER.debug("problems by reading file...", e);
		}
		return result.toString();
	}
	
	public static void main(String[] args) {
		FileReader reader = new FileReader();
		String x = reader.readFromResourceFolder();
		System.out.println(x);
	}
}
