/**
 * һ������Эͬ�����Эͬ�����㷨
 * @author liuchen
 */

package prediction_algorithms.co_clustering;

import java.util.Random;

import dataset.Dataset;
import dataset.LoadDataset;
import dataset.Trainset;
import model_selection.CrossValidation;
import model_selection.FoldBase;
import model_selection.KFold;
import prediction_algorithms.AlgoBase;

public class CoClustering extends AlgoBase{
	public int n_cltr_u;  //�û��ظ���
	public int n_cltr_i;  //��Ʒ�ظ���
	public int n_epochs;  //������̵ĵ�������
	private int[] cltr_u; //�û���
	private int[] cltr_i; //��Ʒ��
	private double[] user_mean;  //�û����־�ֵ
	private double[] item_mean;  //��Ʒ���־�ֵ
	private double[] avg_cltr_u; //�û������־�ֵ
	private double[] avg_cltr_i; //��Ʒ�����־�ֵ
	private double[][] avg_cocltr; //Э�����־�ֵ
	
	public CoClustering(int n_cltr_u, int n_cltr_i, int n_epochs) {
		this.n_cltr_u = n_cltr_u;
		this.n_cltr_i = n_cltr_i;
		this.n_epochs = n_epochs;
	}

	
	/**
	 * �������
	 */
	@Override
	public void fit(Trainset trainset) {
		int i = 0, j = 0;
		this.trainset = trainset;
		
		//��ʼ��
		this.cltr_u = new int[this.trainset.n_users];
		this.cltr_i = new int[this.trainset.n_items];
		this.user_mean = new double[this.trainset.n_users];
		this.item_mean = new double[this.trainset.n_items];
		this.avg_cltr_u = new double[this.n_cltr_u];
		this.avg_cltr_i = new double[this.n_cltr_i];
		this.avg_cocltr = new double[this.n_cltr_u][this.n_cltr_i];
		
		Random random = new Random();
		//������û�����Ʒ�����
		for(i = 0; i < this.trainset.n_users; i++) {
			this.cltr_u[i] = random.nextInt(this.n_cltr_u);
		}
		for(i = 0; i < this.trainset.n_items; i++) {
			this.cltr_i[i] = random.nextInt(this.n_cltr_i);
		}
		
		//�����û�����Ʒ��ֵ
		for(int u : this.trainset.ur.keySet()) {
			double sum = 0;
			int count = 0;
			for(int[] r : this.trainset.ur.get(u)) {
				sum += r[1];
				count++;
			}
			this.user_mean[u] = sum / count;
		}
		for(int item : this.trainset.ir.keySet()) {
			double sum = 0;
			int count = 0;
			for(int[] r : this.trainset.ir.get(item)) {
				sum += r[1];
				count++;
			}
			this.item_mean[item] = sum / count;
		}
		
		int g = 0, h = 0, k = 0, l = 0;
		double err = 0, est = 0;
		double[] gerrs = new double[this.n_cltr_u];
		double[] herrs = new double[this.n_cltr_i];
		//�������
		for(int epoch = 0; epoch < this.n_epochs; epoch++) {
			//����avg_cltr_u,avg_cltr_i,avg_cocltr
			this.computer_avg();
			
			//���û�������ƽ������С�Ĵ�
			for(i = 0; i<this.trainset.n_users;i++) {
				for(k = 0; k<this.n_cltr_u; k++) {
					g = k;
					err = 0;
					for(int[] r : this.trainset.ur.get(i)) {
						j = r[0];
						h = this.cltr_i[j];
						est = this.avg_cocltr[g][h] + this.user_mean[i] - this.avg_cltr_u[g] + this.item_mean[j] - this.avg_cltr_i[h];
						err += (r[1] - est) * (r[1] - est);
					}
					
					gerrs[k] = err;
				}
				this.cltr_u[i] = this.select_min_index(gerrs, this.n_cltr_u);
			}
			
			//����Ʒ������ƽ������С�Ĵ�
			for(j = 0; j < this.trainset.n_items; j++) {
				for(l=0; l<this.n_cltr_i; l++) {
					h = l;
					err = 0;
					for(int[] r : this.trainset.ir.get(j)) {
						i = r[0];
						g = this.cltr_u[i];
						est = this.avg_cocltr[g][h] + this.user_mean[i] - this.avg_cltr_u[g] + this.item_mean[j] - this.avg_cltr_i[h];
						err += (r[1] - est) * (r[1] - est);
					}
					herrs[l] = err;
				}
				this.cltr_i[j] = this.select_min_index(herrs, this.n_cltr_i);
			}
			
			
			
		}
		
		//������
		this.computer_avg();
		
		
	}

