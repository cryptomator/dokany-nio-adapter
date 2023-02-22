package org.cryptomator.frontend.dokany.mount;

import org.cryptomator.frontend.dokany.internal.constants.DokanOption;
import org.cryptomator.frontend.dokany.internal.structure.EnumIntegerSet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.CURRENT_SESSION;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.DEBUG_MODE;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.DISPATCH_DRIVER_LOGS;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.ENABLE_FCB_GARBAGE_COLLECTION;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.MOUNT_MANAGER;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.REMOVABLE_DRIVE;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.STD_ERR_OUTPUT;
import static org.cryptomator.frontend.dokany.internal.constants.DokanOption.WRITE_PROTECTION;

/**
 * Utility class for processing a string of mount options.
 * <p>
 * Each option starts with "--" (GNU-style). Different options must be separated with at least one whitespace.
 * Available options are: (GNU-long-option -- Description)
 * <li>
 * <item>{@value OPTION_THREADCOUNT_NAME} -- {@value OPTION_THREADCOUNT_DESC}</item>
 * <item>{@value OPTION_TIMEOUT_NAME} -- {@value OPTION_TIMEOUT_DESC}</item>
 * <item>{@value OPTION_OPTIONS_NAME} -- {@value OPTION_OPTIONS_DESC}</item>
 * </li>
 */
public class MountOptionParser {

	private static final Options OPTIONS = new Options();

	private static final String OPTION_ALLOCUNITSIZE_NAME = "allocation-unit-size";
	private static final String OPTION_ALLOCUNITSIZE_DESC = "TODO";
	private static final String OPTION_OPTIONS_NAME = "options";
	private static final String OPTION_OPTIONS_DESC = "Features enabled for the mount given as a comma separated list without whitespaces. Supported features are DEBUG_MODE, STD_ERR_OUTPUT, MOUNT_MANAGER, CURRENT_SESSION, REMOVABLE_DRIVE, WRITE_PROTECTION and DISPATCH_DRIVER_LOGS. For their description see the Dokany API documentation.";
	private static final String OPTION_THREADCOUNT_NAME = "thread-count";
	private static final String OPTION_THREADCOUNT_DESC = "Number of threads to be used by Dokan library internally. More threads will handle more events at the same time.";
	private static final String OPTION_SECTORSIZE_NAME = "sector-size";
	private static final String OPTION_SECTORSIZE_DESC = "TODO";
	private static final String OPTION_TIMEOUT_NAME = "timeout";
	private static final String OPTION_TIMEOUT_DESC = "Maximum timeout in milliseconds of each request before Dokany gives up to wait events to complete and unmounts the filesystem.";


	static {
		OPTIONS.addOption(null, OPTION_THREADCOUNT_NAME, true, OPTION_THREADCOUNT_DESC);
		OPTIONS.addOption(null, OPTION_ALLOCUNITSIZE_NAME, true, OPTION_ALLOCUNITSIZE_DESC);
		OPTIONS.addOption(null, OPTION_SECTORSIZE_NAME, true, OPTION_SECTORSIZE_DESC);
		OPTIONS.addOption(null, OPTION_TIMEOUT_NAME, true, OPTION_TIMEOUT_DESC);
		OPTIONS.addOption(Option.builder() //
				.argName("arg1,arg2,...") //
				.longOpt(OPTION_OPTIONS_NAME) //
				.hasArgs() //
				.valueSeparator(',') //
				.desc(OPTION_OPTIONS_DESC) //
				.build());
	}

	private static final EnumIntegerSet<DokanOption> POSSIBLY_SUPPORTED_DOKAN_OPTIONS = new EnumIntegerSet(DEBUG_MODE, //
			ENABLE_FCB_GARBAGE_COLLECTION, //
			STD_ERR_OUTPUT, //
			MOUNT_MANAGER, //
			CURRENT_SESSION, //
			REMOVABLE_DRIVE, //
			WRITE_PROTECTION, //
			DISPATCH_DRIVER_LOGS);

