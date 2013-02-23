package brightmoon.web;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import brightmoon.util.IllegalException;

public class HttpClientUtil {
	/**
	 * 检查是否可以理解到主机.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkCanConnect(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 使用httpClient工具进行读取网络地址.
	 * 
	 * @param url
	 * @param encoding
	 * @throws Exception
	 */
	public static String getUrl(String url, String encoding) {
		try {
			final String e = encoding;
			ContextConsole console = new ContextConsole() {
				@Override
				public void console(HttpContext localContext) {
					// 从上下文中得到HttpConnection对象
					HttpConnection con = (HttpConnection) localContext
							.getAttribute(ExecutionContext.HTTP_CONNECTION);
					// System.out.println("socket超时时间：" +
					// con.getSocketTimeout());

					// 从上下文中得到HttpHost对象
					HttpHost target = (HttpHost) localContext
							.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
					// System.out.println("最终请求的目标:" + target.getHostName() +
					// ":"
					// + target.getPort());

					// 从上下文中得到代理相关信息.
					HttpHost proxy = (HttpHost) localContext
							.getAttribute(ExecutionContext.HTTP_PROXY_HOST);
					if (proxy != null)
						System.out.println("代理主机的目标:" + proxy.getHostName()
								+ ":" + proxy.getPort());

					// System.out
					// .println("是否发送完毕:"
					// + localContext
					// .getAttribute(ExecutionContext.HTTP_REQ_SENT));

					// 从上下文中得到HttpRequest对象
					HttpRequest request = (HttpRequest) localContext
							.getAttribute(ExecutionContext.HTTP_REQUEST);
					// System.out.println("请求的版本:" +
					// request.getProtocolVersion());
					Header[] headers = request.getAllHeaders();
					// System.out.println("请求的头信息: ");
					for (Header h : headers) {
						// System.out.println(h.getName() + "--" +
						// h.getValue());
					}
					// System.out.println("请求的链接:"
					// + request.getRequestLine().getUri());

					// 从上下文中得到HttpResponse对象
					HttpResponse response = (HttpResponse) localContext
							.getAttribute(ExecutionContext.HTTP_RESPONSE);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						// System.out.println("返回结果内容编码是："
						// + entity.getContentEncoding());
						// System.out.println("返回结果内容类型是："
						// + entity.getContentType());
						result = dump(entity, e);
					}
				}
			};
			getUrl(url, encoding, console);
			return console.getResult() + "";
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
	}

	/**
	 * 提供上下文环境，供操作.使用例子见getUrl(String url, String encoding).
	 * 
	 * @param url
	 * @param encoding
	 * @param console
	 * @throws Exception
	 */
	public static void getUrl(String url, String encoding,
			ContextConsole console) {
		getUrl(url, encoding, console, false);
	}

	private static DefaultHttpClient httpclient;
	private static HttpContext context;
	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter("charset", HTTP.UTF_8);
		// 设置允许链接的做多链接数目
		ConnManagerParams.setMaxTotalConnections(params, 200);
		// 设置超时时间.
		ConnManagerParams.setTimeout(params, 10000);
		// 设置每个路由的最多链接数量是20
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		// 设置到指定主机的路由的最多数量是50
		HttpHost localhost = new HttpHost("127.0.0.1", 80);
		connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		// 设置链接使用的版本
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// 设置链接使用的内容的编码
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// 是否希望可以继续使用.
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		httpclient = new DefaultHttpClient(cm, params);
		// httpclient = new DefaultHttpClient(new
		// ThreadSafeClientConnManager());
		context = new BasicHttpContext();
		// 原子操作的一个int，方便在多线程环境下面进行操作。
		// 主要提供自增，自减等操作.
		AtomicInteger count = new AtomicInteger(1);
		context.setAttribute("count", count);

