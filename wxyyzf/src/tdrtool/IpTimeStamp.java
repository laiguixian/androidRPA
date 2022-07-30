package tdrtool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;

public class IpTimeStamp {
    private SimpleDateFormat sim=null;//������ȡʱ��
    private String ip=null;
    public IpTimeStamp(){
    }
    public IpTimeStamp(String ip){
        this.ip=ip;
    }
    public String getIpTimeRand(String ip){
    	this.ip=ip;
    	StringBuffer sbf=new StringBuffer();
        if(this.ip!=null){
            String a[]=this.ip.split("\\.");                //���ݵ������ip��ַ������Ҫת��
            for(int i=0;i<a.length;i++){
                sbf.append(this.addZero(a[i], 3));            //���ò���ķ�����ÿ��ip������λ���Զ����㵽��λ
            }
            sbf.append(this.getTimeStamp());                //��this�������ⲿ�ķ���
            Random random=new Random();                        //Ҫ���������
            for(int i=0;i<3;i++){                            //������λ�����
                sbf.append(random.nextInt(10));                //ÿλ�������������10
            }
        }
        return sbf.toString();
    }
    @SuppressWarnings("unused")
    private String getDate(){                                //����������ʱ���ʵ��
        //this.sim=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:zzz");
        //return this.sim.format(new Date());
    	Date dt = new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//������ʾ��ʽ
		String nowTime = "";
		nowTime = df.format(dt);
		return nowTime;
    }
    private String getTimeStamp(){                            //����ʱ���
        //this.sim=new SimpleDateFormat("yyyyMMddhhmmsszzz");
        //return this.sim.format(new Date());
    	Date dt = new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//������ʾ��ʽ
		String nowTime = "";
		nowTime = df.format(dt);
		return nowTime;
    }
    private String addZero(String str,int len){                //�Զ�����ķ���������Ϊָ�����ַ����ͳ���
        StringBuffer s=new StringBuffer();
        s.append(str);
        while(s.length()<len){
            s.insert(0,"0");                                //�����λ���Ͻ��в������
        }
        return s.toString();
    }
    
    //������
}