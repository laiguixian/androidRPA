package wxyyzf;

import java.sql.ResultSet;
import java.util.Date;

import tdrtool.databasebean;
import tdrtool.isdatavalid;

public class mubanbiao {
	private isdatavalid idv=new isdatavalid();
	public String huoqumuban(String imei, String imsi, int weixinbanben,String somevalue) {
		String shujubanben=somevalue.substring(0, somevalue.indexOf(","));
		String logstr=somevalue.substring(somevalue.indexOf(",")+1);
		String returnxinxi = "";
		String getsuccess="��ȡʧ��";
		if (((imei.length() > 0) | (imsi.length() > 0))
				&& (shujubanben.length() > 0)) {
			Date dt = new Date();// �������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
			// DateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//����ʱ���ʽ
			// String huoqushijian=df.format(dt);
			databasebean huoquxinxi = new databasebean();
			ResultSet sbrs;
			try {
				sbrs = huoquxinxi
						.executeQueryscroll("select * from shebeibiao where (imei='"
								+ imei + "') order by zhujian desc");
				if (sbrs.next()) {
					if (sbrs.getDate("daoqishijian").before(dt))
						returnxinxi = "ע�ᵽ��";
					else {
						String beizhu=sbrs.getString("beizhu").trim();//��ȡ��ע����豸��ذ汾��Ϣ
						float screenwidth=sbrs.getFloat("screenwidth");//��ȡ��Ļ���
						float screenheight=sbrs.getFloat("screenheight");//��ȡ��Ļ�߶�
						ResultSet mbrs;
						try {
							returnxinxi="";	
							mbrs = huoquxinxi.executeQueryscroll("select * from mubanbiao where weixinbb='"+weixinbanben+"' and isactive=1 order by zhujian");
							while (mbrs.next()) {
								returnxinxi = returnxinxi+mbrs.getString("daihao") + ","//ģ�����
								+ mbrs.getString("canshu") + ","//ʶ�����
								+ mbrs.getString("picstr") + ","//ͼƬ�ַ���
								+ mbrs.getString("leixing")+";";//ģ������
							}
							if(returnxinxi.length()>0){
								returnxinxi=returnxinxi.substring(0, returnxinxi.length()-1);
								//���imei���Ϸ����ֻ��ͺŵȲ�������Ļ��С�����򷵻ؿ�
								if((!idv.isimei(imei))||(!(beizhu.indexOf("2014813---Xiaomi---19---4.4.4---5.12.17")>-1))||(screenwidth!=720.0)||(screenheight!=1280.0)){//���imei���Ϸ�
									System.out.println("��ȡģ��ʧ�ܣ�"+beizhu+";"+screenwidth+";"+screenheight);
									returnxinxi="";
								}
							}
							else
								return "�޼�¼";
							mbrs.close();
							huoquxinxi.executeUpdate("insert into logbiao(imsi,imei,logstr) values('"+imsi+"','"+imei+"','"+logstr+"')");
						} catch (Exception e) {
							huoquxinxi.closeConnection();
							return "���ݿ�ӿ�2����";
						}
					}
				} else {
					return "�޼�¼";
				}
				sbrs.close();
				if (returnxinxi.length() > 0)
					getsuccess="��ȡ�ɹ�";
				else
					getsuccess="��ȡʧ��";
				//��¼�ͻ���½��Ϣ
				huoquxinxi.executeUpdate("update sbdlbiao set duqumuban='"+getsuccess+"' where zhujian=(select max(zhujian) from sbdlbiao where imsi='"+imsi+"' and imei='"+imei+"')");
				huoquxinxi.closeConnection();
			} catch (Exception e) {
				huoquxinxi.closeConnection();
				return "���ݿ�ӿ�1����";
			}
		}
		if (returnxinxi.length() > 0)
			return returnxinxi;
		else
			return "û��ע����ѵ���";
	}
}
