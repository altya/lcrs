/**
 * �������ڼ���Ԥ��ľ���
 * ֧�ֵľ���ָ����rmse��mae��fcp
 * ���಻Ӧ�ñ�ʵ���������з���Ϊ��̬����
 * @author liuchen
 */

package accuracy;

import java.util.ArrayList;

import prediction_algorithms.Prediction;

public class Accuracy {
	
	/**
	 * ���������
	 * @param pres Ԥ������
	 * @return double
	 */
	public static double rmse(ArrayList<Prediction> pres) {
		double r = 0.0;
		for(Prediction p : pres) {
			r += (p.est - p.r_ui) * (p.est - p.r_ui);
		}
		
		if(pres.size() == 0) {
			return 0;
		}else {
			return Math.sqrt(r / pres.size());
		}
	}
	
	/**
	 * ƽ���������
	 * @param pres Ԥ������
	 * @return double
	 */
	public static double mae(ArrayList<Prediction> pres) {
		double r = 0.0;
		for(Prediction p : pres) {
			r += Math.abs(p.est - p.r_ui);
		}
		
		if(pres.size() == 0) {
			return 0;
		}else {
			return r / pres.size();
		}
	}

}
