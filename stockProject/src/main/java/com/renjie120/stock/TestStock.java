package com.renjie120.stock;

import brightmoon.util.FileUtil;
import brightmoon.util.Util;


public class TestStock {
	public static void main(String[] args){
		String filename = "d:\\stock.txt";
		String desdname = "d:\\stockAns.txt";
		String fileStr = Util.readFile(filename,"");
		//根据句点进行拆分字符串，得到每一个股票的信息字符串，然后进行分析。
		String[] strs = fileStr.split("[$]");
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<strs.length;i++){
			if(strs[i]!=null&&strs[i].length()>0)
				buf.append(getStockInfoByStr(strs[i]));	
		} 
		new FileUtil.WriteFileBuilder(desdname).encoding("GBK").build().writeFile(buf.toString());
		System.out.println("保存文件到："+desdname);
	}
	
	private static String getStockInfoByStr(String stockStr){
		String[] stockInfos = stockStr.split(":");
		if(stockInfos.length!=3){
			System.out.println("文件中格式有错，请核对。");
		}
		StringBuilder buf = new StringBuilder();
		buf.append("股票名称："+stockInfos[0]+"\n");
		
		//得到第3个位置的字符串，就是最终的基金净值和赎回的费率。
		String[] endValueAndSellValue = stockInfos[2].split(",");	
		double endValue = Double.parseDouble(endValueAndSellValue[0]);
		double sellCost = Double.parseDouble(endValueAndSellValue[1]);
		buf.append("当前股价：" + endValue+"\n");
		buf.append("赎回费率：" + sellCost+"\n");
		
		String operStr = stockInfos[1];
		//然后详细分析基金的操作数据信息
		//根据‘;’拆分得到多行信息，每一行有三个数据，分别代表当前股价，购买的金额，以及费率（在进行构造函数里面自动的转换为金额！）。
		String[][] funds = TestStock.splitStrsWith2Chars(operStr, ",", ";");
		NewStock stock = new NewStock(Double.parseDouble(funds[0][0]),
				Double.parseDouble(funds[0][1]),
				"",
				Double.parseDouble(funds[0][2]));
		for(int i=1;i<funds.length;i++){
			StockVO s = new StockVO(Double.parseDouble(funds[i][0]),
					Double.parseDouble(funds[i][1]),
					Double.parseDouble(funds[i][2]));
			stock.addStock(s);
		}
		stock.setSellCost(sellCost);		
		buf.append("总投资是：" + stock.getChengben()+"\n");
		buf.append("总实际投资(除去交易费用)是：" + stock.getRealChengben()+"\n");
		buf.append("总共购买的份额：" + stock.getCount()+"\n");
		buf.append("总共的面值：" + stock.getRealMoney(endValue)+"\n");
		buf.append("收益：" + stock.getAddMoney(endValue)+"\n");
		buf.append("收益率：" +stock.getAddValue(endValue)+"\n");
		buf.append("保本净值水平：" +stock.getBaoben()+"\n");
		buf.append("10%盈利净值水平：" +Util.multiply(stock.getChengben(),1.1)+"\n");
		buf.append("-10%警戒线净值水平：" +Util.multiply(stock.getChengben(),0.9)+"\n");		
		buf.append("-------------------------------------------------------------------"+"\n");
		return buf.toString();
	}
	
	private static String[][] splitStrsWith2Chars(String str,String char1,String char2)
	{
		String[] arrs = str.split(char2);
		String[][] ans = new String[arrs.length][3];
		for (int temp = 0; temp < arrs.length; temp++) {
			//分别代表的是基金的当前净值，购买的金额，以及费率。
			String[] strArr = arrs[temp].split(char1);
			ans[temp][0] = strArr[0];
			ans[temp][1] = strArr[1];
			ans[temp][2] = strArr[2];
		}
		return ans;
	}
}
