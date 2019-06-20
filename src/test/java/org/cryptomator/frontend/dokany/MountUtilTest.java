package org.cryptomator.frontend.dokany;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.dokany.java.constants.DokanOption.*;
import static org.cryptomator.frontend.dokany.MountUtil.MountOptions;

public class MountUtilTest {

	@Test
	void testIllegalOptionString() {
		String optionsString = "Axolotl";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testPosixStyle() {
		String optionsString = "-t 10 -aus 1024";
		Assertions.assertDoesNotThrow(() -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testWindowsStyle() {
		String optionsString = "\\t 10 \\aus 1024";
		Assertions.assertDoesNotThrow(() -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testMixingWindowsAndPosixFails() {
		String optionsString = "-t 10 \\aus 1024";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testMixingWindowsAndGnuFails() {
		String optionsString = "--thread-count 10 \\aus 1024";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testMixingPosixAndGnuStyle() {
		String optionsString = "-t 10 --allocation-unit-size 1024";
		Assertions.assertDoesNotThrow(() -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testDokanOptionsParsing() {
		String optionsString = "--options CURRENT_SESSION,DEBUG_MODE";
		Assertions.assertDoesNotThrow(() -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testUnsupportedDokanOptionsFail() {
		String optionsString = "--options CURRENT_SESSION,ALT_STREAM";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parseOrDefault(optionsString));
	}

	@Test
	void testParsing() throws ParseException {
		String optionsString = "-t 10 --sector-size 4096 --options CURRENT_SESSION,DEBUG_MODE";

		MountOptions expected = new MountOptions();
		expected.getGeneralOptions().put("threadCount", 10);
		expected.getGeneralOptions().put("sectorSize", 4096);
		expected.getDokanOptions().add(CURRENT_SESSION, DEBUG_MODE);

		MountOptions actual = MountUtil.parseOrDefault(optionsString);
		Assertions.assertEquals(actual.getDokanOptions(), expected.getDokanOptions());
		Assertions.assertEquals(actual.getGeneralOptions(), expected.getGeneralOptions());
	}

}
