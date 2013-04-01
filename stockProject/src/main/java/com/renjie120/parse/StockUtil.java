package com.renjie120.parse;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import sun.org.mozilla.javascript.internal.NativeArray;
import brightmoon.web.HttpClientUtil;

public class StockUtil {
	/**
	 * 判断股票号是否存在.
	 * 
	 * @param stockNo
	 * @return
	 */
	public static String getStockName(String stockNo) {
		String stockName = "sh" + stockNo;
		String temp = getInfo(stockName);
		if (temp == null) {
			stockName = "sz" + stockNo;
			temp = getInfo(stockName);
			if (temp == null)
				return "";
			else
				return (temp.split(",")[0]);
		} else {
			return (temp.split(",")[0]);
		}
	}

	/**
	 * 判断股票号是否存在，存在就返回股票的类型sz或者sh. 
	 * @param stockNo
	 * @return
	 */
	public static StockType getStockType(String stockNo) {
		String stockName = "sh" + stockNo;
		if (checkIsEmpty(stockName))
			return StockType.SH;
		else {
			stockName = "sz" + stockNo;
			if (checkIsEmpty(stockName))
				return StockType.SZ;
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static boolean checkIsEmpty(String stockName) {
		String s = "http://hq.sinajs.cn/list=s_" + stockName;
		String ss = HttpClientUtil.getUrl(s, "GBK");
		String jsContent = wrapFunction(new String[] { stockName }, ss);
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("js");
		try {
			se.eval(jsContent);
			Invocable inv2 = (Invocable) se;
			NativeArray nArray = (NativeArray) inv2.invokeFunction("test");
			for (int i = 0; i < nArray.getLength(); i++) {
				if (null == nArray.get(i, nArray)
						|| "".equals(nArray.get(i, nArray))) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 判断js里面的是否是js空对象.
	 * 
	 * @param script
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static String getInfo(String stockName) {
		String s = "http://hq.sinajs.cn/list=s_" + stockName;
		String ss = HttpClientUtil.getUrl(s, "GBK");
		String jsContent = wrapFunction(new String[] { stockName }, ss);
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("js");
		try {
			se.eval(jsContent);
			Invocable inv2 = (Invocable) se;
			NativeArray nArray = (NativeArray) inv2.invokeFunction("test");
			if (null != nArray.get(0, nArray)
					&& !"".equals(nArray.get(0, nArray))) {
				return nArray.get(0, nArray) + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(StockUtil.getStockName("601919"));
		System.out.println(StockUtil.getStockName("002019"));
		System.out.println(StockUtil.getStockName("60s1919"));
	}

	/**
	 * 封装js函数.
	 * 
	 * @param names
	 * @param str
	 * @return
	 */
	public static String wrapFunction(String[] names, String str) {
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
	 * 使用js引擎处理js方法.
	 * 
	 * @param script
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static String doWithJs(String script) {
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

	/**
	 * 对返回结果的格式化处理.
	 * 
	 * @param str
	 * @return
	 */
	private static String showStock(String str) {
		String[] strs = str.split(",");
		return strs[0].substring(0, 2) + "" + strs[1] + "\t" + strs[3];
	}

}
