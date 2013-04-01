package brightmoon.util.concurrent.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;

import brightmoon.util.concurrent.ThreadPool;

import com.renjie120.parse.MyStockSee;
import com.renjie120.parse.OracleStockHistory;
import com.renjie120.parse.StockHistory;
import com.renjie120.parse.StockReport;
import com.renjie120.parse.StockToHtmls;

public class StockTest {
	/**
	 * 测试从远处http地址下载交易记录保存到数据库.
	 * 
	 * @throws Exception
	 */
	@Test
	public void HistoryTest() throws Exception {
		StockHistory test = new StockHistory(new OracleStockHistory("601919",
				"D:\\testStock"));
//		StockHistory test1 = new StockHistory(new OracleStockHistory("600809",
//				"D:\\testStock"));
//		StockHistory test2 = new StockHistory(new OracleStockHistory("600718",
//				"D:\\testStock"));
//		StockHistory test3 = new StockHistory(new OracleStockHistory("600519",
//				"D:\\testStock"));

		long start = System.currentTimeMillis();
		final ExecutorService exec = ThreadPool.cachedThreadPool();
		Future<String> result = exec.submit(test);
//		Future<String> result1 = exec.submit(test1);
//		Future<String> result2 = exec.submit(test2);
//		Future<String> result3 = exec.submit(test3);
		System.out.println(result.get());
//		System.out.println(result1.get());
//		System.out.println(result2.get());
//		System.out.println(result3.get());
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - start));
		exec.shutdown();
	}

	/**
	 * 测试从text文件中的交易记录导出到html记录.
	 */
	// @Test
	public void StockToHtmlsTest() {
		String filename = "D:\\我的java工具包!!\\stock工具\\stock.txt";
		String dirName = "c:\\aaaa";
		StockToHtmls test = new StockToHtmls(filename, dirName);
		test.showHtmls(filename, dirName);
	}

	/**
	 * 测试定时查询股票信息.
	 */
	// @Test
	public void MyStockSeeTest() {
		Thread th = new Thread(new MyStockSee("d:\\stockname.txt", 10));
		th.start();
	}

	/**
	 * 测试查询得到股票的报表信息.
	 */
	// @Test
	public void MyStockReportTest() {
		StockReport test = new StockReport("601919", "D:\\testStock");
		try {
			test.getHistory();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
