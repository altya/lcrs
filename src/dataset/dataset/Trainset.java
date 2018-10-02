/**
 * ����������㷨ѵ�������ȫ��ѵ������
 * ����ԭʼ��userID��itemID��ϵͳת��Ϊ������Ϊ���ʵ�ID
 * @author liuchen
 */

package dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trainset {
	public Map<Integer, ArrayList<int[]>> ur;   //�û�-��Ʒ���ű�
	public Map<Integer, ArrayList<int[]>> ir;   //��Ʒ-�û����ű�
	public int n_users;   //�û���
	public int n_items;   //��Ʒ��
	public int n_ratings;  //������
	private Map<String, Integer> raw2inner_id_users;  //ԭʼuserID��ϵͳuserID��ӳ��
	private Map<String, Integer> raw2inner_id_items;  //ԭʼitemID��ϵͳitemID��ӳ��
	private Map<Integer, String> inner2raw_id_users;  //ϵͳuserID��ԭʼuserID��ӳ��
	private Map<Integer, String> inner2raw_id_items;  //ϵͳitemID��ԭʼitemID��ӳ��
	private double global_mean;   //���־�ֵ
	
	public Trainset(Map<Integer, ArrayList<int[]>> ur, Map<Integer, ArrayList<int[]>> ir, int n_users, int n_items,
			int n_ratings, Map<String, Integer> raw2inner_id_users,
			Map<String, Integer> raw2inner_id_items) {
		this.ur = ur;
		this.ir = ir;
		this.n_users = n_users;
		this.n_items = n_items;
		this.n_ratings = n_ratings;
		this.raw2inner_id_users = raw2inner_id_users;
		this.raw2inner_id_items = raw2inner_id_items;
		this.inner2raw_id_users = null;
		this.inner2raw_id_items = null;
		this.global_mean = -1.0;
	}
	
	/**
	 * ����ԭʼuserID��Ӧ��ϵͳuserID
	 * @param ruid ԭʼuserID
	 * @return ϵͳuserID
	 */
	public int raw2inner_uid(String ruid) {
		try {
			return this.raw2inner_id_users.get(ruid);
		}catch (Exception e) {
			return -1;
		}
		
	}
	
	/**
	 * ����ԭʼitemID��Ӧ��ϵͳitemID
	 * @param riid ԭʼ��itemID
	 * @return ϵͳ��itemID
	 */
	public int raw2inner_iid(String riid) {
		try {
			return this.raw2inner_id_items.get(riid);
		}catch(Exception e) {
			return -1;
		}
	}
	
	/**
	 * ����ϵͳitemID��Ӧ��ԭʼitemID
	 * @param uid ϵͳitemID
	 * @return ԭʼitemID
	 */
	public String inner2raw_uid(int uid) {
		if(this.inner2raw_id_users == null) {
			this.inner2raw_id_users = new HashMap<Integer, String>();
			for(String key : this.raw2inner_id_users.keySet()) {
				this.inner2raw_id_users.put(this.raw2inner_id_users.get(key), key);
			}
		}
		return this.inner2raw_id_users.get(uid);
	}
	
	/**
	 * ����ϵͳitemID��Ӧ��ԭʼitemID
	 * @param iid ϵͳitemID
	 * @return ԭʼitemID
	 */
	public String inner2raw_iid(int iid) {
		if(this.inner2raw_id_items == null) {
			this.inner2raw_id_items = new HashMap<Integer, String>();
			for(String key : this.raw2inner_id_items.keySet()) {
				this.inner2raw_id_items.put(this.raw2inner_id_items.get(key), key);
			}
		}
		return this.inner2raw_id_items.get(iid);
	}
	
	/**
	 * ��ѵ�����п��ܵ�����Ԥ�⹹�ɲ��Լ�
	 * @return Testsetʵ��
	 */
	public Testset build_anti_testset() {
		ArrayList<String[]> testset = new ArrayList<String[]>();
		for(int u = 0 ; u< this.n_users; u++) {
			List<Integer> u_i = new ArrayList<Integer>();
			for(int[] r : this.ur.get(u)) {
				u_i.add(r[0]);
			}
			for(int i = 0; i<this.n_items; i++) {
				if(!u_i.contains(i)) {
					String[] test = {this.inner2raw_uid(u), this.inner2raw_iid(i), String.valueOf(this.global_mean())};
					testset.add(test);
				}
			}
		}
		return new Testset(testset);
	}
	
	/**
	 *ȫ�����־�ֵ
	 * @return double
	 */
	public double global_mean() {
		if (this.global_mean == -1.0) {
			double sum = 0.0;
			//����ȫ�����־�ֵ
			for(int u : this.ur.keySet()) {
				for(int[] r : this.ur.get(u)) {
					sum += r[1];
				}
			}
			this.global_mean = sum / this.n_ratings;
		}
		return this.global_mean;
	}

}
