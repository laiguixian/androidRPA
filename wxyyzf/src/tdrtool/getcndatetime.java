package tdrtool;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class getcndatetime {
	public static String getcndatetime1(String str){		//����ת������ķ���
		Date dt=new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
		DateFormat df = new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��");//������ʾ��ʽ
		String nowTime="";
		nowTime= df.format(dt);//��DateFormat��format()������dt�л�ȡ����yyyy/MM/dd HH:mm:ss��ʽ��ʾ
		str=nowTime;
		return str;
	}
}