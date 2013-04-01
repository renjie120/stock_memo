/**
 * 
 */
package brightmoon.util.concurrent.test;

import brightmoon.util.concurrent.Callable;
import brightmoon.util.concurrent.Future;
import brightmoon.util.concurrent.MyExecutorService;
 
 

/**
 * Main Test Class
 * @author wangcz
 *
 */
public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyExecutorService es = MyExecutorService.newInstance();
		Callable<Object> task = new MyCallableClass();
		Future<Object> future = es.submit(task);
		System.out.println("return of thread : " + future.get());
		System.out.println("Test Success!");
		
	}

}
