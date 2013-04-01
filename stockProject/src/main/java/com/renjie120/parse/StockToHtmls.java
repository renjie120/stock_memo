package com.renjie120.parse;

import java.io.File;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;

import brightmoon.util.FileUtil;
import brightmoon.util.Util;

import com.renjie120.stock.NewStock;
import com.renjie120.stock.StockVO;

/**
 * 将结果输出到多个网页，在一个主网页形成连接。
 * 
 * @author wblishq
 * 
 */
public class StockToHtmls {
	private static String BTR = "<tr>";
	private static String BTABLE = "<html><body><table>";
	private static String ATABLE = "</table></body></html>";
	private static String ATR = "</tr>";
	private static String BTD = "<td>";
	private static String ATD = "</td>";

	private String filename;
	private String dirName;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public StockToHtmls(String filename, String dirName) {
		this.filename = filename;
		this.dirName = dirName;
	}

	public void showHtmls(String filename, String dirName) {
		System.out.println("来源文件:" + filename);
		System.out.println("结果目录:" + dirName);
		File myFilePath = new File(dirName);
		ArrayList stocks = new ArrayList();
		if (!myFilePath.exists()) {
			myFilePath.mkdir();
		}
		String fileStr = Util.readFile(filename, "");
		// 根据句点进行拆分字符串，得到每一个股票的信息字符串，然后进行分析。
		String[] strs = fileStr.split("[$]");
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] != null && strs[i].length() > 0) {
				NewStock stock = getStockByStr(strs[i]);
				stocks.add(stock);
				new FileUtil.WriteFileBuilder(dirName + "\\"
						+ getStockName(strs[i]) + "_" + i + ".html")
						.encoding("GBK").build()
						.writeFile(getHTMLByStockStr(stock));
			}
		}
		new FileUtil.WriteFileBuilder(dirName + "\\汇总.html").encoding("GBK")
				.build().writeFile(getSumStockHtml(stocks));

		System.out.println("保存文件完毕！");
	}

	private static String getSumStockHtml(ArrayList stockList) {
		StringBuffer buf = new StringBuffer();
		// 全部的总成本
		double allChengben = 0.0;
		// 全部的收益金额
		double allAddMoney = 0.0;
		// 计算累计的收益总金额
		double realAllAddMoney = 0.0;
		// 总的收益率
		double allAddMoneyValue = 0.0;
		buf.append("<table>");
		// 计数器，用来计算当前股票的序列号
		int num = 0;
		if (stockList != null && stockList.size() > 0) {
			Iterator it = stockList.iterator();
			while (it.hasNext()) {
				NewStock stock = (NewStock) it.next();
				double addMoney = stock.getAddMoney(stock.getEndValue());
				double chengben = stock.getChengben();
				if (stock.getStockName().indexOf("_") != -1) {
					// 累计金额的计算包含了以‘_’开头的股票
					realAllAddMoney += addMoney;
					buf.append("<tr >");
					buf.append("<td style='height:22px;border:1px solid blacsk;'>");
					buf.append("<a href='").append(stock.getStockName())
							.append("_" + num).append(".html'>")
							.append(stock.getStockName()).append("(")
							.append(stock.getEndValue()).append(")")
							.append("</a>").append("<br>")
							.append(stock.getAllBuyCost()).append("</td>");
					buf.append("<td style='height:22px;border:1px solid black;'>");
					if (addMoney > 0) {
						buf.append("<span style='color:red'>盈利：")
								.append(addMoney).append("</span><br>");
					} else {
						buf.append("<span style='color:blue'>盈利：")
								.append(addMoney).append("</span><br>");
					}
					buf.append("盈利率：")
							.append(Util.multiply(
									stock.getAddValue(stock.getEndValue()), 100))
							.append("%<br></td></tr>");
				} else {
					allChengben += chengben;
					allAddMoney += addMoney;
					// 累计金额的计算包含了以‘_’开头的股票
					realAllAddMoney += addMoney;
					buf.append("<tr >");
					buf.append("<td style='height:22px;border:1px solid blacsk;'>");
					buf.append("<a href='").append(stock.getStockName())
							.append("_" + num).append(".html'>")
							.append(stock.getStockName()).append("(")
							.append(stock.getEndValue()).append(")")
							.append("</a>").append("<br>")
							.append("成本：" + stock.getCount()).append("*")
							.append(stock.getBaoben()).append("</td>");
					buf.append("<td style='height:22px;border:1px solid black;'>");
					buf.append("总投资：").append(chengben).append("</br>");
					if (addMoney > 0) {
						buf.append("<span style='color:red'>盈利：")
								.append(addMoney).append("</span><br>");
					} else {
						buf.append("<span style='color:blue'>盈利：")
								.append(addMoney).append("</span><br>");
					}
					buf.append("总市值：")
							.append(stock.getRealMoney(stock.getEndValue()))
							.append("<br>");
					buf.append("盈利率：")
							.append(Util.multiply(
									stock.getAddValue(stock.getEndValue()), 100))
							.append("%<br></td></tr>");
				}
				num++;
			}
		}
		buf.append("<tr><td colspan='2'>")
				.append("总投资：")
				.append(allChengben)
				.append(";总收益：")
				.append(allAddMoney)
				.append(";总收益率:")
				.append(Util.multiply(Util.divide(allAddMoney, allChengben, 4,
						RoundingMode.HALF_EVEN), 100)).append("%")
				.append("</td></tr>");
		buf.append("<tr><td colspan='2'>").append("总投资：").append(allChengben)
				.append(";累计总收益：").append(realAllAddMoney);
		if (realAllAddMoney < 0) {
			buf.append(";赔本率:")
					.append(-Util.multiply(Util.divide(-realAllAddMoney,
							Util.add(allChengben, -realAllAddMoney), 4,
							RoundingMode.HALF_EVEN), 100)).append("%");
			buf.append(";距离保本的比率:")
					.append(Util.multiply(Util.divide(-realAllAddMoney,
							allChengben, 4, RoundingMode.HALF_EVEN), 100))
					.append("%");
		} else {
			buf.append(";累计总收益率:")
					.append(Util.multiply(Util.divide(realAllAddMoney,
							allChengben, 4, RoundingMode.HALF_EVEN), 100))
					.append("%");
		}
		buf.append("</td></tr>");
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * 根据字符串得到股票名称
	 * 
	 * @param stockStr
	 * @return
	 */
	private static String getStockName(String stockStr) {
		String[] stockInfoStr = splitStockInfo(stockStr);
		return stockInfoStr[0];
	}

	/**
	 * 根据字符串形成html分析结果网页形式
	 * 
	 * @param stockStr
	 * @return
	 */
	private static String getHTMLByStockStr(NewStock stock) {
		// 当前股价
		double endValue = stock.getEndValue();
		// 交易费用
		double sellCost = stock.getSellCost();
		// 股票名称
		String stockName = stock.getStockName();
		// 交易字符串的分析结果
		String ansStr = getStockInfoByStr(stock);
		StringBuffer buf = new StringBuffer();
		/********************* 连接分析字符串和结果 ********************************************************/
		buf.append(BTABLE).append(BTR).append(BTD).append("股票名称：" + stockName)
				.append(ATD).append(ATR).append(BTR).append(BTD)
				.append("<span style='color:red'>").append("当前股价：" + endValue)
				.append("</span>").append(ATD).append(ATR).append(BTR)
				.append(BTD).append("赎回费率：" + sellCost).append(ATD).append(ATR)
				.append(BTR).append(BTD).append(ansStr).append(ATD).append(ATR)
				.append(ATABLE);
		return buf.toString();
	}

	/**
	 * 根据字符串第一次拆分进行分析数据
	 * 
	 * @param stockStr
	 * @return
	 */
	private static String[] splitStockInfo(String stockStr) {
		String[] stockInfos = stockStr.split(":");
		if (stockInfos.length != 3) {
			System.out.println("文件中格式有错，请核对。");
		}
		return stockInfos;
	}

	public static NewStock getStockByStr(String stockStr) {
		String[] stockInfoStr = splitStockInfo(stockStr);
		String stockName = stockInfoStr[0];
		String operStr = stockInfoStr[1];
		// 得到第3个位置的字符串，就是最终的股票价格和交易费率。
		String[] endValueAndSellValue = stockInfoStr[2].split(",");
		double endValue = Double.parseDouble(endValueAndSellValue[0]);
		double sellCost = Double.parseDouble(endValueAndSellValue[1]);
		NewStock stock = getStockByStr(operStr, endValue, sellCost);
		stock.setStockName(stockName);
		return stock;
	}

	private static NewStock getStockByStr(String operStr, double endValue,
			double sellCost) {
		// 然后详细分析基金的操作数据信息
		// 根据‘;’拆分得到多行信息，每一行有三个数据，分别代表当前股价，购买的金额，以及费率（在进行构造函数里面自动的转换为金额！）。
		String[][] stocks = StockToHtmls.splitStrsWith2Chars(operStr, ",", ";");
		NewStock stock = new NewStock(Double.parseDouble(stocks[0][0]),
				Double.parseDouble(stocks[0][1]), "",
				Double.parseDouble(stocks[0][2]));
		for (int i = 1; i < stocks.length; i++) {
			StockVO s = new StockVO(Double.parseDouble(stocks[i][0]),
					Double.parseDouble(stocks[i][1]),
					Double.parseDouble(stocks[i][2]));
			stock.addStock(s);
		}
		stock.setSellCost(sellCost);
		stock.setEndValue(endValue);
		return stock;
	}

	/**
	 * 根据交易字符串，当前股价，交易费率分析交易字符串,返回分析结果的html表示形式
	 * 
	 * @param stock
	 * @return
	 */
	private static String getStockInfoByStr(NewStock stock) {
		double endValue = stock.getEndValue();
		// 保本股价
		double baoben = 0.0;
		// 计算估计值的数目
		int num = 20;
		// 总成本（含有交易费用）
		double chengben = 0.0;
		// 上涨价格表格(对应上涨1%，2%，3%依次类推)
		double bigValues[] = new double[num];
		// 以当前股价进行上下推断一些价格
		double nextDayBigValues[] = new double[num];
		// 以当前股价进行上下推断一些价格
		double nextDaySmallValues[] = new double[num];
		// 下跌表格(对应下跌1%，2%，依次类推)
		double smallValues[] = new double[num];
		// 每次跳跃查询的百分比
		double addStepValue = 0.005;
		double removeStepValue = -0.005;
		// 涨停10%的价格
		double zhangting = 0.0;
		// 得到保本的价格
		baoben = stock.getBaoben();
		// 得到涨停的价格
		zhangting = stock.getZhangting(baoben);
		// 得到根据成本价计算的一些涨价的价格
		bigValues = getAnyValue(baoben, addStepValue, num);
		smallValues = getAnyValue(baoben, removeStepValue, num);
		// 根据当前成交价计算的一些涨价的价格.
		nextDayBigValues = getAnyValue(endValue, addStepValue, num);
		nextDaySmallValues = getAnyValue(endValue, removeStepValue, num);
		chengben = stock.getChengben();

		// 收益
		double addMoney = stock.getAddMoney(endValue);
		/************************************* 下面是进行的画表格的部分 ****************************************************/
		StringBuilder buf = new StringBuilder();

		buf.append(BTR).append(BTD);
		buf.append("总投资是：" + chengben);
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("总实际投资(除去交易费用)是：" + stock.getRealChengben());
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("总共购买的份额：" + stock.getCount());
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("总共的面值：" + stock.getRealMoney(endValue));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("<span style='color:red'>").append("收益：" + addMoney)
				.append("</span>");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("<span style='color:red'>")
				.append("收益率：" + stock.getAddValue(endValue)).append("</span>");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("<span style='color:red'>").append("成本股价：" + baoben)
				.append("</span>");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("当前价格距离10%收益的比率："
				+ Util.multiply(Util.subtract(Util.divide(zhangting, endValue,
						3, RoundingMode.HALF_EVEN), 1), 100) + "%");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("当前价格距离保本的比率："
				+ Util.multiply(Util.subtract(Util.divide(baoben, endValue, 3,
						RoundingMode.HALF_EVEN), 1), 100) + "%");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(chengben, bigValues, addStepValue,
				"基于保本价涨幅表", "addValues", 20));
		// buf.append("10%盈利净值水平："
		// +Util.multiply(stock.getDeadLineValue(sellCost),1.1));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(chengben, smallValues, removeStepValue,
				"基于保本价跌幅表", "removeValues", 20));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(chengben, nextDayBigValues, addStepValue,
				"下一日涨幅表", "nextDayBigValues", 20, addMoney));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(chengben, nextDaySmallValues,
				removeStepValue, "下一日跌幅表", "nextDaySmallValues", 20, addMoney));
		buf.append(ATD).append(ATR);
		return buf.toString();
	}

	/**
	 * 得到一个小的table，用来展示一个表格。。。
	 * 
	 * @param chengben
	 *            总成本，包含交易费用
	 * @param values
	 *            进行计算的价格数组(例如涨价1%之后的价格,2%的价格,3%.....)
	 * @param step
	 *            每次计算的跳跃的比率,例如每次计算相差1%
	 * @param tableTitle
	 *            表格的标题字符串
	 * @param everyRowTdNum
	 *            每行显示的列数
	 * @return
	 */
	private static String anyValuesTable(double chengben, double values[],
			double step, String tableTitle, String tableId, int everyRowTdNum) {
		return anyValuesTable(chengben, values, step, tableTitle, tableId,
				everyRowTdNum, 0.0);
	}

	/**
	 * 得到一个小的table，用来展示一个表格。。。
	 * 
	 * @param chengben
	 *            总成本，包含交易费用
	 * @param values
	 *            进行计算的价格数组(例如涨价1%之后的价格,2%的价格,3%.....)
	 * @param step
	 *            每次计算的跳跃的比率,例如每次计算相差1%
	 * @param tableTitle
	 *            表格的标题字符串
	 * @param addSomeValue
	 *            在每一行的数字后面添加一个数据（例如在每次计算按照下一日的价格计算收益前面加上当日的收益数，就可以得到下一日的实际收益数）
	 * @param everyRowTdNum
	 *            每行显示的列数
	 * @return
	 */
	private static String anyValuesTable(double chengben, double values[],
			double step, String tableTitle, String tableId, int everyRowTdNum,
			double addSomeValue) {
		StringBuffer ans = new StringBuffer();
		int rows = (int) (Math.ceil(values.length / (everyRowTdNum * 1.0)));
		ans.append("<table id='").append(tableId)
				.append("' style=' border:1px solid black; font-size:13px;'>");
		ans.append("<tr><td colspan='").append(values.length).append("'>")
				.append(tableTitle).append("</td></tr>");
		int tdNum = values.length;
		int temp = 0;
		for (int ii = 0; ii < rows; ii++) {
			StringBuffer buf1 = new StringBuffer();
			StringBuffer buf2 = new StringBuffer();
			StringBuffer buf3 = new StringBuffer();
			buf1.append("<tr>");
			buf2.append("<tr>");
			buf3.append("<tr>");
			for (; temp < everyRowTdNum && tdNum > 0; temp++, tdNum--) {
				// 计算当前的比率倍数
				int index = ii * everyRowTdNum + temp;
				// 增长的比率
				double addValue = Util.multiply(step, index + 1);
				buf1.append(
						"<td style='height:22px;border-top:1px solid black;border-left:1px solid black;border-right:1px solid black;color=red'>")
						.append(Util.multiply(addValue, 100)).append("%")
						.append("</td>");
				buf2.append(
						"<td style='height:22px;border-left:1px solid black;border-right:1px solid black;'>")
						.append(values[index]).append("</td>");
				buf3.append(
						"<td style='height:22px;border-left:1px solid black;border-right:1px solid black;border-bottom:1px solid black;'>")
						.append(Util.add(Util.multiply(chengben, addValue),
								addSomeValue)).append("</td>");
			}
			temp = 0;
			buf1.append("</tr>");
			buf2.append("</tr>");
			buf3.append("</tr>");
			ans.append(buf1).append(buf2).append(buf3);
		}
		ans.append("</table>");
		return ans.toString();
	}

	/**
	 * 根据参数得到一个数组，返回一系列值。
	 * 
	 * @param baoben
	 *            保本值
	 * @param range
	 *            每次跳跃的比例,例如1%
	 * @param time
	 *            返回的数组大小
	 * @return
	 */
	private static double[] getAnyValue(double baoben, double range, int time) {
		double ans[] = new double[time];
		// 变化的比率
		double addValue = 0.0;
		for (int i = 0; i < time; i++) {
			addValue = Util.add(1, Util.multiply(range, (i + 1)));
			ans[i] = Util.multiply(addValue, baoben);
		}
		return ans;
	}

	/**
	 * 分割字符串得到相关数据。
	 * 
	 * @param str
	 * @param char1
	 * @param char2
	 * @return
	 */
	private static String[][] splitStrsWith2Chars(String str, String char1,
			String char2) {
		String[] arrs = str.split(char2);
		String[][] ans = new String[arrs.length][3];
		for (int temp = 0; temp < arrs.length; temp++) {
			// 分别代表的是基金的当前净值，购买的金额，以及费率。
			String[] strArr = arrs[temp].split(char1);
			ans[temp][0] = strArr[0];
			ans[temp][1] = strArr[1];
			ans[temp][2] = strArr[2];
		}
		return ans;
	}
}
