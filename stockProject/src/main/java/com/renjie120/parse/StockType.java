package com.renjie120.parse;

public enum StockType {
	SH("sh"), SZ("sz");
	private String tp;

	private StockType(String context) {
		this.tp = context;
	}
}
