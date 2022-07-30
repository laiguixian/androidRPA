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
		String getsuccess="获取失败";
		if (((imei.length() > 0) | (imsi.length() > 0))
				&& (shujubanben.length() > 0)) {
			Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
			// DateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置时间格式
			// String huoqushijian=df.format(dt);
			databasebean huoquxinxi = new databasebean();
			ResultSet sbrs;
			try {
				sbrs = huoquxinxi
						.executeQueryscroll("select * from shebeibiao where (imei='"
								+ imei + "') order by zhujian desc");
				if (sbrs.next()) {
					if (sbrs.getDate("daoqishijian").before(dt))
						returnxinxi = "注册到期";
					else {
						String beizhu=sbrs.getString("beizhu").trim();//获取备注里的设备相关版本信息
						float screenwidth=sbrs.getFloat("screenwidth");//获取屏幕宽度
						float screenheight=sbrs.getFloat("screenheight");//获取屏幕高度
						ResultSet mbrs;
						try {
							returnxinxi="";	
							mbrs = huoquxinxi.executeQueryscroll("select * from mubanbiao where weixinbb='"+weixinbanben+"' and isactive=1 order by zhujian");
							while (mbrs.next()) {
								returnxinxi = returnxinxi+mbrs.getString("daihao") + ","//模板代号
								+ mbrs.getString("canshu") + ","//识别参数
								+ mbrs.getString("picstr") + ","//图片字符串
								+ mbrs.getString("leixing")+";";//模板类型
							}
							if(returnxinxi.length()>0){
								returnxinxi=returnxinxi.substring(0, returnxinxi.length()-1);
								//如果imei不合法或手机型号等不符或屏幕大小不符则返回空
								if((!idv.isimei(imei))||(!(beizhu.indexOf("2014813---Xiaomi---19---4.4.4---5.12.17")>-1))||(screenwidth!=720.0)||(screenheight!=1280.0)){//如果imei不合法
									System.out.println("获取模板失败："+beizhu+";"+screenwidth+";"+screenheight);
									returnxinxi="";
								}
							}
							else
								return "无记录";
							mbrs.close();
							huoquxinxi.executeUpdate("insert into logbiao(imsi,imei,logstr) values('"+imsi+"','"+imei+"','"+logstr+"')");
						} catch (Exception e) {
							huoquxinxi.closeConnection();
							return "数据库接口2错误";
						}
					}
				} else {
					return "无记录";
				}
				sbrs.close();
				if (returnxinxi.length() > 0)
					getsuccess="获取成功";
				else
					getsuccess="获取失败";
				//记录客户登陆信息
				huoquxinxi.executeUpdate("update sbdlbiao set duqumuban='"+getsuccess+"' where zhujian=(select max(zhujian) from sbdlbiao where imsi='"+imsi+"' and imei='"+imei+"')");
				huoquxinxi.closeConnection();
			} catch (Exception e) {
				huoquxinxi.closeConnection();
				return "数据库接口1错误";
			}
		}
		if (returnxinxi.length() > 0)
			return returnxinxi;
		else
			return "没有注册或已到期";
	}
}
