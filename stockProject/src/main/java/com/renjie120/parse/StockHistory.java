package com.renjie120.parse;

import java.util.concurrent.Callable;

/**
 * 查询得到股票的历史记录，并保存到数据库.
 * 
 * @author wblishq
 * 
 */
public class StockHistory implements Callable<String> {
	private StockHistoryParse parse;

	public StockHistory(StockHistoryParse parse) {
		this.parse = parse;
	}

	public String call() throws Exception {
		try {
			long start = System.currentTimeMillis();
			parse.downLoadHistory();
			parse.saveToDb();
			long end = System.currentTimeMillis();
			System.out.println("返回耗时：" + (end - start));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			//return "出现异常" + e.getMessage();
		}
		return "下载完毕!";
	}
}
