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
			Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
			String getsuccess="获取失败";
			String oneline=shebeiinfo;
			String screenwidth=oneline.substring(0, oneline.indexOf(","));//屏幕分辨率宽度
			oneline=oneline.substring(oneline.indexOf(",")+1);
			String screenheight=oneline.substring(0, oneline.indexOf(","));//屏幕分辨率高度
			oneline=oneline.substring(oneline.indexOf(",")+1);
			String wxbb=oneline.substring(0, oneline.indexOf(","));//微信版本
			String beizhu=oneline.substring(oneline.indexOf(",")+1);
			//System.out.println("版本值："+wxbb);
			// DateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间格式
			// String huoqushijian=df.format(dt);
			databasebean huoqushebei = new databasebean();
			
			ResultSet sbrs;
			try {
				sbrs = huoqushebei
						.executeQueryscroll("select * from shebeibiao where (imei='"
								+ imei + "') order by zhujian desc");
				if (sbrs.next()) {
					if (sbrs.getDate("daoqishijian").before(dt))
						shebeixinxi = "注册到期";
					else {
						//有判断注册
						//if ((shujubanben.equals("1"))&&(sbrs.getString("shoujihao")!=null)&&(sbrs.getString("shoujihao").length()>0))// 获取的数据版本为1
						//屏蔽注册
						if /*(*/(shujubanben.equals("1"))/*&&(sbrs.getString("shoujihao")!=null)&&(sbrs.getString("shoujihao").length()>0))*/// 获取的数据版本为1
							shebeixinxi = sbrs.getString("shoujihao") + ","//手机号
									+ sbrs.getString("shibiejiange") + ","//识别间隔 单位：秒
									+ sbrs.getString("leixing") + ","//设备类型
									+ sbrs.getString("mingling") + ","//执行的命令
									+ sbrs.getString("fmtstr") + ","//录像生成时需要发送文件扫描的广播参数开头
									+ sbrs.getString("videotype") + ","//录像类型，如mp4,3gp等
									+ sbrs.getString("sdcardpath") + ","//存储卡路径
									+ sbrs.getString("tdrcwx") + ","//本应用的路径
									+ sbrs.getString("tdrcwxtemp") + ","//应用的临时路径
									+ sbrs.getString("tdrcwxmov");//录像目录
									
						ResultSet bbrs;
						bbrs = huoqushebei
								.executeQueryscroll("select * from wxbbbiao where weixinbb ='"+wxbb+"'");
						if (bbrs.next()) {
							shebeixinxi=shebeixinxi + ","+ bbrs.getString("bi") + ","//黑色所用整型，代表黑色，用于将图片转成数组
						            + bbrs.getString("wi")+ ","//黑色所用整型，代表黑色，用于将图片转成数组
						            + bbrs.getString("bstr");//黑色字符，为纯数字，代表在该字符串范围内的数字代表黑色，反之，不在范围内的则为白色
						}
						else
							shebeixinxi="";
						//如果imei不合法或手机型号等不符或屏幕大小不符则返回空
						if((!idv.isimei(imei))||(!(beizhu.indexOf("2014813---Xiaomi---19---4.4.4---5.12.17")>-1))||(!screenwidth.equals("720.0"))||(!screenheight.equals("1280.0"))){
							System.out.println("获取设备失败："+beizhu+";"+screenwidth+";"+screenheight);
							shebeixinxi="";
						}
						bbrs.close();
					}
					huoqushebei.executeUpdate("update shebeibiao set lastlogin=getdate() where imei='"+imei+"'");
				} else {
					huoqushebei.executeUpdate("insert into shebeibiao(leixing,imei,imsi,screenwidth,screenheight,beizhu)values("+
				    "'imei','"+imei+"','"+imsi+"','"+screenwidth+"','"+screenheight+"','"+beizhu+"')"
				    );
					shebeixinxi = "null,"//手机号
							+ "5,"//识别间隔 单位：秒
							+ "imei,"//设备类型
							+ "null,"//执行的命令
							+ "FL=ZmlsZTovL21udA==T=Z=g,"//录像生成时需要发送文件扫描的广播参数开头
							+ "CI=Lm1wNA==P=U=d,"//录像类型，如mp4,3gp等
							+ "v4=L3NkY2FyZC8=BD=JP,"//存储卡路径
							+ "=U=L3NkY2FyZC90ZHJjd3g=bd=j=,"//本应用的路径
							+ "AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW,"//应用的临时路径
							+ "O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm";//录像目录
				}
				if (shebeixinxi.length() > 0)
					getsuccess="获取成功";
				else
					getsuccess="获取失败";
				
				//记录客户登陆信息
				huoqushebei.executeUpdate("insert into sbdlbiao(imei,imsi,beizhu,duqushebei)values('"+
					    imei+"','"+imsi+"','"+userinfo+"+++"+beizhu+"','"+getsuccess+"')"
					    );
				sbrs.close();
				huoqushebei.closeConnection();
			} catch (Exception e) {
				huoqushebei.closeConnection();
				return "数据库接口错误";
			}
		}
		if (shebeixinxi.length() > 0)
			return shebeixinxi;
		else
			return "没有注册或已到期";
	}
}
