package org.cryptomator.frontend.dokany;

import com.dokany.java.structure.EnumIntegerSet;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.dokany.java.constants.DokanOption.CURRENT_SESSION;
import static com.dokany.java.constants.DokanOption.DEBUG_MODE;
import static org.cryptomator.frontend.dokany.MountUtil.MountOptions;

public class MountUtilTest {

	@Test
	void testIllegalOptionString() {
		String optionsString = "Axolotl";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parse(optionsString));
	}

	@Test
	void testDokanOptionsParsing() {
		String optionsString = "--options CURRENT_SESSION,DEBUG_MODE";
		Assertions.assertDoesNotThrow(() -> MountUtil.parse(optionsString));
	}

	@Test
	void testUnsupportedDokanOptionsFail() {
		String optionsString = "--options CURRENT_SESSION,ALT_STREAM";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parse(optionsString));
	}

	@Test
	void testThreadCountToBig() {
		String optionsString = "--thread-count 65.536";
		Assertions.assertThrows(IllegalArgumentException.class, () -> MountUtil.parse(optionsString));
	}

	@Test
	void testParsing() throws ParseException {
		String optionsString = "--thread-count 10 --sector-size 4096 --options CURRENT_SESSION,DEBUG_MODE";

		MountOptions expected = new MountUtil.MountOptionsBuilder().addThreadCount((short) 10).addSectorSize(4096).addDokanOptions(new EnumIntegerSet<>(CURRENT_SESSION, DEBUG_MODE)).build();

		MountOptions actual = MountUtil.parse(optionsString);
		Assertions.assertEquals(actual.getDokanOptions(), expected.getDokanOptions());
		Assertions.assertEquals(actual.getThreadCount(), expected.getThreadCount());
		Assertions.assertEquals(actual.getSectorSize(), expected.getSectorSize());
		Assertions.assertEquals(actual.getTimeout(), expected.getTimeout());
	}

	@Test
	void shortOptionDisabled() {
		String optionsString = "-t 10 -to 1000 -ss 4096";
		Assertions.assertThrows(ParseException.class, () -> MountUtil.parse(optionsString));
	}

}
