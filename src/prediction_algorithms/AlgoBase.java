/**
 * ����������Ԥ���㷨�ĳ������
 * @author liuchen
 */

package prediction_algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dataset.Testset;
import dataset.Trainset;

public abstract class AlgoBase {
	
	public Trainset trainset;
	
	/**
	 * �ڸ���ѵ������ѵ���㷨
	 * @param trainset ѵ����
	 */
	public abstract void fit(Trainset trainset);
	
	/**
	 * Ԥ��user��item������
	 * @param ruid ԭʼuserID
	 * @param riid ԭʼitemID
	 * @return ����
	 */
	public abstract double predict(String ruid, String riid);
	
	/**
	 * Ԥ��Ԥ��user��item�����֣�����Ԥ����Ϣ���
	 * @param ruid ԭʼuserID
	 * @param riid ԭʼitemID
	 * @param r_ui ��ʵ���֣� 0�����޴���Ϣ
	 * @return Prediction����
	 */
	public Prediction predict(String ruid, String riid, double r_ui) {
		double est = this.predict(ruid, riid);
		return new Prediction(ruid, riid, r_ui, est);
	}
	
	/**
	 * �Բ��Լ��Ľ���Ԥ��
	 * @param testset ���Լ�
	 * @return ArrayList<Prediction>����
	 */
	public ArrayList<Prediction> test(Testset testset){
		ArrayList<Prediction> pres = new ArrayList<Prediction>();
		for(String[] t : testset.testset) {
			Prediction pre = this.predict(t[0], t[1], Double.parseDouble(t[2]));
			pres.add(pre);
		}
		return pres;
	}
	
	/**
	 * ����Top-N�Ƽ�
	 * @param pres Ԥ������
	 * @param n ÿ���û��Ƽ���Ʒ��
	 * @return Map<String, ArrayList<String>>����
	 */
	public Map<String, ArrayList<String>> get_top_n(ArrayList<Prediction> pres, int n){
		Map<String, ArrayList<String>> top_n = new HashMap<String, ArrayList<String>>();
		return top_n;
	}
	
}
