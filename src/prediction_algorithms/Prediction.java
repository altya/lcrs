/**
 * �������ڴ洢һ��Ԥ��ľ�����Ϣ
 * @author liuchen
 */

package prediction_algorithms;

public class Prediction {
	
	public String ruid;  
	public String riid;
	public double r_ui;  //��ʵ������  
	public double est;   //Ԥ�������
	
	public Prediction(String ruid, String riid, double r_ui, double est) {
		this.ruid = ruid;
		this.riid = riid;
		this.r_ui = r_ui;
		this.est = est;
	}
	
	/**
	 * ��ӡ��Ϣ
	 */
	public String toString (){
		return "�û�ID��" + this.ruid +"; ��ƷID��" + this.riid +"; ��ʵ���֣�" + this.r_ui +"; Ԥ�����֣�" + String.valueOf(this.est);
	}
	
	

}
