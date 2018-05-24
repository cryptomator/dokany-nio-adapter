package org.cryptomator.frontend.dokan;

import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

public class AclAttribute implements FileAttribute<List<AclEntry>> {

	private String name;
	private List<AclEntry> aclEntries;

	public AclAttribute(List<AclEntry> aclEntries){
		this.name = "acl:acl";
		this.aclEntries = new ArrayList<>(aclEntries);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<AclEntry> value() {
		return aclEntries;
	}
}
