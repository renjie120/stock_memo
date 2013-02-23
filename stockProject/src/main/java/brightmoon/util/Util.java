package brightmoon.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set; 

/**
 * 工具类.
 * 
 * @author wblishq
 * 
 */
public class Util {
	public static void main(String[] args) {
		String inputString = "2撒旦";
		inputString = inputString.replaceAll("^[\\u4e00-\\u9fa5]+", "").trim();
		System.out.println(inputString);
	}

	private static DecimalFormat df = new DecimalFormat("#######.####");

	public static void p(String str) {
		System.out.println(str);
	}

	public static String notBlank(Object obj) {
		if (obj == null)
			return "";
		return obj.toString();
	}

	/**
	 * 得到文件的后缀名.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null)
			return null;
		int lastIndexOfDot = fileName.lastIndexOf('.');
		int fileNameLength = fileName.length();
		final String extension = fileName.substring(lastIndexOfDot + 1,
				fileNameLength);
		return extension;
	}

	public static void printStrArr(String[][] result) {
		int i = result.length;
		for (int j = 0; j < i; j++) {
			for (int k = 0, m = result[j].length; k < m; k++) {
				System.out.println(j + ",," + k + ",,val = " + result[j][k]);
			}
		}

	}

	public static void printStrArr(String[] result) {
		int i = result.length;
		for (int j = 0; j < i; j++) {
			System.out.println(j + ",val = " + result[j]);
		}

	}

	public final static String CONFIG = "struts.xml";
	private static Properties prop = new Properties(); 

	/**
	 * 将字符串写进文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param contant
	 *            要写入文件的字符串
	 */
	// public static void writeFile(String fileName, String contant) {
	// PrintWriter out;
	// try {
	// File file = new File(fileName);
	// out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
	// out.print(contant);
	// out.close();
	// } catch (IOException e) {
	// System.out.println("读写文件出现异常！");
	// } catch (Exception e) {
	// System.out.println("出现异常");
	// }
	// }
	//
	// /**
	// * 将数组写入到文件中，数组中每个元素为一行
	// *
	// * @param fileName
	// * @param rows
	// */
	// public static void writeFile(String fileName, Object[] rows) {
	// PrintWriter out;
	// try {
	// out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
	// for (int temp = 0; temp < rows.length; temp++) {
	// out.println(rows[temp]);
	// }
	// out.close();
	// } catch (IOException e) {
	// System.out.println("读写文件出现异常！");
	// } catch (Exception e) {
	// System.out.println("出现异常");
	// }
	// }
	//
	// /**
	// * 将指定文件中的内容已每行转换为字符串数组
	// *
	// * @param fileName
	// * @return
	// */
	// public static String[] readFileToStrArr(String fileName) {
	// BufferedReader in;
	// ArrayList list = new ArrayList();
	// String[] result = null;
	// try {
	// // 定义文件读的数据流
	// in = new BufferedReader(new FileReader(fileName));
	// String s;
	// while ((s = in.readLine()) != null) {
	// list.add(s);
	// }
	// result = new String[list.size()];
	// Iterator it = list.iterator();
	// int index = 0;
	// while (it.hasNext()) {
	// result[index++] = it.next().toString();
	// }
	// return result;
	// } catch (FileNotFoundException e) {
	// System.out.println("找不到文件！");
	// throw new Exception("文件找不到！");
	// } catch (IOException e) {
	// System.out.println("出现异常！");
	// throw new Exception("文件找不到！");
	// } finally {
	// return result;
	// }
	// }