		// 在服务端设置一个保持持久连接的特性.
		// HTTP服务器配置了会取消在一定时间内没有活动的链接，以节省系统的持久性链接资源.
		httpclient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
			public long getKeepAliveDuration(HttpResponse response,
					HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (Exception e) {

						}
					}
				}
				HttpHost target = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if ("www.baidu.com".equalsIgnoreCase(target.getHostName())) {
					return 5 * 1000;
				} else
					return 30 * 1000;
			}
		});
		// 下面添加一个http协议过滤器，将里面的一个变量count用来进行计数，得到处理的次数.
		httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				AtomicInteger count = (AtomicInteger) context
						.getAttribute("count");
				Header[] h = request.getAllHeaders();
				for (Header hh : h) {
					// System.out.println(hh.getName()+",,"+hh.getValue());
				}
				// System.out.println("连接数:" + count.toString());
				request.addHeader("Count",
						Integer.toString(count.getAndIncrement()));
			}

		});
	}

	/**
	 * 是否进行重试处理.
	 * 
	 * @param url
	 * @param encoding
	 * @param console
	 * @param retryHandler
	 *            默认为false.
	 * @throws Exception
	 */
	public static void getUrl(String url, String encoding,
			ContextConsole console, boolean retryHandler) {
		// 设置为get取连接的方式.
		try {
			HttpGet get = new HttpGet(url);
			if (retryHandler) {
				HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
					public boolean retryRequest(IOException exception,
							int executionCount, HttpContext context) {
						// 如果重复请求了5次，就不再重复请求.
						if (executionCount >= 5)
							return false;
						// 如果服务器关闭了链接，继续重复请求.
						if (exception instanceof NoHttpResponseException)
							return true;

						// SSL handshake异常的时候，不进行处理
						if (exception instanceof SSLHandshakeException)
							return false;

						// HttpEntityEnclosingRequest
						HttpRequest request = (HttpRequest) context
								.getAttribute(ExecutionContext.HTTP_REQUEST);
						boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
						if (idempotent)
							return true;
						return false;
					}

				};
				httpclient.setHttpRequestRetryHandler(myRetryHandler);
			}
			// 得到返回的response.---这里会阻塞。如果请求时间长的 话，会阻塞程序执行。
			httpclient.execute(get, context);
			console.console(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
	}

	/**
	 * 返回url的内容. 与 byte[] getUrlAsBytes采用不同的方法得到结果.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getUrl(String url) {
		ContextConsole console = new ContextConsole() {
			@Override
			public void console(HttpContext localContext)
					throws IllegalException {
				try {
					HttpResponse response = (HttpResponse) localContext
							.getAttribute(ExecutionContext.HTTP_RESPONSE);
					HttpEntity entity = response.getEntity();
					if (entity != null)
						result = EntityUtils.toString(entity);
					else
						result = null;
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalException(ERROR);
				}
			}

		};
		getUrl(url, "GBK", console);
		return console.getResult() + "";
	}

	/**
	 * 关闭连接.
	 */
	public static void shutDown() {
		httpclient.getConnectionManager().shutdown();
	}

	/**
	 * 根据代理查询链接.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getUrlByProxy(String url, String hostIp, int port) {
		HttpHost proxy = new HttpHost(hostIp, port);
		// 设置代理主机.
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		ContextConsole console = new ContextConsole() {
			@Override
			public void console(HttpContext localContext)
					throws IllegalException {
				try {
					HttpResponse response = (HttpResponse) localContext
							.getAttribute(ExecutionContext.HTTP_RESPONSE);
					HttpEntity entity = response.getEntity();
					if (entity != null)
						result = EntityUtils.toString(entity);
					else
						result = null;
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalException(ERROR);
				}
			}

		};
		getUrl(url, "GBK", console);
		return console.getResult() + "";
	}

	/**
	 * 返回指定的url的字节流. 与String getUrl()采用不同的方法得到结果.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static byte[] getUrlAsBytes(String url) {
		try {
			HttpGet get = new HttpGet(url);
			// 定义一个response处理器,这里的返回类型是String
			ResponseHandler<byte[]> responseHandler = new ResponseHandler<byte[]>() {
				public byte[] handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					HttpEntity entity = response.getEntity();
					if (entity != null)
						return EntityUtils.toByteArray(entity);
					return null;
				}
			};
			return httpclient.execute(get, responseHandler, context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
	}

	public static void main(String[] args) {
		String url = "http://money.finance.sina.com.cn/corp/go.php/vFD_BalanceSheet/stockid/600031/ctrl/part/displaytype/4.phtml";
		// String url = "http://www.google.com.tw" ;
		// HttpGet get = new HttpGet(url);
		System.out.println(getUrlByProxy(url, "127.0.0.1", 8087));
		// 定义一个response处理器,这里的返回类型是String
		// ResponseHandler<String> responseHandler = new
		// ResponseHandler<String>() {
		// public String handleResponse(HttpResponse response)
		// throws ClientProtocolException, IOException {
		// HttpEntity entity = response.getEntity();
		// if (entity != null)
		// return EntityUtils.toString(entity);
		// return null;
		// }
		// };

		// for (int i = 0; i < 10; i++) {
		// HttpResponse response = httpclient.execute(get,context);
		// HttpEntity entity = response.getEntity();
		// if(entity!=null)
		// entity.consumeContent();
		//
		// }

	}

	/**
	 * 从网络下载文件.
	 * 
	 * @param url
	 * @param fileName
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void getFile(String url, String fileName) {
		byte[] charts = null;
		try {
			charts = getUrlAsBytes(url);
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(charts);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看一下url的某些状态，但是不处理内容.
	 * 
	 * @param url
	 */
	public static void peekUrl(String url) {
		HttpGet get = new HttpGet(url);
		System.out.println("查看的链接是：" + get.getURI());
		System.out.println("请求的方式是：" + get.getMethod());
		HttpResponse response;
		try {
			response = httpclient.execute(get, context);
			System.out.println("回馈状态:" + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println("反馈内容长度：" + entity.getContentLength());
				System.out.println("反馈编码:" + entity.getContentEncoding());
			}

			// 因为只是测试一下流程，这里就丢弃请求.
			get.abort();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}

	/**
	 * 请求连接的时候同时带去参数.
	 * 
	 * @param url
	 * @param params
	 * @param encoding
	 * @throws Exception
	 */
	public static String postUrlWithParams(String url, Map params,
			String encoding) {
		return postUrlWithParams(url, params, false, encoding);
	}

	/**
	 * 请求连接的时候同时带去参数.
	 * 
	 * @param url
	 * @param params
	 * @param encoding
	 * @throws Exception
	 */
	public static String postUrlWithParams(String url, Map params) {
		return postUrlWithParams(url, params, false, "UTF-8");
	}

	/**
	 * 使用post提交页面请求.
	 * 
	 * @param url
	 * @param params
	 * @param read
	 *            是否读取结果页面.
	 * @param encoding
	 * @throws Exception
	 */
	public static String postUrlWithParams(String url, Map params,
			boolean read, String encoding) {
		String result = null;
		try {
			HttpPost httpost = new HttpPost(url);
			// 添加参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null && params.keySet().size() > 0) {
				Iterator iterator = params.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Entry) iterator.next();
					nvps.add(new BasicNameValuePair((String) entry.getKey(),
							(String) entry.getValue()));
				}
			}
			HttpParams pp = new BasicHttpParams();
			pp.setParameter("charset", HTTP.UTF_8);
			httpost.setParams(pp);
			httpost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

			HttpClient cc = new DefaultHttpClient();
			cc.getParams().setParameter("charset", HTTP.UTF_8);
			HttpResponse response = cc.execute(httpost, context);
			System.out.println("结果码:"
					+ response.getStatusLine().getStatusCode());
			if (read) {
				HttpEntity entity = response.getEntity();

				// System.out.println("登录请求地址: " + response.getStatusLine()
				// + entity.getContent());
				result = dump(entity, encoding);
				// System.out.println("post产生的cookie：");
				// List<Cookie> cookies =
				// httpclient.getCookieStore().getCookies();
				// if (cookies.isEmpty()) {
				// System.out.println("无.");
				// } else {
				// for (int i = 0; i < cookies.size(); i++) {
				// System.out.println("- " + cookies.get(i).toString());
				// }
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param params
	 * @param read
	 * @throws Exception
	 */
	public static String postUrlWithParams(String url, Map params, boolean read) {
		return postUrlWithParams(url, params, read, "UTF-8");
	}

	public static String getSessionId(String url, Map params, String encoding,
			String url2) {
		try {
			HttpPost httpost = new HttpPost(url);
			// 添加参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null && params.keySet().size() > 0) {
				Iterator iterator = params.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Entry) iterator.next();
					nvps.add(new BasicNameValuePair((String) entry.getKey(),
							(String) entry.getValue()));
				}
			}
			// 设置请求的编码格式
			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			// 登录一遍
			httpclient.execute(httpost, context);
			// 然后再第二次请求普通的url即可。
			httpost = new HttpPost(url2);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			System.out.println(httpclient.execute(httpost, responseHandler,
					context));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		} finally {
			// 关闭请求

		}
		return "";
	}

	private static final String ERROR = "出现异常";

	/*
	 * 打印页面输出.
	 */
	private static String dump(HttpEntity entity, String encoding) {
		StringBuffer buf = new StringBuffer();
		try {
			InputStream in = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					encoding));
			String l = "";
			while ((l = br.readLine()) != null)
				buf.append(l + "\n");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
		return buf.toString();
	}

	/**
	 * 使用post提交一个链接请求.
	 * 
	 * @param url
	 * @param encoding
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void postUrl(String url, String encoding) {
		try {
			HttpPost post = new HttpPost(url);
			HttpResponse response = httpclient.execute(post, context);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println("内容编码是：" + entity.getContentEncoding());
				System.out.println("内容类型是：" + entity.getContentType());
				dump(entity, encoding);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
	}

	/**
	 * 返回一个url的访问状态.
	 * 
	 * @param url
	 * @type get:0 post:1
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String getStauts(String url, int type) {
		// HttpGet和HttpPost都是HttpUriRequest的实现！！
		try {
			HttpUriRequest get = null;
			if (type == 0)
				get = new HttpGet(url);
			else
				get = new HttpPost(url);
			HttpResponse response = httpclient.execute(get, context);
			return response.getStatusLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalException(ERROR);
		}
	}
}
