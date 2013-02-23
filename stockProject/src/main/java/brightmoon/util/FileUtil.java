package brightmoon.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作工具类.
 * 
 * @author lsq
 * 
 */
public class FileUtil {
	private final String fileName;
	private final int maxRow;
	private final String encoding;
	private final String oldCoding;
	private final String newCoding;
	private final boolean back;
	private final String fileFilter;
	private final boolean create;

	private FileUtil(ReadFileBuilder builder) {
		this.fileName = builder.fileName;
		this.maxRow = builder.maxRow;
		this.encoding = builder.encoding;
		this.create = false;
		this.oldCoding = null;
		this.newCoding = null;
		this.back = false;
		this.fileFilter = null;
	}

	private FileUtil(ChangeFileCodingBuilder builder) {
		this.fileName = builder.fileName;
		this.fileFilter = builder.fileFilter;
		this.oldCoding = builder.oldCoding;
		this.newCoding = builder.newCoding;
		this.create = false;
		this.back = builder.back;
		this.maxRow = -1;
		this.encoding = builder.encoding;
	}

	private FileUtil(WriteFileBuilder builder) {
		this.maxRow = -1;
		this.fileFilter = null;
		this.fileName = builder.destName;
		this.create = builder.create;
		this.encoding = builder.encoding;
		this.oldCoding = null;
		this.newCoding = null;
		this.back = false;
	}

	public static class ReadFileBuilder {
		private final String fileName;
		private int maxRow = Integer.MAX_VALUE;
		private String encoding = "UTF-8";

		public ReadFileBuilder(String fileName) {
			this.fileName = fileName;
		}

		public ReadFileBuilder maxRow(int val) {
			this.maxRow = val;
			return this;
		}

		public ReadFileBuilder encoding(String val) {
			this.encoding = val;
			return this;
		}

		public FileUtil build() {
			return new FileUtil(this);
		}
	}

	public static class ChangeFileCodingBuilder {
		private final String fileName;
		private final String oldCoding;
		private final String newCoding;
		private String fileFilter = "*";
		private boolean back = true;
		private String encoding = "UTF-8";

		public ChangeFileCodingBuilder(String fileName, String oldCoding,
				String newCoding) {
			this.fileName = fileName;
			this.oldCoding = oldCoding;
			this.newCoding = newCoding;
		}

		public ChangeFileCodingBuilder back(boolean val) {
			this.back = val;
			return this;
		}

		public ChangeFileCodingBuilder fileFilter(String val) {
			this.fileFilter = val;
			return this;
		}

		public ChangeFileCodingBuilder encoding(String val) {
			this.encoding = val;
			return this;
		}

		public FileUtil build() {
			return new FileUtil(this);
		}
	}

	public static class WriteFileBuilder {
		private final String destName;
		private String encoding = "UTF-8";
		private boolean create = true;

		public WriteFileBuilder(String destName) {
			this.destName = destName;
		}

		public WriteFileBuilder create(boolean val) {
			this.create = val;
			return this;
		}

		public WriteFileBuilder encoding(String val) {
			this.encoding = val;
			return this;
		}

		public FileUtil build() {
			return new FileUtil(this);
		}
	}

