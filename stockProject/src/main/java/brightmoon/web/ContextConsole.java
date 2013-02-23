package brightmoon.web;

import org.apache.http.protocol.HttpContext;

import brightmoon.util.IllegalException;

/**
 * 模拟的一个应用请求的对象. 包含：登录，get请求，post请求. 
 * @author lsq
 * 
 */
public abstract class ContextConsole {
	protected static Object result;

	public Object getResult() {
		return result;
	}

	public abstract void console(HttpContext localContext) throws IllegalException;
}