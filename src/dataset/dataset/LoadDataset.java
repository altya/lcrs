/**
 * �������ڼ������ݼ���
 * ���಻Ӧ�ñ�ʵ���������з���Ϊ��̬��������ͨ���������á�
 * @author liuchen
 */

package dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadDataset {
	
	/**
	 * ���������ݼ��м�������
	 * @param name �������ݼ����ƣ���ѡ����"ml-100k"
	 * @return DatasetToTrainset����
	 */
	public static Dataset load_builtin(String name) {
		String file_path = null;
		Reader reader = null;
		if (name.equals("ml-100k")) {
			file_path = "resource/u.data";
			reader = new Reader("user item rating timestamp", "\t");
		}
		return load_from_file(file_path, reader);
	}
	
	/**
	 * �ӱ����ļ�ϵͳ�м�������
	 * @param file_path ���ش�����ݼ����ļ�ϵͳ·��
	 * @param reader ���ڽ���һ�����ݵ�Reader��
	 * @return DatasetToTrainset����
	 */
	public static Dataset load_from_file(String file_path, Reader reader) {
		ArrayList<String[]> raw_ratings = new ArrayList<String[]>();
		File file = new File(file_path);
		BufferedReader buff_reader = null;
		try {
			buff_reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = buff_reader.readLine()) != null) {
            	raw_ratings.add(reader.parse_line(line));
            }
            buff_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buff_reader != null) {
                try {
                	buff_reader.close();
                } catch (IOException e1) {
                }
            }
        }
		return new Dataset(raw_ratings);
	}
	
}