	record MountOptions(short threadCount, int timeout, int sectorSize, int allocationUnitSize, EnumIntegerSet<DokanOption> dokanOptions) {

	}

	static class MountOptionsBuilder {

		private short threadCount = 5;
		private int timeout = 10000;
		private final EnumIntegerSet<DokanOption> dokanOptions = new EnumIntegerSet<>(DokanOption.class);
		private int sectorSize = 4096;
		private int allocationUnitSize = 4096;


		public MountOptionsBuilder() {
		}

		public MountOptionsBuilder setThreadCount(short num) {
			this.threadCount = num;
			return this;
		}

		public MountOptionsBuilder setTimeout(int milliseconds) {
			this.timeout = milliseconds;
			return this;
		}

		public MountOptionsBuilder addDokanOptions(Collection<DokanOption> other) {
			this.dokanOptions.addAll(other);
			return this;
		}

		public MountOptionsBuilder setSectorSize(int sectorSize) {
			this.sectorSize = sectorSize;
			return this;
		}

		public MountOptionsBuilder setAllocationUnitSize(int allocationUnitSize) {
			this.allocationUnitSize = allocationUnitSize;
			return this;
		}

		public MountOptions build() {
			return new MountOptions(threadCount, timeout, sectorSize, allocationUnitSize, dokanOptions);
		}

	}


	public static MountOptions parse(String argsString) throws ParseException, IllegalArgumentException {
		CommandLineParser parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();

		String[] args = argsString.split(" "); //possible since we don't have any option which must be enclosed by "
		CommandLine cmd = parser.parse(OPTIONS, args);

		if (!cmd.getArgList().isEmpty()) {
			throw new IllegalArgumentException("Unrecognized options:" + cmd.getArgList().toString());
		}

		MountOptionsBuilder builder = new MountOptionsBuilder();
		if (cmd.hasOption(OPTION_THREADCOUNT_NAME)) {
			try {
				builder.setThreadCount(Short.parseShort(cmd.getOptionValue(OPTION_THREADCOUNT_NAME)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("The maximum allowed number of threads is 65.535.", e);
			}
		}
		if (cmd.hasOption(OPTION_TIMEOUT_NAME)) {
			builder.setTimeout(Integer.parseInt(cmd.getOptionValue(OPTION_TIMEOUT_NAME)));
		}
		if (cmd.hasOption(OPTION_OPTIONS_NAME)) {
			builder.addDokanOptions(Arrays.stream(cmd.getOptionValues(OPTION_OPTIONS_NAME)) //
					.filter(s -> !s.isEmpty()) //
					.map(String::trim) //
					.map(MountOptionParser::convertAndCheck) //
					.collect(Collectors.toList()) //
			);

		}
		return builder.build();
	}

	private static DokanOption convertAndCheck(String s) {
		DokanOption op = DokanOption.valueOf(s);
		if (POSSIBLY_SUPPORTED_DOKAN_OPTIONS.contains(op)) {
			return op;
		} else {
			throw new IllegalArgumentException("Dokany option " + s + " not supported.");
		}
	}

	/**
	 * Gives info about the supported mount options and the format.
	 *
	 * @return An information string
	 */
	public static String info() {
		List<String> description = new ArrayList<>();
		POSSIBLY_SUPPORTED_DOKAN_OPTIONS.forEach(op -> description.add(op.name() + " - " + op.getDescription() + "\n"));
		String header = "Each option starts with \"--\" (GNU-style). Different options must be separated with at least one whitespace.";
		String syntax = "[--thread-count INT] [--timeout INT] [--options OPTION1,OPTION2,...] --file-systen-name STRING";
		HelpFormatter help = new HelpFormatter();
		StringWriter writer = new StringWriter();
		help.printHelp(new PrintWriter(writer), 60, syntax, header, OPTIONS, 3, 3, "");
		return writer.toString();
	}

}