	/**
	 * 将指定文件中的内容已每行转换为字符串数组.
	 * 
	 * @param fileName
	 * @param encoding
	 *            编码格式
	 * @return
	 */
	@SuppressWarnings("finally")
	public String[] readFile() {
		BufferedReader in;
		ArrayList<String> list = new ArrayList<String>();
		String[] result = null;
		try {
			// 定义文件读的数据流
			// in = new BufferedReader(new FileReader(fileName));
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName), encoding));
			String s;
			int _r = 1;
			while ((s = in.readLine()) != null && (_r++) <= this.maxRow) {
				list.add(s);
			}
			result = new String[list.size()];
			Iterator<String> it = list.iterator();
			int index = 0;
			while (it.hasNext()) {
				result[index++] = it.next().toString();
			}
			return result;
		} catch (FileNotFoundException e) {
			System.out.println("找不到文件！");
			throw new Exception("文件找不到！");
		} catch (IOException e) {
			System.out.println("出现异常！");
			throw new Exception("文件找不到！");
		} finally {
			return result;
		}
	}

	/**
	 * 复制文件.
	 * 
	 * @param oldFile
	 * @param newFile
	 * @throws IOException
	 */
	public static void copyFile(String oldFile, String newFile)
			throws IOException {
		// 使用FileInputStream打开一个文件输入流
		FileInputStream fis = new FileInputStream(oldFile);
		// 使用FileOutputStream打开一个文件输出流
		FileOutputStream fos = new FileOutputStream(newFile);
		// 得到文件输入流的通道
		FileChannel ifc = fis.getChannel();
		// 得到文件输出流的通道
		FileChannel ofc = fos.getChannel();
		// 分配一个字节缓冲区，大小为1024
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (true) {
			// 清空缓冲区，使其处于可接受字节状态
			buffer.clear();
			// 从文件输入流通道里读取数据，大小取决于缓冲区大小，以及文件剩余字节大小
			int i = ifc.read(buffer);
			// 如果返回值为-1表示已读取完毕
			if (i == -1) {
				break;
			}
			// 反转缓冲区，使其处于可写入通道状态
			buffer.flip();
			// 把缓冲区数据写入文件输出流通道
			ofc.write(buffer);
		}
		fis.close();
		fos.close();
	}

	/**
	 * 返回一个目录里面的全部文件地址.
	 * 
	 * @param filename
	 * @return
	 */
	public static ArrayList allFilesInDir(String filename) {
		File tempFile = new File(filename);
		ArrayList ans = new ArrayList();
		if (tempFile.isFile()) {
			ans.add(tempFile.getAbsolutePath());
		} else {
			String[] list;
			list = tempFile.list();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					Iterator it = allFilesInDir(filename + "\\" + list[i])
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
	 * 替换一个目录里面的文件中的指定字符串为新的内容.
	 * 
	 * @param dirName
	 * @param source
	 * @param target
	 */
	public static void replaceFilesAnyStr(String dirName, String source,
			String target) {
		// 得到目录下面的全部文件名.
		ArrayList allFiles = allFilesInDir(dirName);
		if (allFiles != null) {
			for (Object filenm : allFiles) {
				String name = (String) filenm;
				FileUtil util = new FileUtil.ReadFileBuilder(name).build();
				String[] contents = util.readFile();
				for (int i = 0, j = contents.length; i < j; i++) {
					contents[i] = contents[i].replace(source, target);
				}
				new FileUtil.WriteFileBuilder(name).build().writeFile(contents);
			}
		}
	}

	/**
	 * 替换一个目录里面的文件中的指定字符串为新的内容.
	 * 
	 * @param dirName
	 * @param source
	 * @param target
	 * @param encoding
	 *            编码
	 */
	public static void replaceFilesAnyStr(String dirName, String source,
			String target, String encoding) {
		// 得到目录下面的全部文件名.
		ArrayList allFiles = allFilesInDir(dirName);
		if (allFiles != null) {
			for (Object filenm : allFiles) {
				String name = (String) filenm;
				FileUtil util = new FileUtil.ReadFileBuilder(name).encoding(
						encoding).build();
				String[] contents = util.readFile();
				for (int i = 0, j = contents.length; i < j; i++) {
					contents[i] = contents[i].replace(source, target);
				}
				new FileUtil.WriteFileBuilder(name).encoding(encoding).build()
						.writeFile(contents);
			}
		}
	}

	private Pattern pattern;

	/**
	 * 将指定目录下面的文件以指定的编码方式进行转换。
	 * 
	 * @param dirName
	 * @param sourceEncoding
	 *            原来的编码格式
	 * @param targetEncoding
	 *            新的编码格式
	 * @throws Exception
	 */
	public void changeCoding() throws Exception {
		// 得到目录下面的全部文件名.
		File f = new File(fileName);
		if (fileFilter != null && !"*".equals(fileFilter))
			pattern = Pattern.compile(fileFilter);
		if (f.isDirectory()) {
			ArrayList allFiles = allFilesInDir(this.fileName);
			if (allFiles != null) {
				for (Object filenm : allFiles) {
					String fileName = filenm.toString();
					if (pattern != null) {
						Matcher matcher = pattern.matcher(fileName);
						if (!matcher.find()) {
							System.out.println("跳过:" + fileName);
							continue;
						}
					}
					if (back)
						FileUtil.copyFile(fileName, fileName + ".bak");
					System.out.println("开始转换：" + fileName + "   " + oldCoding
							+ " -> " + newCoding);
					FileUtil util = new FileUtil.ReadFileBuilder(fileName)
							.encoding(this.oldCoding).build();
					new FileUtil.WriteFileBuilder(fileName)
							.encoding(this.newCoding).build()
							.writeFile(util.readFile());
				}
			}
		} else {
			if (pattern != null) {
				Matcher matcher = pattern.matcher(fileName);
				if (!matcher.matches()) {
					System.out.println("跳过:" + fileName);
					return ;
				}
			}
			if (back && fileName.indexOf(".bak") != -1)
				FileUtil.copyFile(fileName, fileName + ".bak");
			System.out.println("开始转换：" + fileName + "   " + oldCoding + " -> "
					+ newCoding);
			FileUtil util = new FileUtil.ReadFileBuilder(fileName).encoding(
					this.oldCoding).build();
			new FileUtil.WriteFileBuilder(fileName).encoding(this.newCoding)
					.build().writeFile(util.readFile());
		}
	}

	/**
	 * 读取文本文件字符串内容.
	 * 
	 * @param templet
	 * @param encoding
	 * @return
	 */
	public String readFileAsString() {
		String templetContent = "";
		try {
			FileInputStream fileinputstream = new FileInputStream(this.fileName);
			int length = fileinputstream.available();
			byte bytes[] = new byte[length];
			fileinputstream.read(bytes);
			fileinputstream.close();
			templetContent = new String(bytes, encoding);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return templetContent;
	}

	/**
	 * 向指定文件以指定编码写入指定字符串.
	 * 
	 * @param newFile
	 * @param templetContent
	 * @param encoding
	 * @param create
	 *            不存在就创建文件.
	 * @return
	 */
	public boolean writeFile(String content) {
		boolean isSucc = false;
		try {
			File f = new File(fileName);
			if (!f.exists() && create) {
				f.createNewFile();
			} else if (!f.exists()) {
				throw new RuntimeException("文件不存在！");
			}
			FileOutputStream fout = new FileOutputStream(fileName);
			OutputStreamWriter out = new OutputStreamWriter(
					new BufferedOutputStream(fout), encoding);
			out.write(content);
			out.close();
			fout.close();
			isSucc = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSucc;
	}

	/**
	 * 追加内容到文件中去.
	 * 
	 * @param content
	 * @return
	 */
	public boolean appendFile(String content) {
		boolean isSucc = false;
		try {
			File f = new File(this.fileName);
			if (!f.exists()) {
				throw new RuntimeException("文件不存在！");
			}
			FileOutputStream fout = new FileOutputStream(fileName, true);
			OutputStreamWriter out = new OutputStreamWriter(
					new BufferedOutputStream(fout), encoding);
			out.write(content);
			out.close();
			fout.close();
			isSucc = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSucc;
	}

	/**
	 * 写文件.
	 * 
	 * @param fileName
	 * @param rows
	 * @param encoding
	 */
	public boolean writeFile(String[] contentArray) {
		OutputStreamWriter out;
		try {
			if (contentArray == null)
				return false;
			FileOutputStream fout = new FileOutputStream(this.fileName);
			out = new OutputStreamWriter(new BufferedOutputStream(fout),
					encoding);
			for (int temp = 0; temp < contentArray.length; temp++) {
				out.write(contentArray[temp].toString() + "\n");
			}
			out.close();
			fout.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("读写文件出现异常！");
		} catch (Exception e) {
			throw new RuntimeException("出现异常");
		}
	}

}
