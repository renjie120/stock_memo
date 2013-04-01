package brightmoon.util.concurrent.test;

import brightmoon.util.concurrent.Callable;
 
public class MyCallableClass implements Callable<Object> {

	@Override
	public Object call() throws Exception{
		for (int i = 0; i < 10; i++) {
			System.out.println("[" + Thread.currentThread().getName() + "]: " + i);
			Thread.sleep(2000);
		}
		return "SUCCESS";
	}

}
