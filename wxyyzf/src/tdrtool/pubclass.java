package tdrtool;
import java.io.UnsupportedEncodingException;
import java.io.File;
public class pubclass {
	public String toChinese(String str){		//����ת������ķ���
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
	public String getwhereconvertlikesql(String sqlwhere,String columnname,String columnvalue){//��like��ʽ��ȡsql�����where����
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" CONVERT(varchar(100), "+columnname+", 20) like '%"+columnvalue+"%'";}else{sqlwhere=sqlwhere+" and  CONVERT(varchar(100), "+columnname+", 20) like '%"+columnvalue+"%'";}
			}
		return sqlwhere;
	}
	public String getwherelikesql(String sqlwhere,String columnname,String columnvalue){//��like��ʽ��ȡsql�����where����
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" "+columnname+" like '%"+columnvalue+"%'";}else{sqlwhere=sqlwhere+" and "+columnname+" like '%"+columnvalue+"%'";}
			}
		return sqlwhere;
	}
	public String getwhereequssql(String sqlwhere,String columnname,String columnvalue){//�Ե��ڷ�ʽ��ȡsql�����where����
		if((columnvalue!=null)&&(columnvalue.length()>0)){
			  if(sqlwhere.length()<=0){sqlwhere=sqlwhere+" "+columnname+" = '"+columnvalue+"'";}else{sqlwhere=sqlwhere+" and "+columnname+" = '"+columnvalue+"'";}
			}
		return sqlwhere;
	}
	public String getupdatesetsql(String updatesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnname,String columnvalue){//��Update,set��ʽ��ȡsql�����set���沿��
		if ((loginusergongnengstr.indexOf(xianshistr) >= 0)&&(loginusergongnengstr.indexOf(baocunstr) >= 0)) {
			if(updatesql.length()<=0){updatesql=updatesql+columnname+"='"+ columnvalue+ "'";}else{
				updatesql=updatesql+","+columnname+"='"+ columnvalue+ "'";
			}
		}
		return updatesql;
	}
	public String getinsertcolumnnamesql(String insertcolumnnamesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnname){//��Update,set��ʽ��ȡsql�����set���沿��
		if ((loginusergongnengstr.indexOf(xianshistr) >= 0)&&(loginusergongnengstr.indexOf(baocunstr) >= 0)) {
			if(insertcolumnnamesql.length()<=0){insertcolumnnamesql=insertcolumnnamesql+columnname;}else{
				insertcolumnnamesql=insertcolumnnamesql+","+columnname;
			}
		}
		return insertcolumnnamesql;
	}
	public String getinsertcolumnvaluesql(String insertcolumnnamesql,String loginusergongnengstr,String xianshistr,String baocunstr,String columnvalue){//��Update,set��ʽ��ȡsql�����set���沿��
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
