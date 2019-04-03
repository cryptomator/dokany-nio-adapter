package org.cryptomator.frontend.dokany;

import com.sun.jna.Pointer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

class OpenFileTest {

	@Test
	@DisplayName("read short string from file")
	void testReadShortString(@TempDir Path tmpDir) throws IOException {
		Path file = tmpDir.resolve("read.txt");
		Files.write(file, "hello world".getBytes(StandardCharsets.US_ASCII));
		try (OpenFile openFile = OpenFile.open(file, EnumSet.of(StandardOpenOption.READ))) {
			Pointer p = Mockito.mock(Pointer.class);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Mockito.doAnswer(invocation -> {
				long offset = invocation.getArgument(0);
				byte[] buf = invocation.getArgument(1);
				int index = invocation.getArgument(2);
				int length = invocation.getArgument(3);
				Assertions.assertEquals(baos.size(), offset);
				baos.write(buf, index, length);
				return null;
			}).when(p).write(Mockito.anyLong(), Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());

			openFile.read(p, 5, 0);
			Assertions.assertArrayEquals("hello".getBytes(), baos.toByteArray());

			baos.reset();
			openFile.read(p, 5, 6);
			Assertions.assertArrayEquals("world".getBytes(), baos.toByteArray());
		}
	}

	@Test
	@DisplayName("write short string to file")
	void testWriteShortString(@TempDir Path tmpDir) throws IOException {
		byte[] input = "hello world".getBytes(StandardCharsets.US_ASCII);
		Path file = tmpDir.resolve("write.txt");
		try (OpenFile openFile = OpenFile.open(file, EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE))) {
			Pointer p = Mockito.mock(Pointer.class);
			Mockito.doAnswer(invocation -> {
				long offset = invocation.getArgument(0);
				byte[] buf = invocation.getArgument(1);
				int index = invocation.getArgument(2);
				int length = invocation.getArgument(3);
				System.arraycopy(input, (int) offset, buf, index, length);
				return null;
			}).when(p).read(Mockito.anyLong(), Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());

			openFile.write(p, 11, 0);
		}
		byte[] written = Files.readAllBytes(file);
		Assertions.assertArrayEquals(input, written);
	}

}
