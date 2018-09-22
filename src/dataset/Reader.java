/**
 * �������ڽ������ݼ���ÿһ�С�
 * @author liuchen
 */

package dataset;

import java.util.List;
import java.util.Arrays;

public class Reader {
	private String line_format;
	private String separator;
	
	/**
	 * ���캯��
	 * @param line_format �ļ��ĸ�ʽ "user item rating [timestamp]"
	 * @param separator �ֶεķָ���
	 */
	public Reader(String line_format, String separator) {
		this.line_format = line_format;
		this.separator = separator;
	}
	
	/**
	 * ����һ��
	 * @param line һ����������
	 * @return ���ذ�["user", "item", "rating", "timestamp"]���е���������
	 */
	public String[] parse_line(String line) {
		List<String> list = Arrays.asList(this.line_format.split(" "));
		String[] spilted_line = line.split(this.separator);
		String[] rs = new String[4];
		
		if (list.contains("timestamp")) {
			String[] standard_format = {"user", "item", "rating", "timestamp"};
			for(int i = 0; i < 4; i++) {
				rs[i] = spilted_line[list.indexOf(standard_format[i])].trim();
			}
			
		}else {
			String[] standard_format = {"user", "item", "rating"};
			for(int i = 0; i < 3; i++) {
				rs[i] = spilted_line[list.indexOf(standard_format[i])];
			}
			rs[3] = null;
		}
		return rs;
	}
	
	public static void main(String[] args) {
		Reader reader = new Reader("item rating user", "\t");
		String[] aa = reader.parse_line("a 	b	1");
		for(int i=0;i<4;i++) {
			System.out.println(aa[i]);
		}
	}
}
