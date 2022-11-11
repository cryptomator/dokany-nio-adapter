package org.cryptomator.frontend.dokany.internal.structure;

import java.util.Arrays;
import java.util.List;

import org.cryptomator.frontend.dokany.internal.constants.CreationDisposition;
import org.cryptomator.frontend.dokany.internal.constants.FileAttribute;
import com.sun.jna.Structure;

public class CreateData extends Structure {
	public int DesiredAccess;
	public FileAttribute FileAttributes;
	public int ShareAccess;
	public CreationDisposition CreateDisposition;
	public int CreateOptions;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
		        "DesiredAccess",
		        "FileAttributes",
		        "ShareAccess",
		        "CreateDisposition",
		        "CreateOptions");
	}
}
