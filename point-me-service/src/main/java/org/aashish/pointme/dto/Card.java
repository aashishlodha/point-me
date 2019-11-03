package org.aashish.pointme.dto;

import java.io.Serializable;

public class Card implements Serializable{
	
	private static final long serialVersionUID = 1998457252789116094L;
	private Integer value;
	
	public Card() {}
	
	public Card(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
}
