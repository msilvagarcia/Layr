package layr.commons;

import java.io.IOException;

import layr.commons.FileUtils;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilsTest {

	@Test
	public void grantThatReadTheFile() throws IOException{
		String fileAsString = FileUtils.readFileAsString("file.txt");
		assertEquals("<h1>It really works!</h1>", fileAsString);
	}

	@Test
	public void grantThatCantFindTheFile() throws IOException{
		String fileAsString = FileUtils.readFileAsString("another.file.txt");
		assertNull(fileAsString);
	}
	
}
