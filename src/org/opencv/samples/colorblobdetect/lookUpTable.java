package org.opencv.samples.colorblobdetect;

public enum lookUpTable {

	FACEBOOK("111111"), HOTMAIL("000000"), YAHOO("110011"), GOOGLE("001100");
	private String value;

	private lookUpTable(String value) {
		this.value = value;
	}

	public String getCode() {
		return value;
	}

	public lookUpTable get(String code) {
		for (lookUpTable s : values()) {
			if (s.value.equals(code))
				return s;
		}
		return null;
	}

};
