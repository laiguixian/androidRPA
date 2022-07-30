package tdrtool;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class getcndatetime {
	public static String getcndatetime1(String str){		//进行转码操作的方法
		Date dt=new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");//设置显示格式
		String nowTime="";
		nowTime= df.format(dt);//用DateFormat的format()方法在dt中获取并以yyyy/MM/dd HH:mm:ss格式显示
		str=nowTime;
		return str;
	}
}