package com.renjie120.stock;

import brightmoon.util.Util;


public class StockVO {
	//股票名称
	private String stockName;
	//股价
	private double stockValue;
	//交易份数
	private double count;
	//买入交易费
	private double buyCost;
	//卖出交易费
	private double sellCost;
	
	public StockVO()
	{}
	/**
	 * 根据交易的金额计算交易价格
	 * @param stockVlaue 交易股票的价值
	 * @param costValue 交易的费率
	 * @return
	 */
	private double getBuyCostByStockMoney(double stockMoney,double costValue){
		double temp = Util.multiply(stockMoney, costValue);
		if(temp<5){
			return 5;
		}else{
			return temp;
		}
	}
	
	public StockVO(double stockValue,double count,double buyCostValue){
		this.stockValue = stockValue;
		this.count = count;
		double temp = Util.multiply(stockValue, count);
		this.buyCost = getBuyCostByStockMoney(temp,buyCost);
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public double getStockValue() {
		return stockValue;
	}
	public void setStockValue(double stockValue) {
		this.stockValue = stockValue;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public double getBuyCost() {
		return buyCost;
	}
	public void setBuyCost(double buyCost) {
		double temp = Util.multiply(stockValue, count);
		this.buyCost = getBuyCostByStockMoney(temp,buyCost);
	}
	public double getSellCost() {
		return sellCost;
	}
	public void setSellCost(double sellCost) {
		this.sellCost = sellCost;
	}
}
