package brightmoon.web;

import java.util.concurrent.CountDownLatch;

/**
 * 一个多线程版本的获得链接的例子.
 * 
 * @author lsq
 * 
 */
public class MultiProcess implements Runnable {
	private String url;
	private int i;
	private CountDownLatch count; 

	public MultiProcess(String url, int i, CountDownLatch count, long start) {
		this.url = url;
		this.i = i;  
		this.count = count;
	} 
	public void run() {
		try {
			String fileName = "f://file" + i + ".txt";
			HttpClientUtil.getFile(url, fileName);
			count.countDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) throws Exception {
		long s = System.currentTimeMillis();
		int testCount = 30;
		String url = "http://money.finance.sina.com.cn/corp/go.php/vFD_BalanceSheet/stockid/600031/ctrl/part/displaytype/4.phtml";
		CountDownLatch count = new CountDownLatch(testCount);
		for (int i = 0; i < testCount; i++)
			new Thread(new MultiProcess(url, i, count, s)).start();

		// 下面是统计的多线程的耗时.
		while (true) {
			if (count.getCount() < 1)
				break;
			Thread.sleep(50);
		}
		long e = System.currentTimeMillis();
		System.out.println(e - s); 
	}
}