package org.aashish.pointme.entity.v2;

import lombok.Data;

@Data
public class ResourceAction<T> {
	private String type;
	private String id;
	private T body;
}
