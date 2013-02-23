package brightmoon.web;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 一个多线程环境下面安全的保存一个可用的httpClient--还可以保存其他的内容. 
 * @author lsq
 * 
 */
public class SessionContext {
	public static final String HTTP_CLIENT = "httpClient";
	private static ThreadLocal<SessionContext> sessionContext = new ThreadLocal<SessionContext>();

	public static SessionContext getContext() {
		return (SessionContext) sessionContext.get();
	}

	public static void setContext(SessionContext context) {
		sessionContext.set(context);
	}

	private Map<String, Object> context = null;

	/**
	 * 使用一个map初始化缓存容器.
	 * 
	 * @param context
	 */
	public SessionContext(Map<String, Object> context) {
		this.context = context;
	}

	/**
	 * 返回容器里面的全部的内容.
	 * 
	 * @return
	 */
	public Map<String, Object> getContextMap() {
		return context;
	}

	/**
	 * 返回容器里面的指定的一个内容.
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * 返回会话里面的httpClient.
	 * 
	 * @return
	 */
	public HttpClient getHttpClient() {
		return (DefaultHttpClient) context.get(HTTP_CLIENT);
	}
}