	/**
	 * 返回字符串的非空形式，如果空就返回""
	 * 
	 * @param oldStr
	 * @return
	 */
	public static String notBlank(String oldStr) {
		if (isBlank(oldStr)) {
			return "";
		}
		return oldStr;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean notNull(String str) {
		return str != null && !"".equals(str);
	}

	/**
	 * 将字符串转换为非空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String changeToNotNull(String str) {
		if (!notNull(str)) {
			return "";
		}
		return str;
	}

	/**
	 * 将字符串转换为非空字符串
	 * 
	 * @param str
	 * @param val
	 *            默认的值
	 * @return
	 */
	public static String changeToNotNull(String str, String val) {
		if (!notNull(str)) {
			return val;
		}
		return str;
	}

	/**
	 * 返回字符串的非空形式，如果是空就返回默认的字符串形式
	 * 
	 * @param oldStr
	 * @param defaultStr
	 * @return
	 */
	public static String notBlank(String oldStr, String defaultStr) {
		if (isBlank(oldStr)) {
			return defaultStr;
		}
		return oldStr;
	}

	/**
	 * 得到属性文件中的对应的参数值.
	 * 
	 * @param key
	 *            属性名
	 * @param fileName
	 *            文件地址
	 * @return
	 */
	public static String getProperty(String key, String fileName) {
		try {
			FileInputStream in = new FileInputStream(fileName);
			prop.load(in);
			in.close();
		} catch (Exception e) {
			System.out.println("Error of create input stream");
		}
		String value = "";
		try {
			value = prop.getProperty(key);
			value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			error("Util--getProperty:", e);
		}
		return value;
	}
	
	public static void error(String str,Exception e){
		System.out.println(str);
	}

	/**
	 * 根据列表集合返回符合表格数据的json串,字段顺序是根据反射java形成,是不变的.
	 * 
	 * @param dataList
	 *            表格数据的集合
	 * @param total
	 *            数据的总行数
	 * @param page
	 *            当前的页数
	 * @return
	 */
	public static String getGridJsonStr(List dataList, long total, int page) {
		StringBuffer buf = new StringBuffer();
		if (total == 0)
			page = 0;
		buf.append("{page:").append(page).append("\n,total:").append(total)
				.append("\n,rows:[");
		if (dataList != null && dataList.size() > 0) {
			Iterator it = dataList.iterator();
			int rowNum = 1;
			while (it.hasNext()) {
				buf.append("{");
				Map allProperties = getAllProperties(it.next());
				Set entrySet = allProperties.entrySet();
				Iterator iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					Object value = entry.getValue();
					Object key = entry.getKey();
					buf.append(key).append(":'")
							.append(Util.notBlankStr(value)).append("',");
				}
				buf.deleteCharAt(buf.lastIndexOf(",")).append("},\n");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
		} else {
			buf.append("{'id':null,'name':null}");
		}
		buf.append("]}");
		return buf.toString();
	}

	/**
	 * 得到ext的表格的数据s
	 * 
	 * @param dataList
	 * @param total
	 * @param page
	 * @return
	 */
	public static String getExtGridJsonStr(List dataList, int total, int page) {
		StringBuffer buf = new StringBuffer();
		if (total == 0)
			page = 0;
		buf.append("{totalNum:").append(total).append("\n,root:[");
		if (dataList != null) {
			Iterator it = dataList.iterator();
			int rowNum = 1;
			while (it.hasNext()) {
				buf.append("{");
				Map allProperties = getAllProperties(it.next());
				Set entrySet = allProperties.entrySet();
				Iterator iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					Object value = entry.getValue();
					Object key = entry.getKey();
					buf.append(key).append(":'").append(value).append("',");
				}
				buf.deleteCharAt(buf.lastIndexOf(",")).append("},\n");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
		} else {
			buf.append("{}");
		}
		buf.append("]}");
		return buf.toString();
	}

	/**
	 * 得到分页查询的sql语句
	 * 
	 * @param queryStr
	 *            原始sql语句
	 * @param start
	 *            起始行(最小为1)
	 * @param end
	 *            终止行
	 * @return
	 */
	public static String getRealQuerySql(String queryStr, int start, int end) {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT *																						");
		buf.append("  FROM (SELECT T_T_T.*, ROWNUM ROWCOUNT             ");
		buf.append("          FROM (").append(queryStr).append(") T_T_T  ");
		buf.append("         WHERE ROWNUM < ").append(end).append(")    ");
		buf.append(" WHERE ROWCOUNT >= ").append(start);
		return buf.toString();
	}

	/**
	 * 在形成ext表格的时候得到分页的相关数组信息.
	 * 
	 * @param start
	 *            起始位置
	 * @param limit
	 *            每页的行数
	 * @param total
	 *            总行数
	 * @return [起始位置，终止位置，当前页数]
	 */
	public static int[] getStartAndEnd(int start, int limit, int total) {
		return new int[] { start, start + limit, start / limit + 1 };
	}

	/**
	 * 判断是否为空字符串
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 如果不为空返回true
	 */
	public static boolean isNotBlank(String str) {
		return (str != null && !"".equals(str)) ? true : false;
	}

	/**
	 * 判断是否为空字符串
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 如果为空返回true
	 */
	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}

