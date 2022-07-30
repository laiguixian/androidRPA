package tdrtool;
import java.io.UnsupportedEncodingException;
import java.io.File;
public class pubclass {
	public String toChinese(String str){		//进行转码操作的方法
		if(str==null)
			str="";
		try {
			str=new String(str.getBytes("ISO-8859-1"),"gb2312");
		} catch (UnsupportedEncodingException e) {
			str="";
			e.printStackTrace();
		}
		return str;
	}
	public String getwhereconvertlikesql(String sqlwhere,String columnname,String columnvalue){//以like方式获取sql语句中where部分
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" CONVERT(varchar(100), "+columnname+", 20) like '%"+columnvalue+"%'";}else{sqlwhere=sqlwhere+" and  CONVERT(varchar(100), "+columnname+", 20) like '%"+columnvalue+"%'";}
			}
		return sqlwhere;
	}
	public String getwherelikesql(String sqlwhere,String columnname,String columnvalue){//以like方式获取sql语句中where部分
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" "+columnname+" like '%"+columnvalue+"%'";}else{sqlwhere=sqlwhere+" and "+columnname+" like '%"+columnvalue+"%'";}
			}
		return sqlwhere;
	}
	public String getwhereequssql(String sqlwhere,String columnname,String columnvalue){//以等于方式获取sql语句中where部分
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" "+columnname+" = '"+columnvalue+"'";}else{sqlwhere=sqlwhere+" and "+columnname+" = '"+columnvalue+"'";}
			}
		return sqlwhere;
	}
	public String getupdatesetsql(String updatesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnname,String columnvalue){//以Update,set方式获取sql语句中set后面部分
		if ((loginusergongnengstr.indexOf(xianshistr) >= 0)&&(loginusergongnengstr.indexOf(baocunstr) >= 0)) {
			if(updatesql.length()<=0){updatesql=updatesql+columnname+"='"+ columnvalue+ "'";}else{
				updatesql=updatesql+","+columnname+"='"+ columnvalue+ "'";
			}
		}
		return updatesql;
	}
	public String getinsertcolumnnamesql(String insertcolumnnamesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnname){//以Update,set方式获取sql语句中set后面部分
		if ((loginusergongnengstr.indexOf(xianshistr) >= 0)&&(loginusergongnengstr.indexOf(baocunstr) >= 0)) {
			if(insertcolumnnamesql.length()<=0){insertcolumnnamesql=insertcolumnnamesql+columnname;}else{
				insertcolumnnamesql=insertcolumnnamesql+","+columnname;
			}
		}
		return insertcolumnnamesql;
	}
	public String getinsertcolumnvaluesql(String insertcolumnnamesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnvalue){//以Update,set方式获取sql语句中set后面部分
		if ((loginusergongnengstr.indexOf(xianshistr) >= 0)&&(loginusergongnengstr.indexOf(baocunstr) >= 0)) {
			if(insertcolumnnamesql.length()<=0){insertcolumnnamesql=insertcolumnnamesql+"'"+ columnvalue+ "'";}else{
				insertcolumnnamesql=insertcolumnnamesql+",'"+ columnvalue+ "'";
			}
		}
		return insertcolumnnamesql;
	}
    public void deletefile(String filename){
    	File file = new File(filename);
		if (file.exists()) {file.delete();}
    }
}
