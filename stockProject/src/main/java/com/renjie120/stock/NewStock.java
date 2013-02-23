package com.renjie120.stock;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;

import brightmoon.util.Util;

public class NewStock {
	private StockVO stock = new StockVO();
	private String stockName;
	/**
	 * 多次交易的份额
	 */
	private LinkedList counts;

	/**
	 * 多级交易时的股价
	 */
	@SuppressWarnings("rawtypes")
	private LinkedList stockValues;

	/**
	 * 多次交易时的买入费用
	 */
	private LinkedList buyCosts; 
	
	/**
	 * 保本价格
	 */
	private double baoben;
	
	/**
	 * 涨停10%价格
	 */
	private double zhangting;
	
	/**
	 * 总投资，含交易费用
	 */
	private double chengben;

	/**
	 * 实际投资，出去交易费用
	 */
	private double realChengben;
	
	/**
	 * 总购买份额
	 */
	private double count;
	
	/**
	 * 总共面值
	 */
	private double realMoney;
	
	/**
	 * 收益金额
	 */
	private double addMoney;
	
	/**
	 * 收益率
	 */
	private double addValue;
	
	/**
	 * 当前距离10%收益的比率
	 */
	private double chaValue;
	
	/**
	 * 交易费率，为了准确计算交易的金额，收益等
	 */
	private double sellCost;
	
	/**
	 * 计算股价标准，以此计算其他值的不可少条件
	 */
	private double endValue;
	
	/**
	 * 返回保本股价
	 * @return
	 */
	public double getBaoben() {
		if(sellCost>0.0)
			return getDeadLineValue(sellCost);
		else{
			System.out.println("请输入交易费率！");
			return 0.0;
		}
	}

	/**
	 * 返回涨停价格
	 * @param baoben 计算出来的保本股价
	 * @return
	 */
	public double getZhangting(double baoben) {
		return Util.multiply(baoben,1.1);	
	}

	/**
	 * 返回总共的投资金额，包含交易费用
	 * @return
	 */
	public double getChengben() {
		return getAllMoney();
	}

	/**
	 * 返回除去交易费用的实际投资金额
	 * @return
	 */
	public double getRealChengben() {
		return getRealAllMoney();
	}

	/**
	 * 返回持有的股票总份数
	 * @return
	 */
	public double getCount() {
		return Util.getSum(counts);
	}

	/**
	 * 返回总共的面值，即持有股票的市价
	 * @param 当前股价
	 * @return
	 */
	public double getRealMoney(double endValue) {
		return Util.multiply(getCount(), endValue);
	}

	/**
	 * 获得股票的收益
	 * @param 当前股价
	 * @return
	 */
	public double getAddMoney(double stockValue) {
		return Util.subtract(getRealMoney(stockValue),getAllMoney());
	}

	/**
	 * 获得股票的收益率
	 * @param 当前股价
	 * @return
	 */
	public double getAddValue(double stockValue) {
		return Util.divide(getAddMoney(stockValue),getAllMoney(), 5, RoundingMode.HALF_EVEN);
	}

	public double getChaValue() {
		return chaValue;
	}

	public NewStock(double stockValue, double count, String stockName,
			double costValue) {
		LinkedList stocklist = new LinkedList();
		stocklist.add(stockValue);
		LinkedList countList = new LinkedList();
		countList.add(count);
		double temp = Util.multiply(stockValue, count);
		double buyCost = getBuyCostByStockMoney(temp,costValue);
		LinkedList buyCostList = new LinkedList();
		buyCostList.add(buyCost);

		stock.setCount(count);
		stock.setBuyCost(buyCost);
		stock.setStockValue(stockValue);
		stock.setStockName(stockName);

		stockValues = stocklist;
		counts = countList;
		buyCosts = buyCostList;
	}
	
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

	/**
	 * 加仓股票
	 * @param vo
	 */
	public void addStock(StockVO vo) {
		if (Util.notNull(vo.getStockName())
				&& Util.notNull(vo.getStockName())) {
			if (!vo.getStockName().equals(stock.getStockName())) {
				System.out.println("增仓股票名不与此前股票名一样，不可以进行操作。");
				return;
			}
		}
		stockValues.add(vo.getStockValue());
		counts.add(vo.getCount());
		buyCosts.add(vo.getBuyCost());
		stock.setBuyCost(vo.getBuyCost());
		stock.setCount(vo.getCount());
		stock.setStockValue(vo.getStockValue());
	}
	
	/**
	 * 得到总共的花费
	 * @return
	 */
	public double getAllBuyCost() {
		return Util.getSum(buyCosts);
	}

	/**
	 * 返回投资金额，含有交易费用。
	 * @return
	 */
	private double getAllMoney() {
		double ans = 0;
		Iterator cit = counts.iterator();
		Iterator vit = stockValues.iterator();
		Iterator bit = buyCosts.iterator();
		while (cit.hasNext()) {
			double ct = Double.parseDouble(cit.next().toString());
			double vt = Double.parseDouble(vit.next().toString());
			double bt = Double.parseDouble(bit.next().toString());
			//计算公式：累加（单价*份额+购买费用）
			ans = Util.add(ans, Util.add(Util.multiply(ct, vt), bt));
		}
		return ans;
	}
	
	/**
	 * 返回实际的投资额，出去了交易费用。
	 * @return
	 */
	private double getRealAllMoney(){
		return Util.subtract(getAllMoney(), getAllBuyCost());
	}
	
	private double getDeadLineValue(double sellCost) {
		return Util.divide(Util.multiply(getAllMoney(),(1+sellCost)) ,
				getCount(),
					5,
					RoundingMode.HALF_EVEN);
	}

	/**
	 * 返回交易的费率
	 */
	public double getSellCost() {
		return sellCost;
	}

	/**
	 * 设置交易费率，便于计算收益等
	 * @param sellCost
	 */
	public void setSellCost(double sellCost) {
		this.sellCost = sellCost;
	}

	public double getEndValue() {
		return endValue;
	}

	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
}