	/**
	 * 判断对象是否为空，空就返回true
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isBlank(Object obj) {
		return !isNotBlank(obj);
	}

	/**
	 * 判断对象是否为非空，非空就返回true
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotBlank(Object obj) {
		return obj != null;
	}

	/**
	 * 返回对象的非空字符串形式
	 * 
	 * @param obj
	 * @param defaultStr
	 * @return
	 */
	public static String notBlankStr(Object obj, String defaultStr) {
		if (isBlank(obj) || "".equals(obj))
			return defaultStr;
		return obj.toString();
	}

	/**
	 * 返回非空字符串，是空就返回‘’
	 * 
	 * @param obj
	 * @return
	 */
	public static String notBlankStr(Object obj) {
		if (isBlank(obj))
			return "";
		return obj.toString();
	}

	/**
	 * 判断是否为空字符串(包括空格)
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 如果不为空返回true
	 */
	public static boolean isNotEmpty(String str) {
		return (str != null && !"".equals(str.trim())) ? true : false;
	}

	/**
	 * 判断是否为空字符串(包括空格)
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 如果为空返回true
	 */
	public static boolean isEmpty(String str) {
		return !isNotEmpty(str);
	}

	/**
	 * 字符串比较
	 * 
	 * @param src
	 * @param des
	 * @return
	 */
	public static boolean equals(String src, String des) {
		if (src == null)
			return (des == null ? true : false);
		if (des == null)
			return (src == null ? true : false);
		return src.equals(des);
	}

	/**
	 * 将String数组变成","号间隔的字符串
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 如果为空返回true
	 */
	public static String StringArrayToString(String[] str) {
		StringBuilder sb = new StringBuilder();
		if (str != null && str.length > 0) {
			for (String s : str) {
				if (s != null) {
					sb.append(s + ",");
				}
			}
			if (sb.length() == 0)
				return "";
			return sb.substring(0, sb.length() - 1).toString();
		}
		return str[0];
	}

	/**
	 * 判断URL后缀是否为.action,如果是的话，提取actionName
	 * 
	 * @param servletPath
	 *            request.getServletPath()
	 * @return actionName
	 */
	public static String parseServletPath(String servletPath, String fiexdStr) {
		fiexdStr = "." + fiexdStr;
		if (null != servletPath && !"".equals(servletPath)) {
			if (servletPath.contains(fiexdStr)) {
				String actionName = servletPath.substring(
						servletPath.lastIndexOf("/") + 1,
						servletPath.indexOf(fiexdStr));
				return actionName;
			}
		}
		return "";
	}

	/**
	 * 根据文件名得到所属的目录名.
	 * 
	 * @param filename
	 * @return
	 */
	public static String getDirName(String filename) {
		String ans = null;
		if (null != filename && !"".equals(filename)) {
			ans = filename.substring(0, filename.lastIndexOf("\\"));
		}
		return ans;
	}

