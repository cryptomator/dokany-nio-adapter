package org.cryptomator.frontend.dokany;

import com.dokany.java.constants.DokanOption;
import com.dokany.java.structure.EnumIntegerSet;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dokany.java.constants.DokanOption.*;

/**
 * Utility class for processing a string of mount options.
 * <p>
 * Each option starts with an "-" (POSIX-style), "\" (DOS-style) or "--" (GNU-style), but the styles must not be mixed. Different options must be separated with at least one whitespace.
 * Available options are (DOS/POSIX option (GNU-long-option) -- Description):
 * <li>
 * <item>t (thread-count) -- Number of threads to be used by Dokan library internally. More threads will handle more events at the same time. </item>
 * <item>aus (allocation-unit-size -- Allocation Unit Size of the volume. This will affect the file size.</item>
 * <item>ss (sector-size) -- Sector Size of the volume. This will affect the file size.</item>
 * <item>to (timeout) -- Maximum timeout in milliseconds of each request before Dokany gives up to wait events to complete.</item>
 * <item>op (options) -- Features enabled for the mount. Supported are DEBUG_MODE, STD_ERR_OUTPUT, MOUNT_MANAGER, CURRENT_SESSION, REMOVABLE_DRIVE and WRITE_PROTECTION. For their description look at the dokany documentation. </item>
 * </li>
 */
public class MountUtil {

	private static final Options OPTIONS = new Options();

	static {
		OPTIONS.addOption("t", "threadCount", true, "Number of threads to be used by Dokan library internally. More threads will handle more events at the same time.");
		OPTIONS.addOption("aus", "allocation-unit-size", true, "Allocation Unit Size of the volume. This will affect the file size.");
		OPTIONS.addOption("ss", "sector-size", true, "Sector Size of the volume. This will affect the file size.");
		OPTIONS.addOption("to", "timeout", true, "Maximum timeout in milliseconds of each request before Dokany gives up to wait events to complete.");
		OPTIONS.addOption(Option.builder("op")
				.argName("arg1,arg2,...")
				.longOpt("options")
				.hasArgs()
				.valueSeparator(',')
				.desc("Features enabled for the mount")
				.build());
	}

	private static final EnumIntegerSet<DokanOption> POSSIBLY_SUPPORTED_DOKAN_OPTIONS = new EnumIntegerSet(
			DEBUG_MODE,
			STD_ERR_OUTPUT,
			MOUNT_MANAGER,
			CURRENT_SESSION,
			REMOVABLE_DRIVE,
			WRITE_PROTECTION);

	public static class MountOptions {

		private final Optional<Short> threadCount;
		private final Optional<Integer> allocationUnitSize;
		private final Optional<Integer> sectorSize;
		private final Optional<Integer> timeout;
		private final EnumIntegerSet<DokanOption> dokanOptions;

		private MountOptions(Optional<Short> threadCount, Optional<Integer> allocationUnitSize, Optional<Integer> sectorSize, Optional<Integer> timeout, EnumIntegerSet<DokanOption> dokanOptions) {
			this.threadCount = threadCount;
			this.allocationUnitSize = allocationUnitSize;
			this.sectorSize = sectorSize;
			this.timeout = timeout;
			this.dokanOptions = dokanOptions;
		}

		public Optional<Short> getThreadCount() {
			return threadCount;
		}

		public Optional<Integer> getAllocationUnitSize() {
			return allocationUnitSize;
		}

		public Optional<Integer> getSectorSize() {
			return sectorSize;
		}

		public Optional<Integer> getTimeout() {
			return timeout;
		}

		public EnumIntegerSet<DokanOption> getDokanOptions() {
			return dokanOptions;
		}
	}

	public static class MountOptionsBuilder {

		private Optional<Short> threadCount = Optional.empty();
		private Optional<Integer> allocationUnitSize = Optional.empty();
		private Optional<Integer> sectorSize = Optional.empty();
		private Optional<Integer> timeout = Optional.empty();
		private EnumIntegerSet<DokanOption> dokanOptions = new EnumIntegerSet<>(DokanOption.class);

		public MountOptionsBuilder() {
		}

		public MountOptionsBuilder addThreadCount(short num) {
			this.threadCount = Optional.ofNullable(num);
			return this;
		}

		public MountOptionsBuilder addAllocationSizeUnit(int bytes) {
			this.allocationUnitSize = Optional.ofNullable(bytes);
			return this;
		}

		public MountOptionsBuilder addSectorSize(int bytes) {
			this.sectorSize = Optional.ofNullable(bytes);
			return this;
		}

		public MountOptionsBuilder addTimeout(int milliseconds) {
			this.timeout = Optional.ofNullable(milliseconds);
			return this;
		}

		public MountOptionsBuilder addDokanOptions(Collection<DokanOption> other) {
			this.dokanOptions.addAll(other);
			return this;
		}

		public MountOptions build() {
			return new MountOptions(threadCount, allocationUnitSize, sectorSize, timeout, dokanOptions);
		}

	}


	public static MountOptions parse(String argsString) throws ParseException, IllegalArgumentException {
		CommandLineParser parser = new DefaultParser();

		if (argsString.contains("\\") && argsString.contains("-")) {
			throw new IllegalArgumentException("DOS-style and POSIX-style must not be mixed.");
		}

		String[] args = argsString.replaceAll("\\\\", "-").split(" "); //possible since we don't have any option which muswt be enclosed by "
		CommandLine cmd = parser.parse(OPTIONS, args);

		if (!cmd.getArgList().isEmpty()) {
			throw new IllegalArgumentException("Unrecognized options:" + cmd.getArgList().toString());
		}

		MountOptionsBuilder builder = new MountOptionsBuilder();
		if (cmd.hasOption("t")) {
			try {
				builder.addThreadCount(Short.parseShort(cmd.getOptionValue("t")));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("The maximum allowed number of threads is 65.535.", e);
			}
		}
		if (cmd.hasOption("aus")) {
			builder.addAllocationSizeUnit(Integer.parseInt(cmd.getOptionValue("aus")));
		}
		if (cmd.hasOption("ss")) {
			builder.addSectorSize(Integer.parseInt(cmd.getOptionValue("ss")));
		}
		if (cmd.hasOption("to")) {
			builder.addTimeout(Integer.parseInt(cmd.getOptionValue("to")));
		}
		if (cmd.hasOption("op")) {
			builder.addDokanOptions(
					Arrays.stream(cmd.getOptionValues("op"))
							.filter(s -> !s.isEmpty())
							.map(String::trim)
							.map(MountUtil::convertAndCheck)
							.collect(Collectors.toList()));

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
		String header = "Each option starts with an \"-\" (POSIX-style), \"\\\" (DOS-style) or \"--\" (GNU-style), but the styles must not be mixed. Different options must be separated with at least one whitespace.";
		String syntax = "[-t INT] [-aus INT] [-ss INT] [-to INT] [-options OPTION1,OPTION2,...]";
		HelpFormatter help = new HelpFormatter();
		StringWriter writer = new StringWriter();
		help.printHelp(new PrintWriter(writer), 60, syntax, header, OPTIONS, 3, 3, "");
		return writer.toString();
	}

}
