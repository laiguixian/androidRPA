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
	private String dbdriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // ����sql���ݿ������
	private String url = "jdbc:sqlserver://localhost:1433;DatabaseName=tdrdingwei";// ����sql���ݿ���ִ�
	private String server = "localhost:1433";// ����sql���ݿ���ִ�
	private String database = "tdrdingwei";// ����sql���ݿ���ִ�
	private String username = "sa";// ����sql���ݿ�����ݿ��û���
	private String password = "123";// ����sql���ݿ�����ݿ�����
	private Connection con = null;

	public databasebean() {
		try {
			// System.out.println(System.getProperty("user.dir"));
			// File directory = new File(".");
			// System.out.println(directory.getCanonicalPath()); //�õ�����C:\test
			// String path=System.getProperty("user.dir");
			String path = URLDecoder.decode(this.getClass().getResource("/")
					.getPath(), "gb2312");// ��ȡclass���Ŀ¼
			path = path.toUpperCase();
			path = path.substring(0, path.lastIndexOf("CLASSES") - 1);
			if (path.substring(path.length() - 1).equals("\\") == false) {
				path = path + "\\";
			}
			// System.out.println(directory.getAbsolutePath());
			// System.out.println(path+"\\cdxtbbxtconfig.ini");
			IniReader reader = new IniReader(path + "\\appconfig.ini");// ��ȡ������ݿ�����
																			// ��ϵͳ���õ�����ļ�
			// IniReader reader = new
			// IniReader("hrdlgcglconfig.ini");//��ȡ������ݿ����� ��ϵͳ���õ�����ļ�
			// System.out.println(reader.getValue("Option", "OracleDB"));
			dbdriver = reader.getValue("databasecon", "dbdriver");
			server = reader.getValue("databasecon", "server");// ����sql���ݿ���ִ�
			database = reader.getValue("databasecon", "database");// ����sql���ݿ���ִ�
			username = reader.getValue("databasecon", "username");// ����sql���ݿ�����ݿ��û���
			password = reader.getValue("databasecon", "password");
			// System.out.println(inireadwrite.getProfileString(path+"\\cdxtbbxtconfig.ini",
			// "Option", "OracleDB", "default"));
			Class.forName(dbdriver).newInstance(); // �������ݿ�����
		} catch (Exception ex) {
			System.out.println("ϵͳ�������ݿ����ʧ�ܣ�" + ex.getMessage());
		}
	}

	// ����������ֵ��׺�����ݿ�����
	public boolean creatspecConnection(String spec)// spec������ֵ������Ϊ��ݣ�Ҳ����ֱ�Ӿ������ݿ⣬��Ϊ���ʱʹ�����ݿ�Ϊϵͳ���ݿ�����+��ݣ�����Ϊʹ�����ݿ�ֱ��Ϊspecֵ
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
			System.out.println("ϵͳ�����������ݿ�����ʱ����!");
		}
		return true;
	}

	// ������ͨ�����ݿ�����
	public boolean creatnorConnection()// spec������ֵ������Ϊ��ݣ�Ҳ����ֱ�Ӿ������ݿ⣬��Ϊ���ʱʹ�����ݿ�Ϊϵͳ���ݿ�����+��ݣ�����Ϊʹ�����ݿ�ֱ��Ϊspecֵ
	{
		try {
			url = "jdbc:sqlserver://" + server + ";DatabaseName=" + database;
			con = DriverManager.getConnection(url, username, password);
			con.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("ϵͳ�����������ݿ�����ʱ����!");
		}
		return true;
	}

	// �����ݿ�����ӡ��޸ĺ�ɾ���Ĳ���
	public boolean executeUpdate(String sql)// spec������ֵ������Ϊ��ݣ�Ҳ����ֱ�Ӿ������ݿ⣬��Ϊ���ʱʹ�����ݿ�Ϊϵͳ���ݿ�����+��ݣ�����Ϊʹ�����ݿ�ֱ��Ϊspecֵ
	{
		if (con == null) {
			creatnorConnection();
		}
		try {
			Statement stmt = con.createStatement();
			int iCount = stmt.executeUpdate(sql);
			System.out.println(sql);
			System.out.println("�����ɹ�����Ӱ��ļ�¼��Ϊ" + String.valueOf(iCount));
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("ϵͳ����ִ�и���ʱ����SQL���Ϊ��" + sql + "!");
			return false;
		}
	}

	// �����ݿ�Ĳ�ѯ���� ���ɹ���
	public ResultSet executeQueryscroll(String sql)// spec������ֵ������Ϊ��ݣ�Ҳ����ֱ�Ӿ������ݿ⣬��Ϊ���ʱʹ�����ݿ�Ϊϵͳ���ݿ�����+��ݣ�����Ϊʹ�����ݿ�ֱ��Ϊspecֵ
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
			System.out.println("ϵͳ����ִ�в�ѯʱ����SQL���Ϊ��" + sql + "!");
			return null;
		}
		return rs;
	}

	// �����ݿ�Ĳ�ѯ���� ���ɹ���
	public ResultSet executeQuery(String sql)// spec������ֵ������Ϊ��ݣ�Ҳ����ֱ�Ӿ������ݿ⣬��Ϊ���ʱʹ�����ݿ�Ϊϵͳ���ݿ�����+��ݣ�����Ϊʹ�����ݿ�ֱ��Ϊspecֵ
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
			System.out.println("ϵͳ����ִ�в�ѯʱ����SQL���Ϊ��" + sql + "!");
			return null;
		}
		return rs;
	}

	// �ر����ݿ�Ĳ���
	public void closeConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("ϵͳ�����ر����ݿ�����ʱ����!");
			} finally {
				con = null;
			}
		}
	}
}