	/**
	 * 使用反射机制进行设置值.
	 * 
	 * @param o
	 *            对象
	 * @param name
	 *            要设置的属性
	 * @param value
	 *            要设置的value
	 */
	public static void setPro(Object o, String name, String value) {
		PropertyDescriptor[] props;
		try {
			props = Introspector.getBeanInfo(o.getClass(), Object.class)
					.getPropertyDescriptors();
			for (int temp = 0; temp < props.length; temp++) {
				if (name.equals(props[temp].getName())) {
					try {
						props[temp].getWriteMethod().invoke(o, value);
					} catch (Exception e) {
					}
					break;
				}
			}
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 使用反射机制进行设置值.
	 * 
	 * @param o
	 *            对象
	 * @param name
	 *            要设置的属性
	 * @param value
	 *            要设置的value
	 */
	public static void setPro(Object o, String name, Object value) {
		PropertyDescriptor[] props;
		try {
			props = Introspector.getBeanInfo(o.getClass(), Object.class)
					.getPropertyDescriptors();
			for (int temp = 0; temp < props.length; temp++) {
				if (name.equals(props[temp].getName())) {
					try {
						props[temp].getWriteMethod().invoke(o, value);
					} catch (Exception e) {
					}
					break;
				}
			}
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 得到指定对象的指定属性值.
	 * 
	 * @param o
	 *            对象
	 * @param name
	 *            属性名
	 * @return
	 */
	public static String getPro(Object o, String name) {
		String result = "";
		PropertyDescriptor[] props;
		try {
			props = Introspector.getBeanInfo(o.getClass(), Object.class)
					.getPropertyDescriptors();
			for (int temp = 0; temp < props.length; temp++) {
				if (name.equals(props[temp].getName())) {
					try {
						result = props[temp].getReadMethod().invoke(o)
								.toString();
					} catch (Exception e) {
					}
					break;
				}
			}
			return result;
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取指定文件的内容，返回文本字符串
	 * 
	 * @param fileName
	 *            文件名
	 * @param linkChar
	 *            换行符号
	 * @return
	 */
	public static String readFile(String fileName, String linkChar) {
		StringBuffer sb = new StringBuffer();
		BufferedReader in;
		String result = "";
		try {
			// 定义文件读的数据流
			in = new BufferedReader(new FileReader(fileName));
			String s;
			while ((s = in.readLine()) != null) {
				sb.append(s);
				// 定义每一行的数据读取之后采用美元连接
				sb.append(linkChar);
			}
			in.close();
			int i = linkChar.length();
			result = sb.toString();
			result = result.subSequence(0, sb.length() - i).toString();
		} catch (FileNotFoundException e) {
			System.out.println("找不到文件！");
			throw new Exception("文件找不到！");
		} catch (IOException e) {
			System.out.println("出现异常！");
			throw new Exception("文件找不到！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("出现异常！");
			throw new Exception("文件找不到！");
		} finally {
			return result;
		}
	}

	/**
	 * 返回一个map的字符串形式
	 * 
	 * @param map
	 * @return
	 */
	public static String getStrFromMap(Map map) {
		StringBuffer buf = new StringBuffer();
		if (map != null) {
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				String v = it.next().toString();
				buf.append("['").append(v).append("',").append(map.get(v))
						.append("]").append(",");
			}
			buf.deleteCharAt(buf.length() - 1);
			return buf.toString();
		}
		return "";
	}

	/**
	 * 返回一个目录下面的全部的jsp页面地址
	 * 
	 * @param filename
	 * @return
	 */
	public static ArrayList seeAllJspFile(String filename) {
		File tempFile = new File(filename);
		ArrayList ans = new ArrayList();
		if (tempFile.isFile() && tempFile.getName().endsWith(".jsp")) {
			ans.add(tempFile.getAbsolutePath());
		} else {
			String[] list;
			list = tempFile.list();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					Iterator it = seeAllJspFile(filename + "\\" + list[i])
							.iterator();
					while (it.hasNext()) {
						ans.add(it.next());
					}
				}
			}
		}
		return ans;
	}

	/**
	 * 根据规则生成自己的sql字符串
	 * 
	 * @param str
	 *            例如：select * from moneys
	 * @param start
	 * @param end
	 * @param whereStr
	 *            例如：mid<100 and desc = "支出"
	 * @param sortStr
	 *            例如：order by mid,mtime desc
	 * @return
	 */
	public static String getQueryStr(String str, int start, int end,
			String whereStr, String sortStr) {
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append("select *									");
		sqlbf.append("  from (select t2_t2_t2.*, ROWNUM ROWCOUNT                                ");
		sqlbf.append("          from (select t1_t1_t1.*                                         ");
		sqlbf.append("                  from (" + str
				+ ")t1_t1_t1 where 1=1        ");
		if (notNull(whereStr)) {
			sqlbf.append(" and " + whereStr);
		}
		if (notNull(sortStr)) {
			sqlbf.append(sortStr);
		}
		sqlbf.append("  ) t2_t2_t2                                 ");
		sqlbf.append("         where ROWNUM < " + end
				+ ")                                               ");
		sqlbf.append(" where ROWCOUNT >= " + start);
		return sqlbf.toString();
	}

	/**
	 * 打印一个对象里面的全部的get方法.
	 * 
	 * @param o
	 */
	public static void getAllGets(Object o) {
		Method[] method = o.getClass().getMethods();
		try {
			for (int i = 0; i < method.length; i++) {
				// 如果方法名是含有get的名称，而且是返回的string类型，以及参数个数为空，就调用该方法。
				if (method[i].getName().indexOf("get") != -1
						&& method[i].getGenericReturnType().toString()
								.indexOf("String") != -1
						&& method[i].getGenericParameterTypes().length == 0) {
					System.out.println(i + method[i].getName() + "():\n"
							+ method[i].invoke(o, null));
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到一个对象的返回string的全部方法.
	 * 
	 * @param o
	 */
	public static List<String> getAllMethods(Object o) {
		Method[] method = o.getClass().getMethods();
		List<String> methods = new ArrayList();
		try {
			for (int i = 0; i < method.length; i++) {
				// 如果方法名是含有get的名称，而且是返回的string类型，以及参数个数为空，就调用该方法。
				if (method[i].getGenericReturnType().toString()
						.indexOf("String") != -1
						&& method[i].getGenericParameterTypes().length == 0) {
					methods.add(method[i].getName());
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return methods;
	}

	/**
	 * 得到一个对象的全部属性值. 
	 * @param o
	 * @return 属性名和属性值的映射
	 */
	public static Map getAllProperties(Object o) {
		Map ans = new HashMap();
		PropertyDescriptor[] props;
		try {
			props = Introspector.getBeanInfo(o.getClass(), Object.class)
					.getPropertyDescriptors();
			for (int temp = 0; temp < props.length; temp++) {
				String result = null;
				if (props[temp].getReadMethod().invoke(o) != null)
					result = props[temp].getReadMethod().invoke(o).toString();
				ans.put(props[temp].getName(), result);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return ans;
	}

	/**
	 * 返回一个对象的全部属性值.
	 * 
	 * @param o
	 * @return
	 */
	public static String getJsonFromObject(Object o) {
		StringBuffer buf = new StringBuffer();
		PropertyDescriptor[] props;
		try {
			props = Introspector.getBeanInfo(o.getClass(), Object.class)
					.getPropertyDescriptors();
			for (int temp = 0; temp < props.length; temp++) {
				if (props[temp].getReadMethod().invoke(o) != null) {
					String result = props[temp].getReadMethod().invoke(o)
							.toString();
					buf.append("'").append(props[temp].getName()).append("':'")
							.append(result).append("',");
				}
			}
			if (buf.length() > 0)
				buf = buf.deleteCharAt(buf.length() - 1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 返回对象中设置了get方法的属性的集合,注意集合的成员是Field对象!
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Field> allAttrWithGetMethod(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Method[] methods = obj.getClass().getMethods();
		List result = new ArrayList();
		for (Field field : fields) {
			String fieldName = field.getName();
			String upperFirstLetter = fieldName.substring(0, 1).toUpperCase();
			String getMethodName = "get" + upperFirstLetter
					+ fieldName.substring(1);
			for (Method method : methods) {
				if (Util.equals(getMethodName, method.getName())) {
					result.add(field);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 得到一个bean里面的全部的设置了get方法的属性.
	 * 
	 * @param obj
	 * @return 集合中的成员是属性名.
	 */
	public static List<Field> allAttrsWithGetMethods(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Method[] methods = obj.getClass().getMethods();
		List result = new ArrayList();
		for (Field field : fields) {
			String fieldName = field.getName();
			String upperFirstLetter = fieldName.substring(0, 1).toUpperCase();
			String getMethodName = "get" + upperFirstLetter
					+ fieldName.substring(1);
			for (Method method : methods) {
				if (Util.equals(getMethodName, method.getName())) {
					result.add(fieldName);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 使用反射执行指定对象的无参方法,返回结果是一个对象.
	 * 
	 * @param obj
	 *            对象
	 * @param methodName
	 *            方法名
	 * @return
	 * @throws MVCException
	 */
	public static Object invoke(Object obj, String methodName) throws Exception {
		try {
			// 如果没有配置method参数就默认的使用execute()方法。
			if (Util.isEmpty(methodName))
				methodName = "execute";
			Method method = obj.getClass().getMethod(methodName, null);
			return method.invoke(obj, null);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 判断参数2字符串是否与参数1字符串相等
	 * 
	 * @param str1
	 *            被比较的字符串
	 * @param str2
	 *            比较的字符串
	 * @return boolean
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1.toLowerCase().equals(str2.toLowerCase());
	}

	/**
	 * 统计总和函数。
	 * 
	 * @return
	 */
	public static double getSum(List l) {
		double ans = 0;
		Iterator mit = l.iterator();
		while (mit.hasNext()) {
			ans = Util.add(Double.parseDouble(mit.next().toString()), ans);
		}
		return ans;
	}

	/**
	 * 计算平均数函数。
	 * 
	 * @param l
	 * @return
	 */
	public static double getAvg(List l) {
		double ans = 0;
		ans = Util.divide(getSum(l), l.size(), 2, RoundingMode.HALF_UP);
		return ans;
	}

	/**
	 * 转换日期为指定日期格式的字符串
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String dateToStr(Date date, String formatStr) {
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		return df.format(date);
	}

	/**
	 * 转换日期字符串到日期。
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param formatStr
	 *            格式化字符串
	 * @return
	 * @throws ParseException
	 */
	public static Date strToDate(String dateString, String formatStr)
			throws ParseException {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat(formatStr);
		Date timeDate = dateFormat.parse(dateString);// util类型
		return timeDate;
	}

	/**
	 * 减法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double subtract(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return Double.parseDouble(df.format(b1.subtract(b2).doubleValue()));
	}

	/**
	 * 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double multiply(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return Double.parseDouble(df.format(b1.multiply(b2).doubleValue()));
	}

	/**
	 * 加法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return Double.parseDouble(df.format(b1.add(b2).doubleValue()));
	}

	/**
	 * 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            精确度
	 * @param mode
	 *            舍入方式
	 * @return
	 */
	public static double divide(double d1, double d2, int scale,
			RoundingMode mode) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return Double.parseDouble(df.format(b1.divide(b2, scale, mode)
				.doubleValue()));
	}

	/**
	 * 除法
	 * 
	 * @param d1
	 *            被除数
	 * @param d2
	 *            除数
	 * @param scale
	 *            小数点精度
	 * @return
	 */
	public static double divide(double d1, double d2, int scale) {
		return divide(d1, d2, scale, RoundingMode.HALF_UP);
	}

}
