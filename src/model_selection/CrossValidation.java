/**
 * �������ý�����֤����һ���㷨ģ��
 * ���಻Ӧ�ñ�ʵ���������з���Ϊ��̬����
 * @author liuchen
 */

package model_selection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import accuracy.Accuracy;
import dataset.Dataset;
import dataset.LoadDataset;
import dataset.Reader;
import dataset.Testset;
import dataset.Trainset;
import prediction_algorithms.AlgoBase;
import prediction_algorithms.Prediction;
import prediction_algorithms.knn.ItemCF;
import prediction_algorithms.knn.UserCF;

public class CrossValidation {
	
	/**
	 * �����Ľ�����֤����
	 * @param algo Ҫ���Ե��㷨ģ��
	 * @param data ���ݼ�
	 * @param measures ����ָ�꣬��["rmse","mae"]
	 * @param fold �������ݼ��Ļ���
	 */
	public static void cross_validate(AlgoBase algo,Dataset data,String[] measures,FoldBase fold) {
		ArrayList<Object[]> train_test = fold.split(data);
		int k = 0;
		HashMap<Integer, Double[]> accuracys = new HashMap<Integer, Double[]>(); 
		for(Object[] tt : train_test) {
			k++;
			Trainset train = (Trainset)tt[0];
			Testset test = (Testset)tt[1];
			algo.fit(train);
			ArrayList<Prediction> pres = algo.test(test);
			Double[] acc = new Double[measures.length]; 
			int index = 0;
			for(String measure : measures) {
				switch(measure) {
				case "rmse":
					acc[index++] = Accuracy.rmse(pres);
					break;
				case "mae":
					acc[index++] = Accuracy.mae(pres);
					break;
				default:
					break;
				}
			}
			accuracys.put(k, acc);
		}
		print(accuracys, measures);  
	}
	
	/**
	 * ��ӡ���������Ϣ
	 * @param accuracys �������
	 * @param measures  ����ָ��
	 */
	public static void print(HashMap<Integer, Double[]> accuracys, String[] measures) {
		DecimalFormat df = new DecimalFormat( "0.0000 ");
		System.out.print("����ָ�꣺     ");
		for(String measure : measures) {
			System.out.print( measure+"   ");
		}
		System.out.println();
		double[] mean = new double[measures.length];  //ÿһ������ָ���ƽ��ֵ
		for(int key : accuracys.keySet()) {
			System.out.print("��"+key+"�۽����    ");
			int j = 0;
			for(Double a : accuracys.get(key)) {
				mean[j] += a;
				j++;
				System.out.print(df.format(a)+"   ");
			}
			System.out.println();
		}
		System.out.print("ƽ��ֵ��         ");
		for(int k = 0;k< measures.length;k++) {
			mean[k] = mean[k] / accuracys.size();
			System.out.print(df.format(mean[k])+"   ");
		}
	}
	public static void main(String[] args) {
		AlgoBase algo = new UserCF("cosine", 30, true);
		Dataset data = LoadDataset.load_builtin("ml-100k");
		String[] measures = {"mae","rmse"};
		FoldBase fold = new KFold(5, 2);
		CrossValidation.cross_validate(algo, data, measures, fold);
	}

}
