package com.renjie120.parse;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import brightmoon.jdbc.DbUtil;
import brightmoon.util.DbcpUtil;
import brightmoon.util.FileUtil;
import brightmoon.web.HttpClientUtil;

/**
 * 查询得到股票的历史记录，并保存到数据库.
 * 
 * @author wblishq
 * 
 */
public class OracleStockHistory implements StockHistoryParse {
	protected String stockName;
	protected String dirName;
	private String downLoad = "http://table.finance.yahoo.com/table.csv?s=";
	private DbUtil util;

	public OracleStockHistory(String stockName, String dirName) {
		this.stockName = stockName;
		this.dirName = dirName;
		util = DbcpUtil.getInstance();
	}

	@Override
	public String downLoadHistory() {
		try {
			System.out.println("开始下载股票：" + stockName);
			dirName += File.separator + stockName;
			File myFilePath = new File(dirName);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
			if (StockUtil.getStockType(stockName) == StockType.SH)
				HttpClientUtil.getFile(downLoad + stockName + ".ss", dirName
						+ File.separator + stockName + ".txt");
			else if (StockUtil.getStockType(stockName) == StockType.SZ)
				HttpClientUtil.getFile(downLoad + stockName + ".sz", dirName
						+ File.separator + stockName + ".txt");
			else
				System.out.println("请输入正确的股票号!");
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@Override
	public void saveToDb() {
		// DbUtil util = new MyDbUtil();
		String[] contents = null;
		contents = new FileUtil.ReadFileBuilder(dirName + File.separator
				+ stockName + ".txt").build().readFile();
		int temp = 0;
		for (String c : contents) {
			parseStatement(temp++, c);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parseStatement(int rowNum, String content) {
		if (rowNum > 0) {
			String[] c = content.split(",");
			List arg = new ArrayList();
			arg.add(stockName);
			arg.add(c[0]);
			arg.add(c[1]);
			arg.add(c[2]);
			arg.add(c[3]);
			arg.add(c[4]);
			arg.add(c[5]);
			arg.add(c[6]);
			try {
				util.updateRecords(
						"INSERT INTO STOCK_HISTORY (STOCK_NO,STOCK_DATE,OPEN,HIGH,LOW,CLOSE,VALUME,ADJ_CLOSE) VALUES  "
								+ "(?,TO_DATE(?,'yyyy-MM-dd'),?,?,?,?,?,?)",
						arg);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("数据库异常");
			}

		}
	}
}
