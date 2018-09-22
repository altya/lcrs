/**
 * �����û���Эͬ�����㷨
 * @author liuchen
 */

package prediction_algorithms.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import accuracy.Accuracy;
import dataset.LoadDataset;
import dataset.Testset;
import dataset.Trainset;
import prediction_algorithms.AlgoBase;
import prediction_algorithms.Prediction;

public class UserCF extends AlgoBase{
	
	public double[][] sim;   //���ƶȾ���
	public String sim_name;  
	public int k; 
	public Boolean activity_punish;
	
	/**
	 * ���캯��
	 * @param sim_name ���ƶȼ��㷽�����ƣ���ѡ����cosine��adjusted_cosine��pearson
	 * @param activity_punish �Ƿ�Ի�Ծ�Ƚ��гͷ�
	 * @param k ����Ԥ�����ֵ��������Ŀ
	 */
	public UserCF(String sim_name, int k, Boolean activity_punish) {
		this.sim_name = sim_name;
		this.k = k;
		this.activity_punish = activity_punish;
	}

	@Override
	public void fit(Trainset trainset) {
		this.trainset = trainset;
		//�������ƶ�
		System.out.println("���ڼ����û���" + this.sim_name +"���ƶ�...");
		switch(this.sim_name) {
		case "cosine":
			this.sim = Similarity.cosine(trainset, true, this.activity_punish);
			break;
		case "adjusted_cosine":
			this.sim = Similarity.adjusted_cosine(trainset, true);
			break;
		default:
			break;
		}
		System.out.println("�û���" + this.sim_name +"���ƶȼ������!");
	}

	@Override
	public double predict(String ruid, String riid) {
		int uid = this.trainset.raw2inner_uid(ruid);
		int iid = this.trainset.raw2inner_iid(riid);
		
		if(uid == -1 || iid == -1) {   //���û�����Ʒ����ѵ������,Ԥ������Ϊѵ����ȫ��ƽ��ֵ
			return this.trainset.global_mean();
		}
		
		Map<Double, ArrayList<Integer>> neighbors = new HashMap<Double, ArrayList<Integer>>(); 
		for(int[] r : this.trainset.ir.get(iid)) {
			if(!neighbors.containsKey(this.sim[uid][r[0]])) {
				neighbors.put(this.sim[uid][r[0]], new ArrayList<Integer>());
			}
			neighbors.get(this.sim[uid][r[0]]).add(r[1]);
		}
		
		
		//neighbors��key����
		List<Map.Entry<Double, ArrayList<Integer>>> infoIds = new ArrayList<Map.Entry<Double, ArrayList<Integer>>>(neighbors.entrySet());    
		Collections.sort(infoIds, new Comparator<Map.Entry<Double, ArrayList<Integer>>>() {  
			public int compare(Map.Entry<Double, ArrayList<Integer>> o1,  Map.Entry<Double, ArrayList<Integer>> o2) {  
				return (o2.getKey()).compareTo(o1.getKey());  
	        }  
	    });  
		
		double sum_sim = 0, sum_ratings = 0;
		int actual_k = 0;
		for (Map.Entry<Double, ArrayList<Integer>> mapping : infoIds) {
			for(int value : mapping.getValue()) {
				actual_k ++;
				if(actual_k > this.k) {
					break;
				}
				sum_sim += mapping.getKey();
				sum_ratings += mapping.getKey() * value;
			}
		} 
		
		if(sum_sim == 0) {
			return this.trainset.global_mean();
		}else {
			return sum_ratings / sum_sim;
		}
		
	}
	
	public static void main(String[] args) {
		UserCF ucf = new UserCF("cosine", 40, false);
		Trainset trainset = LoadDataset.load_builtin("ml-100k").built_full_trainset();
		Testset testset = trainset.build_anti_testset();
		ucf.fit(trainset);
		ArrayList<Prediction> pres = ucf.test(testset);
		System.out.println(Accuracy.rmse(pres));
		System.out.println(Accuracy.mae(pres));
	}
	

}
