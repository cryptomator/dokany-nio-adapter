package com.dokany.java.next.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Directory extends Resource {

	private static final long SIZE = 4L;

	private final List<Resource> resources;

	public Directory(String name, int attributes) {
		super(name,attributes, SIZE);
		this.resources = new ArrayList<>();
	}

	@Override
	public Type getType() {
		return Type.DIR;
	}

	void addResource(Resource r) {
		resources.add(r);
	}

	void removeResource(Resource r) {
		resources.remove(r);
	}

	public Stream<Resource> list() {
		return resources.stream();
	}

}
