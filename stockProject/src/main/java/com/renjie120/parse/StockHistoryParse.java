package com.renjie120.parse;

/**
 * 解析股票历史记录的接口.
 * @author lsq
 *
 */
public interface StockHistoryParse {

	/**
	 * 下载历史数据
	 * @return
	 */
	public String downLoadHistory();

	/**
	 * 保存到数据库.
	 */
	public void saveToDb();

	/**
	 * 解析一行数据的具体操作.
	 * @param rowNum 行数.
	 * @param content 行的内容
	 */
	public void parseStatement(int rowNum,String content);
}
