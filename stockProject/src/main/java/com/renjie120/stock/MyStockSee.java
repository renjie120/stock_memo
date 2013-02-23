package com.renjie120.stock;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import sun.org.mozilla.javascript.internal.NativeArray;
import brightmoon.util.FileUtil;
import brightmoon.web.HttpClientUtil;

public class MyStockSee implements Runnable {
	public static void main(String[] args) {
		if (args.length == 1) {
			String[] str = new FileUtil.ReadFileBuilder(args[0]).build()
					.readFile();
			int i = Integer.parseInt(str[1]);
			System.out.println("来源:" + args[0]);
			Thread th = new Thread(new MyStockSee(args[0], i));
			th.start();
		} else {
			System.out.println("必须输入1个参数:来源’");
			return;
		}
	}

	private String[] names;
	private int separateSecond;

	public MyStockSee(String fileName, int separateSecond) {
		String stocks = new FileUtil.ReadFileBuilder(fileName).build()
				.readFile()[0];
		this.separateSecond = separateSecond * 1000;
		names = stocks.split(",");
	}

	/**
	 * 对返回结果的格式化处理.
	 * 
	 * @param str
	 * @return
	 */
	private String showStock(String str) {
		String[] strs = str.split(",");
		return strs[0].substring(0, 2) + "" + strs[1] + "\t" + strs[3];
	}

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

	/**
	 * 封装js.
	 * 
	 * @param names
	 * @param str
	 * @return
	 */
	private String wrapFunction(String[] names, String str) {
		StringBuffer buf = new StringBuffer("function test(){");
		buf.append(str);
		buf.append("return [");
		for (String s : names) {
			buf.append("hq_str_s_" + s + ",");
		}
		buf = buf.deleteCharAt(buf.lastIndexOf(","));
		buf.append("];}");
		return buf.toString();
	}

	/**
	 * 处理js方法.
	 * 
	 * @param script
	 * @return
	 */
	@SuppressWarnings("restriction")
	private String doWithJs(String script) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("js");
		StringBuffer buf = new StringBuffer();
		try {
			se.eval(script);
			Invocable inv2 = (Invocable) se;
			NativeArray nArray = (NativeArray) inv2.invokeFunction("test");
			for (int i = 0; i < nArray.getLength(); i++) {
				buf.append(showStock("" + nArray.get(i, nArray)) + "\t");
				if (i % 3 == 2)
					buf.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	static String sendUrl = "http://api.jpush.cn:8800/sendmsg/sendmsg";

	public void run() {
		try {
			while (true) {
				String s = HttpClientUtil.getUrl(getStockUrl(names), "GBK");
				String jsContent = wrapFunction(names, s);
				System.out.println(doWithJs(jsContent));
				Thread.sleep(separateSecond);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
