package com.renjie120.parse;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import brightmoon.util.concurrent.ThreadPool;
import brightmoon.web.HttpClientUtil;

/**
 * 查询得到股票的财务分析报表.
 * 
 * @author wblishq
 * 
 */
public class StockReport implements Callable<String> {
	private String stockName;
	private String dirName;
	private String downLoad = "http://table.finance.yahoo.com/table.csv?s=";

	public StockReport(String stockName, String dirName) {
		this.stockName = stockName;
		this.dirName = dirName;
	}

	public void getHistory() throws InterruptedException, ExecutionException {
		if (stockName == null || stockName.trim().equals("")) {
			throw new IllegalArgumentException("必须还有股票名称");
		}
		if (dirName == null || dirName.trim().equals("")) {
			throw new IllegalArgumentException("必须设置保存目录");
		}

		ExecutorService exec = ThreadPool.cachedThreadPool();
		long start = System.currentTimeMillis();
		Future<String> task = exec.submit(this);
		while (true) {
			if (task.get() != null) {
				long end = System.currentTimeMillis();
				System.out.println(task.get() + "---耗时：" + (end - start));
				break;
			}
			Thread.sleep(100);
		}
		exec.shutdown();
	}

	public String call() throws Exception {
		try {
			File myFilePath = new File(dirName);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
			HttpClientUtil.getFile(downLoad + stockName + ".ss", dirName
					+ File.separator + stockName + ".txt");
		} catch (Exception e) {
			e.printStackTrace();
			return "出现异常" + e.getMessage();
		}
		return "下载完毕!";
	}

}