	@Override
	public double predict(String ruid, String riid) {
		int uid = this.trainset.raw2inner_uid(ruid);
		int iid = this.trainset.raw2inner_iid(riid);
		
		if(uid == -1 || iid == -1) {   //���û�����Ʒ����ѵ������,Ԥ������Ϊѵ����ȫ��ƽ��ֵ
			return this.trainset.global_mean();
		}
		
		int g = this.cltr_u[uid];
		int h = this.cltr_i[iid];
		double est = 0;
		est = this.avg_cocltr[g][h] + this.user_mean[uid] - this.avg_cltr_u[g] + this.item_mean[iid] - this.avg_cltr_i[h];
		return est;
	}
	
	/**
	 * ����avg_cltr_u,avg_cltr_i,avg_cocltr
	 */
	public void computer_avg() {
		//avg_cltr_u,avg_cltr_i,avg_cocltr����Ϊ0
		int g = 0, h = 0;
		for(g = 0;g<this.n_cltr_u; g++) {
			this.avg_cltr_u[g] = 0;
		}
		for(h = 0; h<this.n_cltr_i;h++) {
			this.avg_cltr_i[h] = 0;
		}
		for(g = 0;g<this.n_cltr_u;g++) {
			for(h = 0;h<this.n_cltr_i;h++) {
				this.avg_cocltr[g][h] = 0;
			}
		}
		
		//����avg_cltr_u,avg_cltr_i,avg_cocltr
		int[] avg_cltr_u_count = new int[this.n_cltr_u];
		int[] avg_cltr_i_count = new int[this.n_cltr_i];
		int[][] avg_cocltr_count = new int[this.n_cltr_u][this.n_cltr_i];
		for(int u : this.trainset.ur.keySet()) {
			g = this.cltr_u[u];
			for(int[] r : this.trainset.ur.get(u)) {
				h = this.cltr_i[r[0]];
				this.avg_cltr_u[g] += r[1];
				this.avg_cltr_i[h] += r[1];
				this.avg_cocltr[g][h] += r[1];
				avg_cltr_u_count[g] += 1;
				avg_cltr_i_count[h] += 1;
				avg_cocltr_count[g][h] += 1;
			}
		}
		
		for(g = 0; g<this.n_cltr_u;g++) {
			if(avg_cltr_u_count[g] > 0) {
				this.avg_cltr_u[g] = this.avg_cltr_u[g] / avg_cltr_u_count[g];
			}else {
				this.avg_cltr_u[g] = this.trainset.global_mean();
			}
		}
		
		for(h = 0; h<this.n_cltr_i;h++) {
			if(avg_cltr_i_count[h]>0) {
				this.avg_cltr_i[h] = this.avg_cltr_i[h] / avg_cltr_i_count[h];
			}else {
				this.avg_cltr_i[h] = this.trainset.global_mean();
			}
		}
		
		for(g = 0;g<this.n_cltr_u;g++) {
			for(h = 0;h<this.n_cltr_i;h++) {
				if(avg_cocltr_count[g][h] > 0) {
					this.avg_cocltr[g][h] = this.avg_cocltr[g][h] / avg_cocltr_count[g][h];
				}else {
					this.avg_cocltr[g][h] = this.trainset.global_mean();
				}
			}
		}
		
	}
	
	/**
	 * ��һ��������Сֵ�±�
	 * @param aa ����
	 * @param length ���鳤��
	 * @return int
	 */
	public int select_min_index(double[] aa, int length) {
		int min_index = 0;
		for(int i = 1; i<length; i++) {
			if(aa[min_index] < aa[i]) {
				min_index = i;
			}
		}
		return min_index;
	}

	public static void main(String[] args) {
		AlgoBase algo = new CoClustering(3,3,300);
		Dataset data = LoadDataset.load_builtin("ml-100k");
		String[] measures = {"mae","rmse"};
		FoldBase fold = new KFold(5, 2);
		CrossValidation.cross_validate(algo, data, measures, fold);
		
	}
}
