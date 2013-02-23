package brightmoon.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import brightmoon.util.Util;

public class JsoupUtil {
	static String LOGIN = "http://127.0.0.1:1988/NewHibernateMoney/login!login.action";
	static String queryReport = "http://127.0.0.1:1988/NewHibernateMoney/detailReport!reportDetail.action";
	static {
		String bookType = "1";
		String pass = "870420";
		Map params = new HashMap(10);
		params.put("bookType", bookType);
		params.put("pass", pass);
		HttpClientUtil.postUrlWithParams(LOGIN, params);
	}

	public static List<Record> getReport() {
		String str = HttpClientUtil.getUrl(queryReport);
		Document doc = Jsoup.parse(str);
		Element reportTable = doc.getElementById("reportTable");
		Elements heads = reportTable.getElementsByTag("thead").first()
				.getElementsByTag("th");
		List<String> types = new ArrayList<String>();
		List<Record> results = new ArrayList<Record>();
		for (int i = 2, j = heads.size(); i < j; i++) {
			Element td = heads.get(i);
			String s = td.text();
			types.add(s.substring(0, s.indexOf("(")));
		}

		Elements bodyContents = doc.getElementsByTag("tbody").first()
				.getElementsByTag("tr");
		for (Element e : bodyContents) {
			Elements tds = e.getElementsByTag("td");
			String year = "";
			String month = "";
			for (int i = 0, j = tds.size(); i < j; i++) {
				Element td = tds.get(i);
				if (i == 0) {
					year = td.getElementsByTag("a").text();
					if (Util.isEmpty(year))
						break;
				} else if (i == 1) {
					month = td.getElementsByTag("a").text();
					if (Util.isEmpty(month))
						break;
				} else {
					Record record = new Record();
					record.year = year;
					record.month = month;
					record.money = td.text();
					record.type = types.get(i - 2);
					results.add(record);
				}
			}
		}
		return results;
	}

	public static void main(String[] args) {
		// List<Record> records = JsoupUtil.getReport();
		// for (Record r : records)
		// System.out.println(r);
		System.out.println(HttpClientUtil.getUrl(queryReport));
	}
}

class Record {
	public String year;
	public String month;
	public String money;
	public String type;

	public String toString() {
		return year + "," + month + "," + money + "," + type;
	}
}
