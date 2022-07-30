package tdrtool;

import java.io.File;
import java.net.URLDecoder;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tdrtool.IniReader;

public class databasebean {
	private String dbdriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 连接sql数据库的驱动
	private String url = "jdbc:sqlserver://localhost:1433;DatabaseName=tdrdingwei";// 连接sql数据库的字串
	private String server = "localhost:1433";// 连接sql数据库的字串
	private String database = "tdrdingwei";// 连接sql数据库的字串
	private String username = "sa";// 连接sql数据库的数据库用户名
	private String password = "123";// 连接sql数据库的数据库密码
	private Connection con = null;

	public databasebean() {
		try {
			// System.out.println(System.getProperty("user.dir"));
			// File directory = new File(".");
			// System.out.println(directory.getCanonicalPath()); //得到的是C:\test
			// String path=System.getProperty("user.dir");
			String path = URLDecoder.decode(this.getClass().getResource("/")
					.getPath(), "gb2312");// 获取class存放目录
			path = path.toUpperCase();
			path = path.substring(0, path.lastIndexOf("CLASSES") - 1);
			if (path.substring(path.length() - 1).equals("\\") == false) {
				path = path + "\\";
			}
			// System.out.println(directory.getAbsolutePath());
			// System.out.println(path+"\\cdxtbbxtconfig.ini");
			IniReader reader = new IniReader(path + "\\appconfig.ini");// 获取存放数据库设置
																			// 及系统设置的相关文件
			// IniReader reader = new
			// IniReader("hrdlgcglconfig.ini");//获取存放数据库设置 及系统设置的相关文件
			// System.out.println(reader.getValue("Option", "OracleDB"));
			dbdriver = reader.getValue("databasecon", "dbdriver");
			server = reader.getValue("databasecon", "server");// 连接sql数据库的字串
			database = reader.getValue("databasecon", "database");// 连接sql数据库的字串
			username = reader.getValue("databasecon", "username");// 连接sql数据库的数据库用户名
			password = reader.getValue("databasecon", "password");
			// System.out.println(inireadwrite.getProfileString(path+"\\cdxtbbxtconfig.ini",
			// "Option", "OracleDB", "default"));
			Class.forName(dbdriver).newInstance(); // 加载数据库驱动
		} catch (Exception ex) {
			System.out.println("系统出错，数据库加载失败！" + ex.getMessage());
		}
	}

	// 创建带特征值后缀的数据库连接
	public boolean creatspecConnection(String spec)// spec：特征值，可能为年份，也可能直接就是数据库，当为年份时使用数据库为系统数据库名称+年份，否则为使用数据库直接为spec值
	{
		try {
			if (spec.equals("master")) {
				url = "jdbc:sqlserver://" + server + ";DatabaseName=master";
			} else {
				url = "jdbc:sqlserver://" + server + ";DatabaseName="
						+ database + spec;
			}
			con = DriverManager.getConnection(url, username, password);
			con.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("系统出错，创建数据库连接时出错!");
		}
		return true;
	}

	// 创建普通的数据库连接
	public boolean creatnorConnection()// spec：特征值，可能为年份，也可能直接就是数据库，当为年份时使用数据库为系统数据库名称+年份，否则为使用数据库直接为spec值
	{
		try {
			url = "jdbc:sqlserver://" + server + ";DatabaseName=" + database;
			con = DriverManager.getConnection(url, username, password);
			con.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("系统出错，创建数据库连接时出错!");
		}
		return true;
	}

	// 对数据库的增加、修改和删除的操作
	public boolean executeUpdate(String sql)// spec：特征值，可能为年份，也可能直接就是数据库，当为年份时使用数据库为系统数据库名称+年份，否则为使用数据库直接为spec值
	{
		if (con == null) {
			creatnorConnection();
		}
		try {
			Statement stmt = con.createStatement();
			int iCount = stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("操作成功，所影响的记录数为" + String.valueOf(iCount));
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("系统出错，执行更新时出错，SQL语句为：" + sql + "!");
			return false;
		}
	}

	// 对数据库的查询操作 不可滚动
	public ResultSet executeQueryscroll(String sql)// spec：特征值，可能为年份，也可能直接就是数据库，当为年份时使用数据库为系统数据库名称+年份，否则为使用数据库直接为spec值
	{
		ResultSet rs;
		try {
			if (con == null) {
				creatnorConnection();
			}
			Statement stmt = con
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			try {
				rs = stmt.executeQuery(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return null;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("系统出错，执行查询时出错，SQL语句为：" + sql + "!");
			return null;
		}
		return rs;
	}

	// 对数据库的查询操作 不可滚动
	public ResultSet executeQuery(String sql)// spec：特征值，可能为年份，也可能直接就是数据库，当为年份时使用数据库为系统数据库名称+年份，否则为使用数据库直接为spec值
	{
		ResultSet rs;
		try {
			if (con == null) {
				creatnorConnection();
			}
			Statement stmt = con.createStatement();
			try {
				rs = stmt.executeQuery(sql);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return null;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("系统出错，执行查询时出错，SQL语句为：" + sql + "!");
			return null;
		}
		return rs;
	}

	// 关闭数据库的操作
	public void closeConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("系统出错，关闭数据库连接时出错!");
			} finally {
				con = null;
			}
		}
	}
}
