package wxyyzf;

import tdrtool.databasebean;
import tdrtool.isdatavalid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
public class shebeibiao {
	private isdatavalid idv=new isdatavalid();
	public String huoqushebeixinxi(String imei, String imsi, String shebeiinfo, String shujubanben, String userinfo) {
		String shebeixinxi = "";
		if (((imei.length() > 0) | (imsi.length() > 0))
				&& (shujubanben.length() > 0)) {
			Date dt = new Date();// �������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
			String getsuccess="��ȡʧ��";
			String oneline=shebeiinfo;
			String screenwidth=oneline.substring(0, oneline.indexOf(","));//��Ļ�ֱ��ʿ��
			oneline=oneline.substring(oneline.indexOf(",")+1);
			String screenheight=oneline.substring(0, oneline.indexOf(","));//��Ļ�ֱ��ʸ߶�
			oneline=oneline.substring(oneline.indexOf(",")+1);
			String wxbb=oneline.substring(0, oneline.indexOf(","));//΢�Ű汾
			String beizhu=oneline.substring(oneline.indexOf(",")+1);
			//System.out.println("�汾ֵ��"+wxbb);
			// DateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//����ʱ���ʽ
			// String huoqushijian=df.format(dt);
			databasebean huoqushebei = new databasebean();
			
			ResultSet sbrs;
			try {
				sbrs = huoqushebei
						.executeQueryscroll("select * from shebeibiao where (imei='"
								+ imei + "') order by zhujian desc");
				if (sbrs.next()) {
					if (sbrs.getDate("daoqishijian").before(dt))
						shebeixinxi = "ע�ᵽ��";
					else {
						//���ж�ע��
						//if ((shujubanben.equals("1"))&&(sbrs.getString("shoujihao")!=null)&&(sbrs.getString("shoujihao").length()>0))// ��ȡ�����ݰ汾Ϊ1
						//����ע��
						if /*(*/(shujubanben.equals("1"))/*&&(sbrs.getString("shoujihao")!=null)&&(sbrs.getString("shoujihao").length()>0))*/// ��ȡ�����ݰ汾Ϊ1
							shebeixinxi = sbrs.getString("shoujihao") + ","//�ֻ���
									+ sbrs.getString("shibiejiange") + ","//ʶ���� ��λ����
									+ sbrs.getString("leixing") + ","//�豸����
									+ sbrs.getString("mingling") + ","//ִ�е�����
									+ sbrs.getString("fmtstr") + ","//¼������ʱ��Ҫ�����ļ�ɨ��Ĺ㲥������ͷ
									+ sbrs.getString("videotype") + ","//¼�����ͣ���mp4,3gp��
									+ sbrs.getString("sdcardpath") + ","//�洢��·��
									+ sbrs.getString("tdrcwx") + ","//��Ӧ�õ�·��
									+ sbrs.getString("tdrcwxtemp") + ","//Ӧ�õ���ʱ·��
									+ sbrs.getString("tdrcwxmov");//¼��Ŀ¼
									
						ResultSet bbrs;
						bbrs = huoqushebei
								.executeQueryscroll("select * from wxbbbiao where weixinbb ='"+wxbb+"'");
						if (bbrs.next()) {
							shebeixinxi=shebeixinxi + ","+ bbrs.getString("bi") + ","//��ɫ�������ͣ������ɫ�����ڽ�ͼƬת������
						            + bbrs.getString("wi")+ ","//��ɫ�������ͣ������ɫ�����ڽ�ͼƬת������
						            + bbrs.getString("bstr");//��ɫ�ַ���Ϊ�����֣������ڸ��ַ�����Χ�ڵ����ִ����ɫ����֮�����ڷ�Χ�ڵ���Ϊ��ɫ
						}
						else
							shebeixinxi="";
						//���imei���Ϸ����ֻ��ͺŵȲ�������Ļ��С�����򷵻ؿ�
						if((!idv.isimei(imei))||(!(beizhu.indexOf("2014813---Xiaomi---19---4.4.4---5.12.17")>-1))||(!screenwidth.equals("720.0"))||(!screenheight.equals("1280.0"))){
							System.out.println("��ȡ�豸ʧ�ܣ�"+beizhu+";"+screenwidth+";"+screenheight);
							shebeixinxi="";
						}
						bbrs.close();
					}
					huoqushebei.executeUpdate("update shebeibiao set lastlogin=getdate() where imei='"+imei+"'");
				} else {
					huoqushebei.executeUpdate("insert into shebeibiao(leixing,imei,imsi,screenwidth,screenheight,beizhu)values("+
				    "'imei','"+imei+"','"+imsi+"','"+screenwidth+"','"+screenheight+"','"+beizhu+"')"
				    );
					shebeixinxi = "null,"//�ֻ���
							+ "5,"//ʶ���� ��λ����
							+ "imei,"//�豸����
							+ "null,"//ִ�е�����
							+ "FL=ZmlsZTovL21udA==T=Z=g,"//¼������ʱ��Ҫ�����ļ�ɨ��Ĺ㲥������ͷ
							+ "CI=Lm1wNA==P=U=d,"//¼�����ͣ���mp4,3gp��
							+ "v4=L3NkY2FyZC8=BD=JP,"//�洢��·��
							+ "=U=L3NkY2FyZC90ZHJjd3g=bd=j=,"//��Ӧ�õ�·��
							+ "AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW,"//Ӧ�õ���ʱ·��
							+ "O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm";//¼��Ŀ¼
				}
				if (shebeixinxi.length() > 0)
					getsuccess="��ȡ�ɹ�";
				else
					getsuccess="��ȡʧ��";
				
				//��¼�ͻ���½��Ϣ
				huoqushebei.executeUpdate("insert into sbdlbiao(imei,imsi,beizhu,duqushebei)values('"+
					    imei+"','"+imsi+"','"+userinfo+"+++"+beizhu+"','"+getsuccess+"')"
					    );
				sbrs.close();
				huoqushebei.closeConnection();
			} catch (Exception e) {
				huoqushebei.closeConnection();
				return "���ݿ�ӿڴ���";
			}
		}
		if (shebeixinxi.length() > 0)
			return shebeixinxi;
		else
			return "û��ע����ѵ���";
	}
}
