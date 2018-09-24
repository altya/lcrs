/**
 * ������ģ��
 * @author liuchen
 */

package prediction_algorithms.matrix_factorization;

import java.util.ArrayList;
import java.util.Random;

import accuracy.Accuracy;
import dataset.Dataset;
import dataset.LoadDataset;
import dataset.Testset;
import dataset.Trainset;
import model_selection.CrossValidation;
import model_selection.FoldBase;
import model_selection.KFold;
import model_selection.LeaveOneOut;
import prediction_algorithms.AlgoBase;
import prediction_algorithms.Prediction;

public class LFM extends AlgoBase{
	public int n_factors;   //�������
	public int n_epochs;  //��������
	public Boolean biased;  //�Ƿ��Ƿ�ƫ��
	public String optimize_measure;   //�Ż���������ѡ����sgd��als
	public double reg;   //���򻯲���
	public double learning_rate;   //ѧϰ����
	public double[][] q;  //����-��Ʒ����
	public double[][] p;  //����-�û�����
	public double[] bu;
	public double[] bi;
	
	public LFM(int n_factors, int n_epochs, Boolean biased, String optimize_measure, double reg, double learning_rate) {
		this.n_factors = n_factors;
		this.n_epochs = n_epochs;
		this.biased = biased;
		this.optimize_measure = optimize_measure;
		this.reg = reg;
		this.learning_rate = learning_rate;
	}


	/**
	 * �����Ż��������q��p����
	 * @param trainset ѵ����
	 */
	@Override
	public void fit(Trainset trainset) {
		this.trainset = trainset;
		this.q = new double[this.n_factors][this.trainset.n_items];
		this.p = new double[this.n_factors][this.trainset.n_users];
		//�����ʼ��
		Random random = new Random();
		for(int j = 0; j<this.n_factors;j++) {
			for(int i = 0; i<this.trainset.n_items;i++) {
				this.q[j][i] = random.nextDouble();
			}
			for(int i = 0; i<this.trainset.n_users;i++) {
				this.p[j][i] = random.nextDouble();
			}
		}
		
		if(this.biased) {
			this.bu = new double[this.trainset.n_users];
			this.bi = new double[this.trainset.n_items];
			for(int j = 0;j<this.trainset.n_users;j++) {
				this.bu[j] = random.nextDouble();
			}
			for(int j = 0;j<this.trainset.n_items;j++) {
				this.bi[j] = random.nextDouble();
			}
		}
		switch(this.optimize_measure){
			case "sgd":
				if(this.biased) {
					this.biased_sgd();
				}else {
					this.unbiased_sgd();
				}
				break;
			case "als":
				break;
			default:
				break;
		}
		
	}
	
	
	@Override
	public double predict(String ruid, String riid) {
		int uid = this.trainset.raw2inner_uid(ruid);
		int iid = this.trainset.raw2inner_iid(riid);
		
		if(uid == -1 || iid == -1) {   //���û�����Ʒ����ѵ������,Ԥ������Ϊѵ����ȫ��ƽ��ֵ
			return this.trainset.global_mean();
		}
		double est = 0;
		
		for(int j =0;j<this.n_factors;j++) {
			est += this.q[j][iid] * this.p[j][uid];
		}
		if(this.biased) {
			est = est + this.trainset.global_mean() + this.bu[uid] + this.bi[iid];
		}
		return est;
	}
	
	/**
	 * ����ƫ����Ż�����������ݶ��½����������Ż���
	 */
	public void unbiased_sgd() {
		int i = 0;
		for(int epoch = 0 ; epoch < this.n_epochs; epoch++) {
			for(int u : this.trainset.ur.keySet()) {
				for(int[] r : this.trainset.ur.get(u)) {
					i = r[0]; 
					double eui = r[1];
					for(int j = 0; j<this.n_factors;j++) {
						eui = eui - this.q[j][i] * this.p[j][u];
					}
					//����
					for(int j = 0; j<this.n_factors;j++) {
						this.q[j][i] = this.q[j][i] - this.learning_rate * (this.reg * this.q[j][i] - eui * this.p[j][u]);
						this.p[j][u] = this.p[j][u] - this.learning_rate * (this.reg * this.p[j][u] - eui * this.q[j][i]);
					}
				}
			}
		}	
	}
	
	/**
	 * ��ƫ����Ż�����������ݶ��½����������Ż���
	 */
	public void biased_sgd() {
		int i = 0;
		for(int epoch = 0 ; epoch < this.n_epochs; epoch++) {
			for(int u : this.trainset.ur.keySet()) {
				for(int[] r : this.trainset.ur.get(u)) {
					i = r[0]; 
					double eui = r[1] - this.trainset.global_mean() - this.bu[u] - this.bi[i];
					for(int j = 0; j<this.n_factors;j++) {
						eui = eui - this.q[j][i] * this.p[j][u];
					}
					//����
					this.bu[u] = this.bu[u] - this.learning_rate * (this.reg * this.bu[u] - eui);
					this.bi[i] = this.bi[i] - this.learning_rate * (this.reg * this.bi[i] - eui);
					for(int j = 0; j<this.n_factors;j++) {
						this.q[j][i] = this.q[j][i] - this.learning_rate * (this.reg * this.q[j][i] - eui * this.p[j][u]);
						this.p[j][u] = this.p[j][u] - this.learning_rate * (this.reg * this.p[j][u] - eui * this.q[j][i]);
					}
				}
			}
		}	
		
	}
	
	public static void main(String[] args) {
		AlgoBase algo = new LFM(100, 100, true, "sgd", 0.1, 0.02);   //��reg=0.1,learning_rate = 0.02ʱ����
		Dataset data = LoadDataset.load_builtin("ml-100k");
		String[] measures = {"mae","rmse"};
		FoldBase fold = new KFold(5, 2);
		CrossValidation.cross_validate(algo, data, measures, fold);
	}
	
	
	

}
