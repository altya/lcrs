/**
 * �����ǻ������ݼ��ĳ������
 * @author liuchen
 */

package model_selection;

import java.util.ArrayList;

import dataset.Dataset;

public abstract class FoldBase {
	
	public int n_splits;      //���ݼ���������
	public long random_seed;  //�������,��shuffleΪtrueʱ��Ч
	public Boolean shuffle;   //�ڻ���ǰ�Ƿ��������
	
	/**
	 * �Ը��������ݼ����л���
	 * @param dataset ���ݼ�
	 * @return ArrayList<Object[]>��������Object[]�ֱ��Trainset��Testset����
	 */
	public abstract ArrayList<Object[]> split(Dataset dataset);

}
