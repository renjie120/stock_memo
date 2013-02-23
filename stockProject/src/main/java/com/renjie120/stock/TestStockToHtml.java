package com.renjie120.stock;

import java.math.RoundingMode;

import brightmoon.util.FileUtil;
import brightmoon.util.Util;

public class TestStockToHtml {
	private static String BTR = "<tr>";
	private static String ATR = "</tr>";
	private static String BTD = "<td>";
	private static String ATD = "</td>";

	public static void main(String[] args) {
		String filename = "d:\\stock.txt";
		String desdname = "d:\\stockAns.html";
		String fileStr = Util.readFile(filename, "");
		// 根据句点进行拆分字符串，得到每一个股票的信息字符串，然后进行分析。
		String[] strs = fileStr.split("[$]");
		StringBuilder buf = new StringBuilder();
		buf.append("<html><body>");
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] != null && strs[i].length() > 0)
				buf.append("<table id='stock_").append(i).append("'>")
						.append(getStockInfoByStr(strs[i])).append("</table>")
						.append("<hr>");
		}
		buf.append("</table></body></html>");
		new FileUtil.WriteFileBuilder(desdname).encoding("GBK").build()
				.writeFile(buf.toString());
		System.out.println("保存文件到：" + desdname);
	}

	private static String getStockInfoByStr(String stockStr) {
		// 股票名称
		String stockName = "";
		// 当前股价
		double endValue = 0.0;
		// 交易费用
		double sellCost = 0.0;
		// 保本股价
		double baoben = 0.0;
		// 计算估计值的数目
		int num = 30;
		// 上涨价格表格(对应上涨1%，2%，3%依次类推)
		double bigValues[] = new double[num];
		// 下跌表格(对应下跌1%，2%，依次类推)
		double smallValues[] = new double[num];
		// 每次跳跃查询的百分比
		double addStepValue = 0.01;
		double removeStepValue = -0.01;
		// 涨停10%的价格
		double zhangting = 0.0;
		String[] stockInfos = stockStr.split(":");
		if (stockInfos.length != 3) {
			System.out.println("文件中格式有错，请核对。");
		}
		StringBuilder buf = new StringBuilder();
		buf.append(BTR).append(BTD);
		buf.append("股票名称：" + stockInfos[0]);
		buf.append(ATD).append(ATR);
		// 得到第3个位置的字符串，就是最终的基金净值和赎回的费率。
		String[] endValueAndSellValue = stockInfos[2].split(",");
		endValue = Double.parseDouble(endValueAndSellValue[0]);
		sellCost = Double.parseDouble(endValueAndSellValue[1]);
		String operStr = stockInfos[1];
		// 然后详细分析基金的操作数据信息
		// 根据‘;’拆分得到多行信息，每一行有三个数据，分别代表当前股价，购买的金额，以及费率（在进行构造函数里面自动的转换为金额！）。
		String[][] funds = TestStockToHtml.splitStrsWith2Chars(operStr, ",",
				";");
		NewStock stock = new NewStock(Double.parseDouble(funds[0][0]),
				Double.parseDouble(funds[0][1]), "",
				Double.parseDouble(funds[0][2]));
		for (int i = 1; i < funds.length; i++) {
			StockVO s = new StockVO(Double.parseDouble(funds[i][0]),
					Double.parseDouble(funds[i][1]),
					Double.parseDouble(funds[i][2]));
			stock.addStock(s);
		}
		stock.setSellCost(sellCost);
		baoben = stock.getBaoben();
		bigValues = getAnyValue(baoben, addStepValue, num);
		smallValues = getAnyValue(baoben, removeStepValue, num);
		zhangting = Util.multiply(baoben, 1.1);
		double chengben = stock.getChengben();
		/************************************* 下面是进行的画表格的部分 ****************************************************/
		buf.append(BTR).append(BTD);
		buf.append("当前股价：" + endValue);
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("赎回费率：" + sellCost);
		buf.append(ATD).append(ATR);
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
		buf.append("收益：" + stock.getAddMoney(endValue));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("收益率：" + stock.getAddValue(endValue));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("保本净值水平：" + baoben);
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append("当前价格距离10%收益的比率："
				+ Util.multiply(Util.subtract(Util.divide(zhangting, endValue,
						3, RoundingMode.HALF_EVEN), 1), 100) + "%");
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(bigValues, addStepValue, "净值增加表",
				"addValues", 12));
		// buf.append("10%盈利净值水平："
		// +Util.multiply(stock.getDeadLineValue(sellCost),1.1));
		buf.append(ATD).append(ATR);
		buf.append(BTR).append(BTD);
		buf.append(anyValuesTable(smallValues, removeStepValue, "净值警戒表",
				"removeValues", 12));
		buf.append(ATD).append(ATR);
		return buf.toString();
	}

	/**
	 * 得到一个小的table，用来展示一个表格。。。
	 * 
	 * @param values
	 * @param step
	 * @param tableTitle
	 * @param everyRowTdNum
	 *            每行显示的列数
	 * @return
	 */
	private static String anyValuesTable(double values[], double step,
			String tableTitle, String tableId, int everyRowTdNum) {
		StringBuffer ans = new StringBuffer();
		int rows = (int) (Math.ceil(values.length / (everyRowTdNum * 1.0)));
		ans.append("<table id='").append(tableId).append("'>");
		ans.append("<tr><td colspan='").append(values.length).append("'>")
				.append(tableTitle).append("</td></tr>");
		int tdNum = values.length;
		int temp = 0;
		for (int ii = 0; ii < rows; ii++) {
			StringBuffer buf1 = new StringBuffer();
			StringBuffer buf2 = new StringBuffer();
			buf1.append("<tr>");
			buf2.append("<tr>");
			for (; temp < everyRowTdNum && tdNum > 0; temp++, tdNum--) {
				// 计算当前的比率倍数
				int index = ii * everyRowTdNum + temp;
				buf1.append("<td>")
						.append(Util.multiply(Util.multiply(step, index + 1),
								100)).append("%").append("</td>");
				buf2.append("<td>").append(values[index]).append("</td>");
			}
			temp = 0;
			buf1.append("</tr>");
			buf2.append("</tr>");
			ans.append(buf1).append(buf2);
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
