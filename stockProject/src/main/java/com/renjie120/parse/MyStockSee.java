package com.renjie120.parse;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import sun.org.mozilla.javascript.internal.NativeArray;
import brightmoon.util.FileUtil;
import brightmoon.web.HttpClientUtil;

public class MyStockSee implements Runnable {
	private String[] names;
	private int separateSecond;

	public MyStockSee(String fileName, int separateSecond) {
		String stocks = new FileUtil.ReadFileBuilder(fileName).build()
				.readFile()[0];
		this.separateSecond = separateSecond * 1000;
		names = stocks.split(",");
	}

	static String sendUrl = "http://api.jpush.cn:8800/sendmsg/sendmsg";

	/**
	 * 得到要请求的多个信息的地址.
	 * 
	 * @param names
	 * @return
	 */
	private String getStockUrl(String[] names) {
		StringBuffer str = new StringBuffer("http://hq.sinajs.cn/list=");
		for (String s : names) {
			str.append("s_" + s + ",");
		}
		return str.toString();
	}

	public void run() {
		try {
			while (true) {
				String s = HttpClientUtil.getUrl(getStockUrl(names), "GBK");
				String jsContent = StockUtil.wrapFunction(names, s);
				System.out.println(StockUtil.doWithJs(jsContent));
				Thread.sleep(separateSecond);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
