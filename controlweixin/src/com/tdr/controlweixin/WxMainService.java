package com.tdr.controlweixin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.util.EncodingUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.color;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TabActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import com.tdr.controlweixin.tm;
import com.tdr.controlweixin.MainActivity;
import com.tdr.controlweixin.MediaScanner;

@SuppressLint("NewApi")
public class WxMainService extends Service {
	// ��ʱ��
	private Timer tm1 = null;// ��ʱ��
	// ��ʱ�߳�
	private TimerTask tm1t = null;// ʱ������
	// ��λʱ����
	private int tm1s = 10 * 1000;// ʱ����
	private String ei = "";// �����ƶ��û�ʶ����(IMEI�����ֻ�����)
	private String si = "";// �ƶ��豸���������(��sim��Ψһ��Ӧ)
	private String sjh = "";// �ֻ���
	private String dsb = "";// ��ȡ�豸���ص���Ϣ
	private String slx = "";// �豸����
	String mlg = "";// ִ������磺����jietu��ȫ�̽�ͼ����:����jieduan���жϽ׶�
	float swh = 0;// �ֻ���Ļ���
	float sht = 0;// �ֻ���Ļ�߶�
	float sswh = 432;// ��ͼ��Ļ���
	float ssht = 768;// ��ͼ��Ļ�߶�
	float hxbl = 0;// ������Ļ�ͽ�ͼ����
	float zxbl = 0;// ������Ļ�ͽ�ͼ����
	private Thread dmt; // ��������΢����Ϣ���̣߳�dealwxmsgThread
	private boolean dmtc = true; // ����΢����Ϣ���߳��Ƿ�������У�dealwxmsgThreadcon
	private boolean dmti = false; // ���봦��΢����Ϣ���̵߳ı�־��dealwxmsgThreadin
	private AudioManager aum;// ��Ƶ��������audioManager
	private int bi;// ��ɫ�����������������ɫ�����ڽ�ͼƬת������
	private int wi;// ��ɫ�����������������ɫ�����ڽ�ͼƬת������
	private String bstr;// ��ɫ�ַ���Ϊ�����֣������ڸ��ַ�����Χ�ڵ����ִ����ɫ����֮�����ڷ�Χ�ڵ���Ϊ��ɫ
	boolean dig = false;// ����ִ�м�ʱ��1������dotime1ing
	boolean ceshi = false;// ���Ա�־�����ڿ�������Ҫ�����⶯���ı�־
	// �豸��Ϣ�Ƿ��Ѿ�����
	private boolean dis = false;// �Ѿ������豸���ţ�deviceinfosend
	ArrayList jba = new ArrayList();// �жϽ׶���ͼƬģ���б�jieduanbitmapay
	ArrayList dba = new ArrayList();// ��λ����������ͼƬģ���б�dingweibitmapay
	Point tp1 = null;// �����1���ڶ�λ��ʶ��temppoint1
	Point tp2 = null;// �����2���ڶ�λ��ʶ��temppoint2
	Point tp3 = null;// �����3���ڶ�λ��ʶ��temppoint3

	MediaRecorder mr;// ¼�����mediarecorder
	private String vex = "";// ¼�����ͣ�videoext
	boolean iri = false;// �Ƿ����ڼ�¼��¼�񣩣� isRecording
	String lvfn = "";// �ϴ�ʹ�õ�¼��ȫ·����lastvideoFileName

	String fmt = "";// ¼������ʱ��Ҫ�����ļ�ɨ��Ĺ㲥��filemnt
	String scp = "";// �洢��·����sdcardpath
	String cwxp = "";// ��Ӧ�õ�·����controlweixinpath
	String cwxtp = "";// Ӧ�õ���ʱ·����controlweixintemppath
	String cwxmp = "";// ¼��Ŀ¼��controlweixinmoviespath

	String logstr="";//��־�ַ�
	
	// ���µ��ַ����ܶ������Լ��������������ר�õļ��ܹ���

	// �����ռ�
	// ��ȡ�豸��Ϣ��Webservice�ġ�nameSpace = "http://wxyyzf/";���ļ����ַ���
	String rdns = "j=uaHR0cDovL3d4eXl6Zi8=y=35A";

	// ���õķ�������
	// ��ȡ�豸��Ϣ��Webservice�ġ�methodName = "huoqushebeixinxi";���ļ����ַ���
	String rdmn = "g==aHVvcXVzaGViZWl4aW54aQ==vx24B";

	// EndPoint
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://taiderui.com:8883/wxyyzf/shebeibiaoPort";���ļ����ַ���
	String rdep = "Ye=aHR0cDovL3RhaWRlcnVpLmNvbTo4ODgzL3d4eXl6Zi9zaGViZWliaWFvUG9ydA==np=t0";
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.100:8080/wxyyzf/shebeibiaoPort";���ļ����ַ���
	// String
	// rdep="w3=aHR0cDovLzE5Mi4xNjguMS4xMDA6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=CD=JP";
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.101:8080/wxyyzf/shebeibiaoPort";���ļ����ַ���
	// String
	// rdep="=suaHR0cDovLzE5Mi4xNjguMS4xMDE6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=yy=37";
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.102:8080/wxyyzf/shebeibiaoPort";���ļ����ַ���
	// String
	// rdep="==JaHR0cDovLzE5Mi4xNjguMS4xMDI6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=P=T=d";
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.103:8080/wxyyzf/shebeibiaoPort";���ļ����ַ���
	// String
	// rdep="=bhaHR0cDovLzE5Mi4xNjguMS4xMDM6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=km=qw";
	// ��ȡ�豸��Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.104:8080/wxyyzf/shebeibiaoPort";���ļ����ַ���
	// String
	// rdep="=LRaHR0cDovLzE5Mi4xNjguMS4xMDQ6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=VXZ=g";

	// SOAP Action
	// ��ȡ�豸��Ϣ��Webservice�ġ�soapAction = "http://wxyyzf/huoqushebeixinxi";���ļ����ַ���
	String rdsa = "X=haHR0cDovL3d4eXl6Zi9odW9xdXNoZWJlaXhpbnhpkmo=w";

	// �����ռ�
	// ��ȡģ����Ϣ��Webservice�ġ�nameSpace = "http://wxyyzf/";���ļ����ַ���
	String rmns = "J=TaHR0cDovL3d4eXl6Zi8===ZZf";

	// ���õķ�������
	// ��ȡģ����Ϣ��Webservice�ġ�methodName = "huoqumuban";���ļ����ַ���
	String rmmn = "=g=aHVvcXVtdWJhbg==p=t==";

	// EndPoint
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://taiderui.com:8883/wxyyzf/mubanbiaoPort";���ļ����ַ���
	String rmep = "sy5aHR0cDovL3RhaWRlcnVpLmNvbTo4ODgzL3d4eXl6Zi9tdWJhbmJpYW9Qb3J08ACGK";
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.100:8080/wxyyzf/mubanbiaoPort";���ļ����ַ���
	// String
	// rmep="=ntaHR0cDovLzE5Mi4xNjguMS4xMDA6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA===0==B";
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.101:8080/wxyyzf/mubanbiaoPort";���ļ����ַ���
	// String
	// rmep="=79aHR0cDovLzE5Mi4xNjguMS4xMDE6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA====EGK";
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.102:8080/wxyyzf/mubanbiaoPort";���ļ����ַ���
	// String
	// rmep="=puaHR0cDovLzE5Mi4xNjguMS4xMDI6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==0==7=";
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.103:8080/wxyyzf/mubanbiaoPort";���ļ����ַ���
	// String
	// rmep="=7=aHR0cDovLzE5Mi4xNjguMS4xMDM6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==G=KMS";
	// ��ȡģ����Ϣ��Webservice�ġ�endPoint =
	// "http://192.168.1.104:8080/wxyyzf/mubanbiaoPort";���ļ����ַ���
	// String
	// rmep="=7=aHR0cDovLzE5Mi4xNjguMS4xMDQ6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==G=KMS";

	// SOAP Action
	// ��ȡģ����Ϣ��Webservice�ġ�soapAction = "http://wxyyzf/huoqumuban";���ļ����ַ���
	String rmsa = "2=CaHR0cDovL3d4eXl6Zi9odW9xdW11YmFu=I=NV";

	// �ر�ָ���Ƿ���Դ���������isselfclose
	private boolean isf = false;
	// �Ƿ�����ִ������istasking
	private boolean iti = false;

	// ���帡�����ڲ��֣�floatLayout
	LinearLayout flt;
	WindowManager.LayoutParams wmp;// wmParams
	// ���������������ò��ֲ����Ķ���
	WindowManager wmr;// windowmanager

	Button mvbt;// ����¼����Ƶ��ť��makevideobt
	CheckBox aschk;// �����Զ����͹�ѡ��autosendchk
	Button svbt;// ����������Ƶ��ť��sendvideobt
	Button ebt;// �����رհ�ť��exitbt
	SurfaceView vrv;// ¼����ʾ���棺videorecordView
	TextView stv;// ״̬��ʾ��statustv

	int[][] lsay = null;// ���һ�ε���Ļͼ�����飺lastscreenay
	Camera cam = null;// �����������ת

	String sjxh = "";// �ֻ��ͺţ�shoujixinghao
	String sjpp = "";// �ֻ�Ʒ�ƣ�shoujipinpai
	String sdkbb = "";// SDK�汾��sdkbanben
	String xtbb = "";// ϵͳ�汾��xitongbanben
	String mubb = "";// MIUI�汾��miuibanben

	// ���ַ�������ʾֻ��ʹ��һ��
	/*
	 * Looper.prepare(); Toast.makeText(getApplicationContext(), "��Ϣ1",
	 * 1).show(); Looper.loop();// ����loop�е�ѭ�����鿴��Ϣ����
	 */
	// Thread.currentThread().sleep(10000);��ռ��cpu�����Բ���
	// Thread.sleep(10000);��ռ��cpu�����Բ���

	@SuppressLint({ "ShowToast", "NewApi" })
	@Override
	public void onCreate() {
		// ��ȡ�洢��·��
		// sdp=Environment.getExternalStorageDirectory().toString()+"/";
		try {
			super.onCreate();

			aum = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			android.view.Display display = wm.getDefaultDisplay();
			Point pt = new Point();
			display.getRealSize(pt);

			sjxh = android.os.Build.MODEL;// �ֻ��ͺţ�shoujixinghao
			sjpp = android.os.Build.BRAND;// �ֻ�Ʒ�ƣ�shoujipinpai
			sdkbb = android.os.Build.VERSION.SDK;// SDK�汾��sdkbanben
			xtbb = android.os.Build.VERSION.RELEASE;// ϵͳ�汾��xitongbanben
			mubb = android.os.Build.VERSION.INCREMENTAL;// MIUI�汾��miuibanben

			/*
			 * Toast.makeText(this, pt.x+","+pt.y, 1).show();
			 * //��ͼ�жϴ�С�����Դ������ͼ��ֱ��ʣ�����Ϊ�ֻ��ֱ��� //��ͼ
			 * edb("/system/bin/screencap -p " + tct+"/getsc.png"); //����ͼƬ
			 * BitmapFactory.Options options = new BitmapFactory.Options();
			 * options.inJustDecodeBounds = false; Bitmap tempBitmap =
			 * BitmapFactory.decodeFile(tct+"/getsc.png", options); //��ȡͼƬ��Ⱥ͸߶�
			 * swh = tempBitmap.getWidth();//�ֻ���Ļ��� sht =
			 * tempBitmap.getHeight();//�ֻ���Ļ�߶� //Toast.makeText(this,
			 * screenwidth+","+screenheight, 5000).show();
			 */
			swh = pt.x;// �ֻ���Ļ���
			sht = pt.y;// �ֻ���Ļ�߶�
			// ��ȡ�ֱ��ʺͽ�ͼ�߿����
			hxbl = swh / sswh;// ������Ļ�ͽ�ͼ����
			zxbl = sht / ssht;// ������Ļ�ͽ�ͼ����
			
			if ((hxbl > 0) && (zxbl > 0)) {
				// ��ȡimei��imsi
				boolean gds = false;// ��ȡ���ݳɹ���getdatasuccess
				TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				si = TelephonyMgr.getSubscriberId();// �ƶ��豸���������(��sim��Ψһ��Ӧ)
				if ((si == null) || (si.length() <= 0))
					si = "000000000000000";
				ei = TelephonyMgr.getDeviceId();// �����ƶ��û�ʶ����(IMEI�����ֻ�����)
				if (si.length() > 0 && ei.length() > 0) {
					rdv();// ��ȡ�豸��Ϣ
					// Toast.makeText(this, rdv(), 1).show();
					// if(shoujihao.length()>0){
					/*
					File logf = new File(cwxp + "/runlog.txt");
					if (logf.exists()){
						logstr=ReadTxtFile(cwxp + "/runlog.txt");
						if(logstr.length()>4000)
							logstr=logstr.substring(3001);
					}
					else{
						logf.createNewFile();
			         }
			         */
					File tdrcw = new File(cwxp);
					if (!tdrcw.exists())
						tdrcw.mkdir();
					// �жϴ洢��·���±�Ӧ�õ���ʱĿ¼�Ƿ���ڣ��������򴴽�
					File tdrcwtemp = new File(cwxtp);
					if (!tdrcwtemp.exists())
						tdrcwtemp.mkdir();
					// �жϴ洢��·���±�Ӧ�õ�¼��Ŀ¼�Ƿ���ڣ��������򴴽�
					File tdrcwmov = new File(cwxmp);
					if (!tdrcwmov.exists())
						tdrcwmov.mkdir();
					File logf = new File(cwxp + "/runlog.txt");
					if (!logf.exists())
						logf.createNewFile();
					rmb();// ��ȡģ����Ϣ
					// Toast.makeText(this, "b:"+bi+";w:"+wi+";bstr:"+bstr,
					// 5000).show();//readmuban();//��ȡģ����Ϣ
					gds = (jba.size() > 0) && (fmt.length() > 0);// �����ȡ�Ľ׶�ģ��������0������ȡ���ݳɹ�
					// if((jba.size()>0)&&(dba.size()>0)){
					// �жϴ洢��·���±�Ӧ�õ���ʱĿ¼�Ƿ���ڣ��������򴴽�

					if (gds) {
						// ����������
						createFloatView();

						Toast.makeText(this, dsr("l=s5ZCv5Yqo5oiQ5Yqfu==y3"), 1)
								.show();
						if ((ceshi)
								|| ((mlg != null) && (mlg.length() > 0) && (mlg
										.indexOf("jieduan") > -1))) {
							stt();// ��ʼʶ������
						}
					} else
						Toast.makeText(this, dsr("J=T5ZCv5Yqo5aSx6LSlXZ=f="), 1)
								.show();

					// stt();//��ʼʶ������
					// Toast.makeText(this, "�����ɹ�", 1).show();
				}
			}
			// LinearLayout
			// linearLayout=(LinearLayout)findViewByid(R.id.main_container);
			// WindowManager wmr;//=new WindowManager();
			// wmr.toString().getDefaultDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ��ȡ΢�Ű汾����getweixinversion����д��gwxv
	 */
	public String gwxv() {
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);
		String weixinbanbenming="";
		for (int i = 0; i < packages.size(); i++) {

			PackageInfo packageInfo = packages.get(i);

			AppInfo tmpInfo = new AppInfo();

			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();

			tmpInfo.packageName = packageInfo.packageName;

			tmpInfo.versionName = packageInfo.versionName;

			tmpInfo.versionCode = packageInfo.versionCode;

			tmpInfo.appIcon = packageInfo.applicationInfo
					.loadIcon(getPackageManager());

			if(tmpInfo.appName.equals("΢��"))
				weixinbanbenming=tmpInfo.versionCode+"---"+tmpInfo.versionName;

		}// ���� �����ֻ��ϰ�װ��Ӧ�����ݶ�����appList���ˡ�
		return weixinbanbenming;
	}
	
	/*
	 * ��ȡӦ���б�getapplist����д��gal
	 */
	public ArrayList<AppInfo> gal() {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // �����洢��ȡ��Ӧ����Ϣ����
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {

			PackageInfo packageInfo = packages.get(i);

			AppInfo tmpInfo = new AppInfo();

			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();

			tmpInfo.packageName = packageInfo.packageName;

			tmpInfo.versionName = packageInfo.versionName;

			tmpInfo.versionCode = packageInfo.versionCode;

			tmpInfo.appIcon = packageInfo.applicationInfo
					.loadIcon(getPackageManager());

			appList.add(tmpInfo);

		}// ���� �����ֻ��ϰ�װ��Ӧ�����ݶ�����appList���ˡ�
		return appList;
	}

	// ������������
	private void createFloatView() {
		wmp = new WindowManager.LayoutParams();
		// ��ȡWindowManagerImpl.CompatModeWrapper
		wmr = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		// ����window type
		wmp.type = LayoutParams.TYPE_PHONE;
		// ����ͼƬ��ʽ��Ч��Ϊ����͸��
		wmp.format = PixelFormat.RGBA_8888;
		// ���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
		wmp.flags =
		// LayoutParams.FLAG_NOT_TOUCH_MODAL |
		LayoutParams.FLAG_NOT_FOCUSABLE
		// LayoutParams.FLAG_NOT_TOUCHABLE
		;

		// ������������ʾ��ͣ��λ��Ϊ����ö�
		wmp.gravity = Gravity.LEFT | Gravity.TOP;

		// ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
		wmp.x = 1;
		wmp.y = 105;

		// �����������ڳ�������
		// wmp.width = 40;
		// wmp.height = 330;

		// �����������ڳ�������
		wmp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// ��ȡ����������ͼ���ڲ���
		flt = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
		// ���mFloatLayout
		wmr.addView(flt, wmp);
		/*
		 * Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
		 * Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
		 * Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop()); Log.i(TAG,
		 * "mFloatLayout-->bottom" + mFloatLayout.getBottom());
		 */
		mvbt = (Button) flt.findViewById(R.id.makevideobt);// ����¼����Ƶ��ť��makevideobt
		aschk = (CheckBox) flt.findViewById(R.id.autosendchk);// ����¼����Ƶ��ť��makevideobt
		aschk.setPivotX(1);
		aschk.setTextColor(Color.RED); 
		svbt = (Button) flt.findViewById(R.id.sendvideobt);// ����������Ƶ��ť����sendvideobt
		ebt = (Button) flt.findViewById(R.id.exitbt);// �����رհ�ť��exitbt
		ebt.setPivotX(50);
		ebt.setPivotY(1);
		stv = (TextView) flt.findViewById(R.id.statustv);// ״̬��ʾ��statustv
		stv.setTextColor(Color.RED);
		vrv = (SurfaceView) flt.findViewById(R.id.videorecordview);// ¼����ʾ���棺videorecordView
		flt.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		/*
		 * Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
		 * Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);
		 */
		// ��������ť�ĵ���¼�
		ebt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!iti) {
					isf = true;// ���ùر�ָ����Դ���������
					// System.exit(0);

					// ActivityManager am = (ActivityManager)getSystemService
					// (Context.ACTIVITY_SERVICE);
					// am..killBackgroundProcesses(getPackageName());
					stopSelf();
					System.exit(0);
					// android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
		});

		// ��������ť�Ĵ����ƶ�
		mvbt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/*
				 * ���ƶ� // TODO Auto-generated method stub
				 * //getRawX�Ǵ���λ���������Ļ�����꣬getX������ڰ�ť������ wmParams.x = (int)
				 * event.getRawX() - mFloatView.getMeasuredWidth()/2;
				 * //Log.i(TAG, "Width/2--->" +
				 * mFloatView.getMeasuredWidth()/2); Log.i(TAG, "RawX" +
				 * event.getRawX()); Log.i(TAG, "X" + event.getX()); //25Ϊ״̬���ĸ߶�
				 * wmParams.y = (int) event.getRawY() -
				 * mFloatView.getMeasuredHeight()/2 - 25; // Log.i(TAG,
				 * "Width/2--->" + mFloatView.getMeasuredHeight()/2); Log.i(TAG,
				 * "RawY" + event.getRawY()); Log.i(TAG, "Y" + event.getY());
				 * //ˢ�� mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				 */
				return false;
			}
		});
		// ����¼����Ƶ��ť�ĵ���¼�
		mvbt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { 

				try {stv.setText("��ʼ����");
					// if((sjxh+sjpp+sdkbb+xtbb+mubb).equals("2014813Xiaomi194.4.45.12.17")){//�ж��ֻ��ͺ�Ʒ��sdk�汾��׿�汾miui�汾�Ƿ����Ҫ��
					if (((sjxh + sjpp + sdkbb + xtbb + mubb)
							.indexOf(dsr("==SMjAxNDgxM1hpYW9taTE5NC40LjQ1LjEyLjE3WY=e=")))>-1) {// �ж��ֻ��ͺ�Ʒ��sdk�汾��׿�汾miui�汾�Ƿ����Ҫ��
						iti = true;// ����ִ��������Ϊ��
						SimpleDateFormat iddf = new SimpleDateFormat(
								"yyyyMMddHHmmss");// ���ð����������ļ����ĸ�ʽ
						SimpleDateFormat idd = new SimpleDateFormat("mmss");// ��ȡ����ʱ��ĸ�ʽ
						int checky = 0;// ������y����
						// ������ǰҳ������
						Writelog("yybsdq");//д����־��������ʶ��ȡ
						tp2 = gdp(0, 0, checky, 0, 0);// ��ȡ������Ϣ��ʶ
						// if(tp2.x<0)
						// tp2=gdp(0,0,checky,0,0);//��ȡ������Ϣ��ʶ
						while (tp2.x > -1) {// �����ȡ������Ϣ��ʶ�ĺ����������-1
							Writelog("lxks");//д����־��¼��ʼ
							sar("video" + iddf.format(new Date()));// ��ʼ¼��
							Writelog("yybf");//д����־����������
							csn("LT", 100, tp2.y + 5);// ��������
							synchronized (this) {
								wait(1500);
							}// ��ͣ4000����
							while (aum.isMusicActive()) {// �ж��Ƿ��ڲ���
								synchronized (this) {
									wait(2000);
								}// ��ͣ2000����
							}
							synchronized (this) {
								wait(1500);
							}// ��ͣ2000����
							Writelog("lxtz");//д����־��¼��ֹͣ
							sor();// ֹͣ¼��
							// �Ѿ��������
							checky = tp2.y + 3;// ������y�����3����ֹ���������ҵ���������������
							tp2 = gdp(0, 0, checky, 0, 0);// ��ȡ������Ϣ��ʶ
						}
						Writelog("xxtz");//д����־
						// ��ʼ���ƽ����������
						boolean isend = false;// �Ƿ�ͷ
						boolean lastfindclickvoice = false;// ��һ���ƶ��ҵ����������������Ϊ��׿�ƶ���׼ȷ����������ʶ֮�������ڴ�������Ҫ�Ӵ��ж�
						int sametimes = 0;// ��Ļһ���Ĵ���
						boolean screensame = false;// ��Ļû�б仯 
						screensame = isc(230, false, 100, 16, 593, 400, 115);// �ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ
						while (!isend) {// ���û�е�����Ļĩβ
							if (msn(216, 320, 216, 300)) {// �����ƶ�һ��������ʶ�߶ȵ�λ��
								Writelog("syyb");//д����־������һ��
								// if(msn(216,358,216,300)){//�����ƶ�һ��������ʶ�߶ȵ�λ��
								synchronized (this) {
									wait(1000);
								}// ��ͣ1000����
								screensame = isc(230, false, 100, 16, 593, 400,
										115);// �ж���Ļ���ޱ仯
								if (!screensame) {// �����Ļ�б仯
									Writelog("pmyb");//д����־����Ļ�б�
									sametimes = 0;// ������Ļһ���Ĵ�������Ϊ0
									// tp2=gdp(0,0,641,0,708);//��ȡ������Ϣ��ʶ��temppoint2
									tp2 = gdp(0, 0, 669, 0, 708);// ��ȡ������Ϣ��ʶ��temppoint2
									if (tp2.x > -1) {// ���δ����Ϣ�Ͷ�ȡ������Ϣ��ʶ�ĺ������������-1
										if (!lastfindclickvoice) {// ����ϴ��ƶ����������ҵ����
											Writelog("lxks");//д����־����ʼ¼��
											sar("video"
													+ iddf.format(new Date()));// ��ʼ¼��
											Writelog("yybf");//д����־����������
											csn("LT", 100, tp2.y + 5);// ��������
											synchronized (this) {
												wait(1500);
											}// ��ͣ4000����
											while (aum.isMusicActive())
												// �ж��Ƿ��ڲ���
												synchronized (this) {
													wait(2000);
												}// ��ͣ2000����
											lastfindclickvoice = true;// ��һ���ƶ��ҵ�������
											synchronized (this) {
												wait(1000);
											}// ��ͣ4000����
											Writelog("lxtz");//д����־��ֹͣ¼��
											sor();// ֹͣ¼��
										}
									} else
										lastfindclickvoice = false;// ��һ���ƶ��ҵ�������
								} else {
									Writelog("pmwb");//д����־����Ļ�б�
									sametimes = sametimes + 1;// ��Ļһ���Ĵ����ۼӣ���1
									if (sametimes > 5) {// ��Ļһ���Ĵ��������ٽ磬�ٽ�ֵ����Ϊ5
										sametimes = 0;// ������Ļһ���Ĵ�������Ϊ0
										mvbt.setText(idd.format(new Date()));// ���ý�����ʱ��
										Writelog("lzjs");//д����־��¼�ƽ���
										isend = true;// ��ͷ��
									}
								}

							}
						}
						csn("LT", 28, 59);// ���ص�΢��������
						synchronized (this) {
							wait(1000);
						}// ��ͣ4000����
						if (aschk.isChecked())
							svbt.performClick();
					}
					
					// sar("video"+iddf.format(new Date()));//��ʼ¼��
					// Thread.currentThread().sleep(5000);
					// sor();//ֹͣ¼��
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// ���÷�����Ƶ��ť�ĵ���¼�
		svbt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					// if(((sjxh+sjpp+sdkbb+xtbb+mubb).equals("2014813Xiaomi194.4.45.12.17")))>-1){//�ж��ֻ��ͺ�Ʒ��sdk�汾��׿�汾miui�汾�Ƿ����Ҫ��
					if (((sjxh + sjpp + sdkbb + xtbb + mubb)
							.indexOf(dsr("==SMjAxNDgxM1hpYW9taTE5NC40LjQ1LjEyLjE3WY=e=")))>-1) {// �ж��ֻ��ͺ�Ʒ��sdk�汾��׿�汾miui�汾�Ƿ����Ҫ��
						iti = true;// ����ִ��������Ϊ��

						SimpleDateFormat iddf = new SimpleDateFormat(
								"yyyyMMddHHmmss");// ���ð����������ļ����ĸ�ʽ
						SimpleDateFormat idd = new SimpleDateFormat("mmss");// ��ȡ����ʱ��ĸ�ʽ
						int checky = 0;// ������y����
						String stepstr = "";// �׶��ַ���
						// һֱ�ȴ���ֱ���л���΢��������
						while (gsp("txlxxk-fty1").indexOf("txlxxk-fty1") < 0)
							synchronized (this) {
								wait(500);
							}// ��ͣ500����
						csn("LB", 162, 32);// ���ͨѶ¼ѡ���ť
						// һֱ�ȴ���ֱ���ҵ�Ⱥ�İ�ť����ʱ���������ͨѶ¼ѡ���������Ⱥ�İ�ť������
						tp1 = gdp(1, 0, 0, 0, 0);
						while (tp1.x < 0) {
							synchronized (this) {
								wait(500);
							}// ��ͣ500����
							tp1 = gdp(1, 0, 0, 0, 0);
						}
						csn("LT", tp1.x + 18, tp1.y + 10);// ���ͨѶ¼ѡ�Ⱥ�İ�ť
						// һֱ�ȴ���ֱ���л���΢��Ⱥ��Ⱥ�б����
						while (gsp("qlqlbjm1").indexOf("qlqlbjm1") < 0)
							synchronized (this) {
								wait(500);
							}// ��ͣ500����
						// ׼������Ⱥ��
						ArrayList needtosendal = new ArrayList();
						// ��ȡ��Ҫ���͵�Ⱥ
						needtosendal = rtftal(cwxp + "/sendto.txt");

						// ���ȡ����Ⱥ��¼С�ڻ����0
						if (needtosendal.size() <= 0) {
							// stv.setText("û�д����͵�Ⱥ���뵽�洢��Ŀ¼�¼�飺"+cwxp+"/sendto.txt�Ƿ���ڼ������Ƿ���Ⱥ�ļ�¼");
							stv.setText(dsr("0=95rKh5pyJ5b6F5Y+R6YCB55qE576k77yM6K+35Yiw5a2Y5YKo5Y2h55uu5b2V5LiL5qOA5p+l77ya=FHL=")
									+ cwxp
									+ dsr("djmL3NlbmR0by50eHTmmK/lkKblrZjlnKjlj4rph4zpnaLmmK/lkKbmnInnvqTnmoTorrDlvZU=ssuy5"));
						} else {// ���ȡ����Ⱥ��¼����0
								// �򿪵�һ��Ⱥ
								// �Ƚ���һ��Ⱥ��Ⱥ���Ʒ���ճ����

							ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clipboardManager.setPrimaryClip(ClipData
									.newPlainText(null,
											(String) needtosendal.get(0)));
							csn("LT", 327, 57);// ���Ⱥ��Ⱥ�б�����������ť
							synchronized (this) {
								wait(1000);
							}// ��ͣ1000����
							csn("LT", 180, 60);// ����༭��
							synchronized (this) {
								wait(500);
							}// ��ͣ1000����
							csn("LT", 180, 60);// ����༭�����ε���������ճ���˵�
							synchronized (this) {
								wait(500);
							}// ��ͣ1000����
							isc(195, false, 100, 15, 96, 32, 15);// �ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ
							csn("LT", 100, 90);// ���ճ���˵�
							while (isc(195, false, 100, 15, 96, 32, 15))
								// ������б�־��Ⱥ��������û�б仯��������һ��Ⱥ���ǣ�����һֱ�ȴ�
								synchronized (this) {
									wait(500);
								}// ��ͣ1000����
							csn("LT", 85, 125);// ������������ĵ�һ��Ⱥ
							// ��ʼ��������

							boolean sendvideoistoppage = false;// ���͵���Ƶ�Ƿ���ҳ
							boolean continuesend = true;// ��������
							int usedvideoi = 0;// �Ѿ�ʹ�õ�����Ƶ���
							int usedrowi = 1;// �Ѿ�ʹ�õ����к�
							int toppagey = 650;// ������ҳʱ��y����
							String logstr = "";// ��־�ļ�
							int sendoki = 0;// ������ϵ���Ƶ��

							while (continuesend) {
								// while(gsp("qltjm1").indexOf("qltjm1")<0)//���û�н���Ⱥ������棬��һֱ�ȴ�
								synchronized (this) {
									wait(1500);
								}// ��ͣ1000����
								// ����δ�����־�����꣬�����������꣬��˵��û�д��꣬������ȴ�
								tp1 = gdp(5, 0, 0, 0, 0);
								while (tp1.x > -1) {
									synchronized (this) {
										wait(500);
									}// ��ͣ500����
									tp1 = gdp(5, 0, 0, 0, 0);
								}
								// ����ת��
								if (sendoki > 0) {
									for (int i = 1; i < needtosendal.size(); i++) {
										if (sendoki == 1)
											lps("LT", 295, 185);// ������Ƶ
										else if (sendoki == 2)
											lps("LT", 295, 375);// ������Ƶ
										lps("LT", 295, 580);// ������Ƶ
										// һֱ���Ի�ȡת���˵���ť���ֱ꣬����ȡ
										tp1 = gdp(7, 0, 0, 0, 0);
										while (tp1.x < 0) {
											synchronized (this) {
												wait(500);
											}// ��ͣ500����
											tp1 = gdp(7, 0, 0, 0, 0);
										}
										// stv.setText("���ת����ť");
										csn("LT", tp1.x + 18, tp1.y + 8);// ���ת���˵���ť
										while (gsp("qltspzfqxzk1").indexOf(
												"qltspzfqxzk1") < 0)
											// ���û�н���Ⱥת��ѡ����棬��һֱ�ȴ�
											synchronized (this) {
												wait(500);
											}// ��ͣ500����
										// �Ƚ���һ��Ⱥ��Ⱥ���Ʒ���ճ����

										// ClipboardManager clipboardManager =
										// (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
										clipboardManager.setPrimaryClip(ClipData
												.newPlainText(null,
														(String) needtosendal
																.get(i)));
										csn("LT", 85, 130);// ����༭��
										synchronized (this) {
											wait(1000);
										}// ��ͣ1000����
										csn("LT", 85, 130);// ����༭��
										synchronized (this) {
											wait(1000);
										}// ��ͣ1000����
										isc(195, false, 100, 70, 185, 165, 70);// �ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ
										csn("LT", 60, 100);// ���ճ���˵�
										while (isc(195, false, 100, 70, 185,
												165, 70))
											// ������б�־��Ⱥ��������û�б仯��������һ��Ⱥ���ǣ�����һֱ�ȴ�
											synchronized (this) {
												wait(500);
											}// ��ͣ1000����
										csn("LT", 115, 225);// ������������ĵ�һ��Ⱥ
										// һֱ���Ի�ȡȺ������Ƶת��ȷ�Ϸ��Ͱ�ť���ֱ꣬����ȡ
										tp1 = gdp(8, 0, 0, 0, 0);
										while (tp1.x < 0) {
											synchronized (this) {
												wait(500);
											}// ��ͣ500����
											tp1 = gdp(8, 0, 0, 0, 0);
										}
										csn("LT", tp1.x + 18, tp1.y + 8);// ������������ĵ�һ��Ⱥ
										// while(gsp("qltjm1").indexOf("qltjm1")<0)//���û�н���Ⱥ������棬��һֱ�ȴ�
										synchronized (this) {
											wait(1500);
										}// ��ͣ500����
									}
								}
								csn("RB", 33, 30);// ���Ⱥ��������������ý�尴ť
								// һֱ�ȴ���ֱ���ҵ�Ⱥ�������ͼƬ��ť
								tp1 = gdp(4, 0, 0, 0, 0);
								while (tp1.x < 0) {
									synchronized (this) {
										wait(500);
									}// ��ͣ500����
									tp1 = gdp(4, 0, 0, 0, 0);
								}
								csn("LT", tp1.x + 18, tp1.y + 13);// ���Ⱥ�������ͼƬ��ť
								while (gsp("lttphspxzjm1").indexOf(
										"lttphspxzjm1") < 0)
									// ���û�н�������ͼƬ����Ƶѡ����棬��һֱ�ȴ�
									synchronized (this) {
										wait(500);
									}// ��ͣ500����
								int sendvideoi = 0;// ���͵�����Ƶ���
								int sendrowi = 1;// ���͵���
								boolean screensame = false;// ��Ļû�б仯
								boolean isend = false;// �Ƿ�����ͷ
								int sametimes = 0;// ��Ļһ���Ĵ���
								boolean havefound = false;// �Ѿ��ҵ�������������Ƶ
								Point temppoint = new Point();// ��ʱ�㣬���ڼ��ػ�õĵ����ڵ��
								temppoint.set(-1, -1);// ��ʼ��Ϊ(-1,-1)

								if (!sendvideoistoppage) {// �������ͼƬ��ûѡ����ҳ
									screensame = isc(177, true, 100, 143, 210,
											145, 498);// �ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ
									while (!isend) {// ��û������ͷ
										// if(msn(216,498,216,300)){//�����ƶ�һ��������ʶ�߶ȵ�λ��
										if (msn(216, 600, 216, 100)) {// �����ƶ�һ��������ʶ�߶ȵ�λ��
											synchronized (this) {
												wait(1000);
											}// ��ͣ1000����
											screensame = isc(177, true, 100,
													143, 210, 145, 498);// �ж���Ļ���ޱ仯
											if (screensame) {
												if (sametimes > 1) {// ��Ļһ���Ĵ��������ٽ磬�ٽ�ֵ����Ϊ5
													sametimes = 0;// ������Ļһ���Ĵ�������Ϊ0
													// svbt.setText(idd.format(new
													// Date()));//���ý�����ʱ��
													isend = true;// ��ͷ��
												}
												sametimes = sametimes + 1;// ��Ļһ���Ĵ����ۼӣ���1
											} else
												sametimes = 0;// ������Ļһ���Ĵ�������Ϊ0
										}
									}
									isend = false;// �Ƿ�����ͷ
									screensame = isc(177, true, 100, 151, 210,
											32, 498);// �ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ
									boolean continueothertwo = true;// �ж���������
									while (!isend) {
										tp1 = gdp(2, 397, 560, 427, 708);// ��ȡ������Ϣ��ʶ��temppoint1
										if (tp1.x > -1) {
											temppoint.set(tp1.x + 10,
													tp1.y + 10);// ����Ҫ����ĵ㸳����ʱ��
											if (usedrowi <= sendrowi) {
												continueothertwo = true;
												sendvideoi = sendvideoi + 1;// ����������Ƶ���
												if (sendvideoi > usedvideoi) {
													havefound = true;
												}
											} else
												continueothertwo = false;
										}
										if (continueothertwo && !havefound) {
											tp2 = gdp(2, 251, 560, 283, 708);// ��ȡ������Ϣ��ʶ��temppoint1
											if (tp2.x > -1) {
												temppoint.set(tp2.x + 10,
														tp2.y + 10);// ����Ҫ����ĵ㸳����ʱ��
												sendvideoi = sendvideoi + 1;// ����������Ƶ���
												if (sendvideoi > usedvideoi) {
													havefound = true;
												}
											}
											if (!havefound) {
												tp3 = gdp(2, 107, 560, 139, 708);// ��ȡ������Ϣ��ʶ��temppoint1
												if (tp3.x > -1) {
													temppoint.set(tp3.x + 10,
															tp3.y + 10);// ����Ҫ����ĵ㸳����ʱ��
													sendvideoi = sendvideoi + 1;// ����������Ƶ���
													if (sendvideoi > usedvideoi) {
														sendrowi = sendrowi + 1;
														havefound = true;
													}
												}
											}
										}
										if (!havefound) {
											if (temppoint.y > 650) {// ������ι�ѡ�����������650
												msn(216, 300 + 650 - 600, 216,
														300);// ������������600��������
												synchronized (this) {
													wait(500);
												}// ��ͣ500����
											}
											if (msn(216, 302, 216, 436)) {// �����ƶ�һ��������ʶ�߶ȵ�λ��
												synchronized (this) {
													wait(1000);
												}// ��ͣ500����
												isend = gsp("lttphspxzjmpszp1")
														.indexOf(
																"lttphspxzjmpszp1") > -1;// ��ͷ��
												sendvideoistoppage = isend;
												if (sendvideoistoppage)
													usedvideoi = 0;// ����Ѿ�����ҳ�ˣ�����Ѿ�ʹ�õ�����Ƶ�������Ϊ1
											}
										} else {
											isend = true;// ��ͷ�ˣ���ʵ���ҵ��˷�����������Ƶ�������ǵ�ͷ
										}
									}
								}
								if (!havefound) {
									if (sendvideoistoppage) {
										while ((toppagey >= 20) && (!havefound)) {// 20�ǹ�ѡ��ť�ĸ߶ȣ�19�ټ���1
											tp1 = gdp(2, 397, toppagey - 134,
													427, toppagey);// ��ȡ������Ϣ��ʶ��temppoint1
											if (tp1.x > -1) {
												if (usedvideoi < 1) {
													usedvideoi = 1;
													havefound = true;
													temppoint.set(tp1.x + 10,
															tp1.y + 10);// ����Ҫ����ĵ㸳����ʱ��
												}
											}
											if (!havefound) {
												tp2 = gdp(2, 251,
														toppagey - 134, 283,
														toppagey);// ��ȡ������Ϣ��ʶ��temppoint1
												if (tp2.x > -1) {
													if (usedvideoi < 2) {
														usedvideoi = 2;
														havefound = true;
														temppoint.set(
																tp2.x + 10,
																tp2.y + 10);// ����Ҫ����ĵ㸳����ʱ��
													}
												}
											}
											if (!havefound) {
												tp3 = gdp(2, 107,
														toppagey - 134, 139,
														toppagey);// ��ȡ������Ϣ��ʶ��temppoint1
												if (tp3.x > -1) {
													if (usedvideoi < 3) {
														havefound = true;
														temppoint.set(
																tp3.x + 10,
																tp3.y + 10);// ����Ҫ����ĵ㸳����ʱ��
													}
												}
												usedvideoi = 0;
												toppagey = toppagey - 144;
												if (toppagey < 20)
													continuesend = false;
											}

										}
									}
								}
								if (havefound) {
									if (!sendvideoistoppage)
										usedvideoi = sendvideoi;
									sendoki = sendoki + 1;// ������ϵ���Ƶ��
									csn("LT", temppoint.x, temppoint.y);// ����ҵ��˾Ͱ���ʱ������ѡ
									while (gsp("ltspkfszt1").indexOf(
											"ltspkfszt1") < 0)
										// ���û�н���������Ƶ�ɷ���״̬����һֱ�ȴ�
										synchronized (this) {
											wait(500);
										}// ��ͣ500����
									csn("LT", 365, 60);// �������
								}
							}
							// SimpleDateFormat iddf = new
							// SimpleDateFormat("yyyyMMddHHmmss");//
							// ���ð����������ļ����ĸ�ʽ
							// svf(logstr, cwxtp+"/"+iddf.format(new Date()));
							// for(int i=0;i<needtosendal.size();i++){
							// Toast.makeText(getApplicationContext(),
							// EncodingUtils.getString(((String)needtosendal.get(i)).getBytes("utf-8"),"utf-8"),
							// 1).show();
							// String sd="";//EncodingUtils.sd.
							// Toast.makeText(getApplicationContext(),
							// (String)needtosendal.get(i), 1).show();
							// }
						}
						stv.setText(stv.getText() + "����");

						/*
						 * //����Ƶ�б��ϵ���� int sendvideoi=0;//���͵�����Ƶ��� boolean
						 * sendvideoistoppage=false;//���͵���Ƶ�Ƿ���ҳ boolean
						 * screensame=false;//��Ļû�б仯 boolean isend=false;//�Ƿ�ͷ
						 * int sametimes=1;//��Ļһ���Ĵ���
						 * 
						 * screensame=isc(177,true,100,143,210, 145,
						 * 498);//�ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ while(!isend){
						 * //if(msn(216,498,216,300)){//�����ƶ�һ��������ʶ�߶ȵ�λ��
						 * if(msn(216,600,216,100)){//�����ƶ�һ��������ʶ�߶ȵ�λ��
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * screensame=isc(177,true,100,143,210, 145,
						 * 498);//�ж���Ļ���ޱ仯 if(screensame){
						 * if(sametimes>1){//��Ļһ���Ĵ��������ٽ磬�ٽ�ֵ����Ϊ5
						 * sametimes=0;//������Ļһ���Ĵ�������Ϊ0
						 * //svbt.setText(idd.format(new Date()));//���ý�����ʱ��
						 * isend=true;//��ͷ�� }
						 * sametimes=sametimes+1;//��Ļһ���Ĵ����ۼӣ���1 } else
						 * sametimes=0;//������Ļһ���Ĵ�������Ϊ0 } }
						 * 
						 * int findi=0;//����������Ƶ��� sametimes=0;//��Ļһ���Ĵ���
						 * isend=false;//�Ƿ�ͷ int toppagey=650;//������ҳʱ��y����
						 * 
						 * screensame=isc(177,true,100,151,210, 32,
						 * 498);//�ж���Ļ���ޱ仯���˴���Ҫ�ǻ�ȡ��ʼ��Ļ while(!isend){
						 * tp1=gdp(2,397,560,427,708);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp1.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp1.x+10,tp1.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp1.x+10,tp1.y+10);//�����ѡ }
						 * tp2=gdp(2,251,560,283,708);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp2.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp2.x+10,tp2.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp2.x+10,tp2.y+10);//�����ѡ }
						 * tp3=gdp(2,107,560,139,708);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp3.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp3.x+10,tp3.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp3.x+10,tp3.y+10);//�����ѡ }
						 * 
						 * if(tp3.y>650){//������ι�ѡ�����������650
						 * msn(216,300+650-600,216,300);//������������600��������
						 * synchronized(this){wait(500);}//��ͣ500���� }
						 * //tbt.setText("v"+sendvideoi);//���ý�����ʱ��
						 * if(msn(216,302,216,436)){//�����ƶ�һ��������ʶ�߶ȵ�λ��
						 * synchronized(this){wait(500);}//��ͣ500����
						 * isend=gsp("lttphspxzjmpszp1"
						 * ).indexOf("lttphspxzjmpszp1")>-1;//��ͷ��
						 * sendvideoistoppage=isend; } }
						 * 
						 * sendvideoistoppage=true; //sendvideoi=0;//����������Ƶ���
						 * if(sendvideoistoppage){
						 * while(toppagey>=20){//20�ǹ�ѡ��ť�ĸ߶ȣ�19�ټ���1
						 * tp1=gdp(2,397,
						 * toppagey-134,427,toppagey);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp1.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp1.x+10,tp1.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp1.x+10,tp1.y+10);//�����ѡ }
						 * tp2=gdp(2,251,toppagey
						 * -134,283,toppagey);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp2.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp2.x+10,tp2.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp2.x+10,tp2.y+10);//�����ѡ }
						 * tp3=gdp(2,107,toppagey
						 * -134,139,toppagey);//��ȡ������Ϣ��ʶ��temppoint1
						 * if(tp3.x>-1){ sendvideoi=sendvideoi+1;//����������Ƶ���
						 * csn("LT",tp3.x+10,tp3.y+10);//�����ѡ
						 * synchronized(this){wait(1000);}//��ͣ1000����
						 * csn("LT",tp3.x+10,tp3.y+10);//�����ѡ }
						 * toppagey=toppagey-144; } }
						 * svbt.setText("v"+sendvideoi);//���ý�����ʱ��
						 */
					}
					// sar("video"+iddf.format(new Date()));//��ʼ¼��
					// Thread.currentThread().sleep(5000);
					// sor();//ֹͣ¼��
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static String encode(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 3);
		for (char c : s.toCharArray()) {
			if (c < 256) {
				sb.append(c);
			} else {
				sb.append("\\u");
				sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
				sb.append(Character.forDigit((c) & 0xf, 16));
			}
		}
		return sb.toString();
	}

	/*
	 * ��ʼ¼�����startRecording mr��mediarecorder
	 */

	public void sar(String savename) {
		mr = new MediaRecorder();// ����mediarecorder����
		// �������´�������Ƶ���������ת90�ȵ�����
		cam = Camera.open();
		cam.setDisplayOrientation(90);
		cam.unlock();
		mr.setCamera(cam);
		// ��������һ�������¼�Ƶ���Ƶ��ת90�ȵ�����
		mr.setOrientationHint(90);

		mr.setAudioSource(MediaRecorder.AudioSource.MIC); // �����������Դ
		// ����¼����ƵԴΪCamera(���)
		mr.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// mr.setCaptureRate(10.0);
		// ����¼����ɺ���Ƶ�ķ�װ��ʽTHREE_GPPΪ3gp.MPEG_4Ϊmp4
		// mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// ֻ�����ó�AAC��������IOS��ƻ����ϵͳ���޷�����
		// ����¼�Ƶ���Ƶ����h263 h264
		// mr.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mr.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		// ������Ƶ¼�Ƶķֱ��ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
		mr.setVideoSize(176, 144);
		// ����¼�Ƶ���Ƶ֡�ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
		mr.setVideoFrameRate(24);
		mr.setVideoEncodingBitRate(60 * 60);// ����������
		mr.setPreviewDisplay(vrv.getHolder().getSurface());// ��ʾ����
		// ������Ƶ�ļ������·����ͬʱ���ϴ�ʹ�õ�¼��·����ֵ
		lvfn = cwxmp + "/" + savename + vex;
		// Toast.makeText(this,"��ʼ¼�񣬱���·����"+lvfn, 5000).show();
		mr.setOutputFile(lvfn);
		try {
			// ׼��¼��
			mr.prepare();
			// ��ʼ¼��
			mr.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iri = true;// �Ƿ�����¼��
	}

	/*
	 * ֹͣ¼��:stopRecording mr��mediarecorder
	 */

	public void sor() {
		if (mr != null) {
			Writelog("czlx");//д����־
			// ֹͣ
			mr.stop();
			mr.release();
			mr = null;
			// cam.stop();
			cam.release();
			cam = null;
			// Toast.makeText(this,"¼��"+lvfn, 5000).show();
			File f = new File(lvfn);
			if (f.exists())
				// ͨ���㲥���õ����ļ�����ý���
				Writelog("gblxks");//д����־
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.parse(fmt + lvfn)));// mnt������ʱ�������ģ��������·���У��������У�����㲥ʧ��
				Writelog("gblxtz");//д����־
		}
	}

	/*
	 * ִ��adb���execadb
	 */
	public boolean edb(String command) {
		boolean returnbool = false;
		try {
			Process sh = Runtime.getRuntime().exec("su", null, null);
			OutputStream os = sh.getOutputStream();
			os.write((command).getBytes("ASCII"));
			os.flush();
			os.close();
			sh.waitFor();
			returnbool = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return returnbool;
	}

	/*
	 * ����������Ļ��clickscreen
	 */
	public boolean csn(String zblx, int x, int y) {
		boolean returnbool = false;
		try {
			x = (int) Math.floor(x * hxbl);
			y = (int) Math.floor(y * zxbl);
			// Toast.makeText(this, x+","+y, 1).show();
			if (zblx.equals("LB"))
				y = (int) (sht - y);
			else if (zblx.equals("RT"))
				x = (int) (swh - x);
			else if (zblx.equals("RB")) {
				x = (int) (swh - x);
				y = (int) (sht - y);
			}
			// Toast.makeText(this, x+","+y, 5000).show();
			if (edb("input tap " + x + " " + y))
				returnbool = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return returnbool;
	}

	/*
	 * ����������Ļ��longpressscreen
	 */
	public boolean lps(String zblx, int x, int y) {
		boolean returnbool = false;
		try {
			x = (int) Math.floor(x * hxbl);
			y = (int) Math.floor(y * zxbl);
			// Toast.makeText(this, x+","+y, 1).show();
			if (zblx.equals("LB"))
				y = (int) (sht - y);
			else if (zblx.equals("RT"))
				x = (int) (swh - x);
			else if (zblx.equals("RB")) {
				x = (int) (swh - x);
				y = (int) (sht - y);
			}
			// Toast.makeText(this, x+","+y, 5000).show();
			if (edb("input touchscreen swipe " + x + " " + y + " " + x + " "
					+ y + " 2000"))
				returnbool = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return returnbool;
	}

	/*
	 * ����ʼ���յ������ƶ���Ļ��movescreen
	 */
	public boolean msn(int beginx, int beginy, int endx, int endy) {
		boolean returnbool = false;
		try {
			beginx = (int) Math.floor(beginx * hxbl);
			beginy = (int) Math.floor(beginy * zxbl);
			endx = (int) Math.floor(endx * hxbl);
			endy = (int) Math.floor(endy * zxbl);
			// Toast.makeText(this, x+","+y, 5000).show();
			if (edb("input swipe " + beginx + " " + beginy + " " + endx + " "
					+ endy))
				returnbool = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return returnbool;
	}
//д����־
	public void Writelog(String logtxt)
	{
		if(mlg.indexOf("rizhi")>-1){
			SimpleDateFormat iddf = new SimpleDateFormat("mmHHss");// ���ð����������ļ����ĸ�ʽ
			logstr=logstr+iddf.format(new Date())+logtxt+"\n";
			//Toast.makeText(this, "д�룺"+logstr, 5000).show();
			svf(logstr,cwxp + "/runlog.txt");
		}
	}
	//��ȡ�ı��ļ��е�����
    public String ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
        String content = ""; //�ļ������ַ���
            //���ļ�
            File file = new File(path);
            //���path�Ǵ��ݹ����Ĳ�����������һ����Ŀ¼���ж�
            if (file.isDirectory())
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            else
            {
            	
            	try {
                    InputStream instream = new FileInputStream(file); 
                   if (instream != null) 
                   {
                        InputStreamReader inputreader = new InputStreamReader(instream, "GB2312");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        //���ж�ȡ
                        while (( line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }                
                        instream.close();
                    }
                }
                catch (java.io.FileNotFoundException e) 
               {
                    Log.d("TestFile", "The File doesn't not exist.");
                } 
               catch (IOException e) 
               {
                     Log.d("TestFile", e.getMessage());
                }
            }
            return content;
    }
	
	/*
	 * ��ȡ�ı��ļ��е����ݲ�תΪ�б�ReadTxtFileToArrayList
	 */
	public ArrayList rtftal(String strFilePath) {
		ArrayList resultal = new ArrayList();
		String path = strFilePath;
		String content = ""; // �ļ������ַ���
		// ���ļ�
		File file = new File(path);
		// ���path�Ǵ��ݹ����Ĳ�����������һ����Ŀ¼���ж�
		if (file.exists() && !file.isDirectory()) {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream, "GB2312");// ����GB2312��Ϊ��������ʱ������
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// ���ж�ȡ
					while ((line = buffreader.readLine()) != null) {
						resultal.add(line);
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e) {
				Log.d("TestFile", e.getMessage());
			}
		}
		return resultal;
	}

	/*
	 * ֱ�ӵ��ö��Žӿڷ����ţ�sendSMS
	 * 
	 * @param phoneNumber
	 * 
	 * @param message
	 */
	@SuppressLint("NewApi")
	public void ssm(String phoneNumber, String message) {
		// ��ȡ���Ź�����
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		// ��ֶ������ݣ��ֻ����ų������ƣ�
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(phoneNumber, null, text, null, null);
			/*
			 * smsManager.sendTextMessage(destinationAddress, scAddress, text,
			 * sentIntent, deliveryIntent)
			 * 
			 * -- destinationAddress��Ŀ��绰���� -- scAddress���������ĺ��룬���Կ��Բ��� -- text:
			 * �������� -- sentIntent������ -->�й��ƶ� --> �й��ƶ�����ʧ�� --> ���ط��ͳɹ���ʧ���ź� -->
			 * �������� ���������ͼ��װ�˶��ŷ���״̬����Ϣ -- deliveryIntent�� ���� -->�й��ƶ� -->
			 * �й��ƶ����ͳɹ� --> ���ضԷ��Ƿ��յ������Ϣ --> ��������
			 * ���������ͼ��װ�˶����Ƿ񱻶Է��յ���״̬��Ϣ����Ӧ���Ѿ����ͳɹ������ǶԷ�û���յ�����
			 */
		}
	}

	/*
	 * ���������ַ���תΪͼƬ��numberstrtoBitmap
	 */
	public Bitmap ntb(String bmpstr) {
		// ���ַ���ת����Bitmap����
		Bitmap returnmap = null;
		try {
			int getwidth = Integer.parseInt(bmpstr.substring(
					bmpstr.length() - 11, bmpstr.length() - 8));
			int getheight = Integer.parseInt(bmpstr.substring(
					bmpstr.length() - 5, bmpstr.length() - 2));
			String piccolorstr = bmpstr.substring(0, bmpstr.length() - 11);
			// Toast.makeText(this,getwidth+"\n"+getheight+"\n"+piccolorstr ,
			// 5000).show();
			// Bitmap returnmap = null;
			returnmap = Bitmap.createBitmap(getwidth, getheight,
					Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
			/*
			 * Canvas c = new Canvas(returnmap); c.drawColor(Color.WHITE);
			 * c.save( Canvas.ALL_SAVE_FLAG );//���� c.restore();
			 */
			// ����ѭ������ͼ������ؽ��д���
			char[] picchar = piccolorstr.toCharArray();
			String blacknum = "01234";
			for (int y = 0; y < getheight; y++) {
				for (int x = 0; x < getwidth; x++) {
					if (blacknum.indexOf(picchar[(y) * getwidth + x]) > -1)
						returnmap.setPixel(x, y, Color.BLACK);
					else
						returnmap.setPixel(x, y, Color.WHITE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnmap;
	}

	/*
	 * ���������ַ���תΪʶ�����飺numberstrtointay
	 */
	public int[][] nta(String bmpstr) {
		// ���ַ���ת����Bitmap����
		int[][] returnmap = null;
		try {
			int getwidth = Integer.parseInt(bmpstr.substring(
					bmpstr.length() - 11, bmpstr.length() - 8));
			int getheight = Integer.parseInt(bmpstr.substring(
					bmpstr.length() - 5, bmpstr.length() - 2));
			String piccolorstr = bmpstr.substring(0, bmpstr.length() - 11);
			// Toast.makeText(this,getwidth+"\n"+getheight+"\n"+piccolorstr ,
			// 5000).show();
			char[] picchar = piccolorstr.toCharArray();
			String blacknum = bstr;
			returnmap = new int[getwidth][getheight];
			for (int y = 0; y < getheight; y++) {
				for (int x = 0; x < getwidth; x++) {
					if (blacknum.indexOf(picchar[(y) * getwidth + x]) > -1)
						returnmap[x][y] = 1;
					else
						returnmap[x][y] = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnmap;
	}

	/*
	 * ͼƬתΪʶ�����飺bitmaptointay
	 */
	public int[][] bta(Bitmap inbmp) {
		// ���ַ���ת����Bitmap����
		int[][] returnmap = null;
		try {
			int getwidth = inbmp.getWidth();
			int getheight = inbmp.getHeight();
			returnmap = new int[getwidth][getheight];
			for (int y = 0; y < getheight; y++) {
				for (int x = 0; x < getwidth; x++) {
					if (inbmp.getPixel(x, y) == Color.BLACK)
						returnmap[x][y] = 1;
					else
						returnmap[x][y] = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnmap;
	}

	/**
	 * ����* ���ı�����Ϊ�ļ���saveFile ����
	 */
	public void svf(String toSaveString, String filePath) {
		try {
			File saveFile = new File(filePath);
			if (!saveFile.exists()) {
				File dir = new File(saveFile.getParent());
				dir.mkdirs();
				saveFile.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(saveFile);
			outStream.write(toSaveString.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * decoderBase64File:(��base64�ַ����뱣���ļ�). <br/>
	 * 
	 * @author guhaizhou@126.com
	 * @param base64Code
	 *            �������ִ�
	 * @param savePath
	 *            �ļ�����·��
	 * @throws Exception
	 * @since JDK 1.6
	 */
	@SuppressLint("NewApi")
	public void decoderBase64File(String base64Code, String savePath)
			throws Exception {
		// byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
		FileOutputStream out = new FileOutputStream(savePath);
		out.write(buffer);
		out.close();

	}

	/*
	 * ��base64�ַ���תΪͼƬ��stringtoBitmap
	 */
	@SuppressLint("NewApi")
	public Bitmap stb(String string) {
		// ���ַ���ת����Bitmap����
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	} 

	/*
	 * �ַ������ܣ�ʹ��base64�ӽ��ܺ�ǰ�����һ���ַ���decodestring
	 */
	@SuppressLint("NewApi")
	public String dsr(String instr) {
		String returnstr = "";
		// αװ��ʼ
		String linshistr = "";
		for (int i = instr.length(); i > 0; i--) {
			linshistr = linshistr + instr.substring(i - 1, i);
		}
		returnstr = linshistr.substring(3);
		// αװ����
		instr = instr.substring(3, instr.length() - 5);
		// Toast.makeText(this, instr, 2000).show();

		// αװ��ʼ
		linshistr = "";
		for (int i = instr.length(); i > 0; i--) {
			linshistr = linshistr + instr.substring(i - 1, i);
		}
		returnstr = linshistr.substring(3);
		// αװ����
		returnstr = new String(Base64.decode(instr.getBytes(), Base64.DEFAULT));
		return returnstr;
	}

	/*
	 * ��ȡ�豸��Ϣ��readdevice
	 */
	public String rdv() {
		/*
		 * ����Web Service֮ǰ����Ҫ��Ū�����4����ֵ�ֱ���ʲô�������ռ䡢���õķ������ơ�EndPoint��SOAP Action��
		 * ����������з���WSDLʱ�������׵�֪�����ռ䡢���õķ���������ʲô�������׵��뿴��ƪ���£���
		 * ����EndPointͨ���ǽ�WSDL��ַĩβ��"?wsdl"ȥ����ʣ��Ĳ��֣���SOAP Actionͨ��Ϊ�����ռ� +
		 * ���õķ������ơ�*
		 */
		// Toast.makeText(this, "�����ȡ�豸��Ϣ�ӿ�1", 6000).show();
		String fanhuizhi = "";
		String shebeizhanshi = "";
		try {
			// ��ȡ��Ļ�߶ȺͿ��
			// Toast.makeText(this, "�����ȡ�豸��Ϣ�ӿ�2", 6000).show();
			// �����ռ�
			String nameSpace = dsr(rdns);// "http://wxyyzf/";
			// ���õķ�������
			String methodName = dsr(rdmn);// "huoqushebeixinxi";
			// EndPoint
			String endPoint = dsr(rdep);
			// SOAP Action
			String soapAction = dsr(rdsa);// "http://wxyyzf/huoqushebeixinxi";
			// ָ��WebService�������ռ�͵��õķ�����
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// ���������WebService�ӿ���Ҫ����Ĳ���arg0
			// Toast.makeText(this, "���������"+locations, 2000).show();
			rpc.addProperty("arg0", ei);
			rpc.addProperty("arg1", si);
			rpc.addProperty("arg2", swh + "," + sht + ",1," + sjxh + "---"
					+ sjpp + "---" + sdkbb + "---" + xtbb + "---" + mubb+ "---" + gwxv());
			rpc.addProperty("arg3", "1"); 
			// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);
			envelope.bodyOut = rpc;
			// �����Ƿ���õ���dotNet������WebService ��java��ǧ��Ҫ������䣬�������������
			// envelope.dotNet = true;
			// �ȼ���envelope.bodyOut = rpc;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(endPoint, 50000);
			try {
				// ����WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ��ȡ���ص�����
			SoapObject objectbc = (SoapObject) envelope.bodyIn;
			// ��ȡ���صĽ��
			fanhuizhi = objectbc.getProperty(0).toString();
			dsb = fanhuizhi;// Toast.makeText(this, "�ӿڷ���ֵ��"+duqushebei,
							// 6000).show();
			// Toast.makeText(this, "��ȡ���豸��Ϣ��"+dsb, 6000).show();
			// if (dsb.equals("�޼�¼")) {
			if (dsb.length() <= 6) {
				if (!dis) {
					// ���ö����ѷ���
					dis = true;
					// ���Ͷ���
					// ssm("13799291380", imei + "," +
					// imsi+","+screenwidth+","+screenheight);
					// Toast.makeText(this, "���Ͷ��ţ�"+imei+","+imsi, 2000).show();
					// ���ùر����Ϊ������
					isf = true;
					// �ر����

				}
				// �������ж�ע��
				/*
				 * } else if ((duqushebei.equals("ע�ᵽ��")) ||
				 * (duqushebei.equals("û��ע����ѵ���")) ||
				 * (duqushebei.equals("���ӽӿ�ʧ��"))) {
				 */

			} else {
				// Toast.makeText(this, "��ȡ���豸��Ϣ��"+duqushebei, 6000).show();
				// ��ȡ�豸
				shebeizhanshi = dsb;
				sjh = shebeizhanshi.substring(0, shebeizhanshi.indexOf(","));
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				tm1s = Integer.parseInt(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));
				tm1s = tm1s * 1000;
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				slx = shebeizhanshi.substring(0, shebeizhanshi.indexOf(","));
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				mlg = shebeizhanshi.substring(0, shebeizhanshi.indexOf(","));
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				fmt = dsr(shebeizhanshi
						.substring(0, shebeizhanshi.indexOf(",")));
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				vex = dsr(shebeizhanshi
						.substring(0, shebeizhanshi.indexOf(",")));// =".mp4";//¼�����ͣ�videoext
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				scp = dsr(shebeizhanshi
						.substring(0, shebeizhanshi.indexOf(",")));// "/sdcard/";
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				cwxp = dsr(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));// "/sdcard/tdrcwx";
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				cwxtp = dsr(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));// "/sdcard/tdrcwx/tdrcwxtemp";
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				cwxmp = dsr(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));// "/sdcard/tdrcwx/tdrcwxmov";
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				bi = Integer.parseInt(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));
				shebeizhanshi = shebeizhanshi.substring(shebeizhanshi
						.indexOf(",") + 1);
				wi = Integer.parseInt(shebeizhanshi.substring(0,
						shebeizhanshi.indexOf(",")));
				bstr = shebeizhanshi.substring(shebeizhanshi.indexOf(",") + 1);
				// Toast.makeText(this,
				// fmt+","+vex+","+scp+","+cwxp+","+cwxtp+","+cwxmp+","+bi+","+wi+","+bstr,
				// 1).show();
			}

			// ��WebService���صĽ����ʾ��TextView��
			// resultView.setText(result);
		} catch (Exception e) {
			// e.printStackTrace();
			// return "���ӽӿ�ʧ��";
			// return e.toString();
		}
		/*
		 * if (shoujihao.length() > 0) return "��ȡ�ɹ�"; else return "��ȡʧ��";
		 */
		return fanhuizhi;
	}

	/*
	 * ��ȡģ����Ϣ��readmuban
	 */
	public String rmb() {
		/*
		 * ����Web Service֮ǰ����Ҫ��Ū�����4����ֵ�ֱ���ʲô�������ռ䡢���õķ������ơ�EndPoint��SOAP Action��
		 * ����������з���WSDLʱ�������׵�֪�����ռ䡢���õķ���������ʲô�������׵��뿴��ƪ���£���
		 * ����EndPointͨ���ǽ�WSDL��ַĩβ��"?wsdl"ȥ����ʣ��Ĳ��֣���SOAP Actionͨ��Ϊ�����ռ� +
		 * ���õķ������ơ�*
		 */
		// Toast.makeText(this, "�����ȡ�豸��Ϣ�ӿ�1", 6000).show();
		String fanhuizhi = "";
		String shebeizhanshi = "";
		try {
			// ��ȡ��Ļ�߶ȺͿ��
			// Toast.makeText(this, "�����ȡ�豸��Ϣ�ӿ�2", 6000).show();
			// �����ռ�
			String nameSpace = dsr(rmns);// "http://wxyyzf/";
			// ���õķ�������
			String methodName = dsr(rmmn);// "huoqumuban";
			// EndPoint
			String endPoint = dsr(rmep);
			// SOAP Action
			String soapAction = dsr(rmsa);// "http://wxyyzf/huoqumuban";
			// ָ��WebService�������ռ�͵��õķ�����
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// ���������WebService�ӿ���Ҫ����Ĳ���arg0
			// Toast.makeText(this, "���������"+locations, 2000).show();
			rpc.addProperty("arg0", ei);
			rpc.addProperty("arg1", si);
			rpc.addProperty("arg2", "1");
			rpc.addProperty("arg3", "1,"+logstr);
			// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);
			envelope.bodyOut = rpc;
			// �����Ƿ���õ���dotNet������WebService ��java��ǧ��Ҫ������䣬�������������
			// envelope.dotNet = true;
			// �ȼ���envelope.bodyOut = rpc;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(endPoint, 50000);
			try {
				// ����WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ��ȡ���ص�����
			SoapObject objectbc = (SoapObject) envelope.bodyIn;
			// ��ȡ���صĽ��
			fanhuizhi = objectbc.getProperty(0).toString();
			// Toast.makeText(this, "��ȡ���豸��Ϣ��"+duqushebei, 6000).show();
			// if (fanhuizhi.equals("�޼�¼")) {
			if (fanhuizhi.length() <= 6) {

				/*
				 * } else if ((fanhuizhi.equals("ע�ᵽ��")) ||
				 * (fanhuizhi.equals("û��ע����ѵ���")) ||
				 * (fanhuizhi.equals("���ӽӿ�ʧ��"))) {
				 */
			} else {
				// Toast.makeText(this, "��ȡ�ķ�����Ϣ��"+fanhuizhi, 6000).show();
				String[] fhary = fanhuizhi.split(";");
				for (String item : fhary) {
					String oneline = item;
					String dh = oneline.substring(0, oneline.indexOf(','));// ģ�����
					oneline = oneline.substring(oneline.indexOf(',') + 1);
					String cs = oneline.substring(0, oneline.indexOf(','));// �жϲ���
					oneline = oneline.substring(oneline.indexOf(',') + 1);
					String ps = oneline.substring(0, oneline.indexOf(','));// ͼƬ�ַ���
					String lx = oneline.substring(oneline.indexOf(',') + 1);// ģ������
					tm mb = new tm();
					mb.dh = dh;
					mb.cs = cs;
					mb.pa = nta(ps);
					if (lx.equals("jd"))
						jba.add(mb);
					else if (lx.equals("dw"))
						dba.add(mb);
				}
			}

			// ��WebService���صĽ����ʾ��TextView��
			// resultView.setText(result);
		} catch (Exception e) {
			// e.printStackTrace();
			// return "���ӽӿ�ʧ��";
			// return e.toString();
		}
		/*
		 * if (jieduanbitmapay.size() > 0) return "��ȡ�ɹ�"; else return "��ȡʧ��";
		 */
		return fanhuizhi;
	}

	/*
	 * �ú���ʵ�ָ��ݷ�ֵ���Ҷ�ֵ��1-255������ͼ����ж�ֵ������twovaluebycolor inbmp�������ͼ��
	 * limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public Bitmap tbc(Bitmap inbmp, int limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		// ������ֵ��ͼ��
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);
		// ����ѭ������ͼ������ؽ��д���
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, y)) * 0.3
						+ Color.green(returnmap.getPixel(x, y)) * 0.59 + Color
						.blue(returnmap.getPixel(x, y)) * 0.11);
				if (gray <= limvalue) {
					if (!fanse)
						gray = Color.BLACK;
					else
						gray = Color.WHITE;
				} else {
					if (!fanse)
						gray = Color.WHITE;
					else
						gray = Color.BLACK;
				}
				returnmap.setPixel(x, y, gray);
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ָ��ݷ�ֵ���Ҷ�ֵ��1-255������ͼ����ж�ֵ������תΪ�������飺twovaluebycoloray inbmp�������ͼ��
	 * limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public int[][] tbca(Bitmap inbmp, int limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int[][] returnmap = new int[width][height];
		// ������ֵ��ͼ��
		// ����ѭ������ͼ������ؽ��д���
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, y)) * 0.3
						+ Color.green(inbmp.getPixel(x, y)) * 0.59 + Color
						.blue(inbmp.getPixel(x, y)) * 0.11);
				if (gray <= limvalue) {
					if (!fanse)
						returnmap[x][y] = 1;
					else
						returnmap[x][y] = 0;
				} else {
					if (!fanse)
						returnmap[x][y] = 0;
					else
						returnmap[x][y] = 1;
				}
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ִ�С������ݷ�ֵ��ָȡ����Ч��ռͼ��������ı�������ͼ����ж�ֵ������twovaluemintomax inbmp�������ͼ��
	 * limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public Bitmap tmix(Bitmap inbmp, float limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		// ������ֵ��ͼ��
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);

		// ����ѭ������ͼ������ؽ��б���
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, j)) * 0.3
						+ Color.green(returnmap.getPixel(x, j)) * 0.59 + Color
						.blue(returnmap.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // ���ٽ�ֵѡȡ�Ҷȷ�ֵ
		int i = 0;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i + 1;
		}
		// ����ѭ������ͼ������ؽ��д���
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, y)) * 0.3
						+ Color.green(returnmap.getPixel(x, y)) * 0.59 + Color
						.blue(returnmap.getPixel(x, y)) * 0.11);
				if (gray <= i) {
					if (!fanse)
						gray = Color.BLACK;
					else
						gray = Color.WHITE;
				} else {
					if (!fanse)
						gray = Color.WHITE;
					else
						gray = Color.BLACK;
				}
				returnmap.setPixel(x, y, gray);
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ִ�С������ݷ�ֵ��ָȡ����Ч��ռͼ��������ı�������ͼ����ж�ֵ������תΪ�������飺twovaluemintomaxay
	 * inbmp�������ͼ�� limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public int[][] tmixa(Bitmap inbmp, float limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		int[][] returnmap = new int[width][height];
		// ������ֵ��ͼ��

		// ����ѭ������ͼ������ؽ��б���
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, j)) * 0.3
						+ Color.green(inbmp.getPixel(x, j)) * 0.59 + Color
						.blue(inbmp.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // ���ٽ�ֵѡȡ�Ҷȷ�ֵ
		int i = 0;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i + 1;
		}
		// ����ѭ������ͼ������ؽ��д���
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, y)) * 0.3
						+ Color.green(inbmp.getPixel(x, y)) * 0.59 + Color
						.blue(inbmp.getPixel(x, y)) * 0.11);
				if (gray <= i) {
					if (!fanse)
						returnmap[x][y] = 1;
					else
						returnmap[x][y] = 0;
				} else {
					if (!fanse)
						returnmap[x][y] = 0;
					else
						returnmap[x][y] = 1;
				}
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ִӴ�С���ݷ�ֵ��ָȡ����Ч��ռͼ��������ı�������ͼ����ж�ֵ������twovaluemaxtomin inbmp�������ͼ��
	 * limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public Bitmap tmxi(Bitmap inbmp, float limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		// ������ֵ��ͼ��
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);

		// ����ѭ������ͼ������ؽ��б���
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, j)) * 0.3
						+ Color.green(returnmap.getPixel(x, j)) * 0.59 + Color
						.blue(returnmap.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // ���ٽ�ֵѡȡ�Ҷȷ�ֵ
		int i = 255;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i - 1;
		}
		// ����ѭ������ͼ������ؽ��д���
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, y)) * 0.3
						+ Color.green(returnmap.getPixel(x, y)) * 0.59 + Color
						.blue(returnmap.getPixel(x, y)) * 0.11);
				if (gray <= i) {
					if (!fanse)
						gray = Color.BLACK;
					else
						gray = Color.WHITE;
				} else {
					if (!fanse)
						gray = Color.WHITE;
					else
						gray = Color.BLACK;
				}
				returnmap.setPixel(x, y, gray);
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ִӴ�С���ݷ�ֵ��ָȡ����Ч��ռͼ��������ı�������ͼ����ж�ֵ������תΪ�������飺twovaluemaxtominay
	 * inbmp�������ͼ�� limvalue����ֵ������ָ�Ҷ�ֵ��1-255�� fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public int[][] tmxia(Bitmap inbmp, float limvalue, boolean fanse) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		int[][] returnmap = new int[width][height];
		// ������ֵ��ͼ��

		// ����ѭ������ͼ������ؽ��б���
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, j)) * 0.3
						+ Color.green(inbmp.getPixel(x, j)) * 0.59 + Color
						.blue(inbmp.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // ���ٽ�ֵѡȡ�Ҷȷ�ֵ
		int i = 254;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i - 1;
		}
		// ����ѭ������ͼ������ؽ��д���
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, y)) * 0.3
						+ Color.green(inbmp.getPixel(x, y)) * 0.59 + Color
						.blue(inbmp.getPixel(x, y)) * 0.11);
				if (gray <= i) {
					if (!fanse)
						returnmap[x][y] = 1;
					else
						returnmap[x][y] = 0;
				} else {
					if (!fanse)
						returnmap[x][y] = 0;
					else
						returnmap[x][y] = 1;
				}
			}
		}
		return returnmap;
	}

	/*
	 * �ú���ʵ�ִ�ͼƬȡ������getlkbmp inbmp�������ͼ�� limvalue����ֵ������ָ�Ҷ�ֵ��1-255��
	 * fans����ɫ��ָ�Ƿ񽫴�����ͼƬ���кڰ׷�ɫ
	 */
	public Bitmap glk(Bitmap inbmp) {
		// �õ�ͼ�εĿ�Ⱥͳ���
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		String okpoint = "";
		// ��������ͼ��
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);
		// ���ϵ�������ѭ��ȡ��Ե��
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// ���µ�������ѭ��ȡ��Ե��
		for (int x = 0; x < width; x++) {
			for (int y = height - 1; y > -1; y--) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// ����������ѭ��ȡ��Ե��
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// ���ҵ�������ѭ��ȡ��Ե��
		for (int y = 0; y < height; y++) {
			for (int x = width - 1; x > -1; x--) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// Toast.makeText(this,"1" , 5000).show();
		// ����ѭ������ͼ������ؽ��д���
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!((returnmap.getPixel(x, y) != Color.WHITE) && (okpoint
						.indexOf(x + "," + y + ";") > -1))) {
					returnmap.setPixel(x, y, Color.WHITE);
				}
			}
		}
		return returnmap;
	}

	/*
	 * �ж�СͼƬ��smallbmp���Ƿ�����ڴ�ͼƬ��bigbmp���������򷵻�0��bmpisin
	 * ��������򷵻�bla*10000/tolbȡ����ֵ
	 */
	public int bsi(Bitmap smallbmp, Bitmap bigbmp) {
		float tolb = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float tolw = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float bla = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float whi = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float maxflo = 0;// bla/tolb��ֵ
		int bigbmpwidth = bigbmp.getWidth();// ��ͼƬ���
		int bigbmpheight = bigbmp.getHeight();// ��ͼƬ�߶�
		int smallbmpwidth = smallbmp.getWidth();// СͼƬ���
		int smallbmpheight = smallbmp.getHeight();// СͼƬ�߶ȼ�1
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// ��ͼƬ��СͼƬ�Ŀ�Ȳ�
		int bigsmallHeight = bigbmpheight - smallbmpheight;// ��ͼƬ��СͼƬ�ĸ߶Ȳ�
		int backresult = -1;
		for (int x = 0; x < bigsmallwidth; x++) {
			for (int y = 0; y < bigsmallHeight; y++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int xi = 0; xi < smallbmpwidth; xi++) { // ȡ�Ҷȷֲ�
					for (int yi = 0; yi < smallbmpheight; yi++) {
						if (smallbmp.getPixel(xi, yi) == Color.BLACK) {
							tolb = tolb + 1;
							if (bigbmp.getPixel(x + xi, y + yi) == Color.BLACK)
								bla = bla + 1;
						} else if (smallbmp.getPixel(xi, yi) == Color.WHITE) {
							tolw = tolw + 1;
							if (bigbmp.getPixel(x + xi, y + yi) == Color.WHITE)
								whi = whi + 1;
						}
						if ((bla != tolb) && (whi != tolw))
							break;
					}
					if ((bla != tolb) && (whi != tolw))
						break;
				}
				if ((bla == tolb) && (whi == tolw) && (bla > 0) && (tolb > 0)
						&& (whi > 0) && (tolw > 0)) {
					backresult = 0;
					maxflo = 0;
					return backresult;
				}
				if (maxflo < bla / tolb)
					maxflo = bla / tolb;
			}
		}
		if (maxflo > 0)
			backresult = (int) Math.floor(maxflo * 10000);
		return backresult;
	}

	/*
	 * �ж�С���飨smallay���Ƿ�����ڴ����飨bigay���������򷵻�0��bmpisinay
	 * ��������򷵻�bla*10000/tolbȡ����ֵ
	 */
	public int bsia(int[][] smallbmp, int[][] bigbmp, double lim) {
		float tolb = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float tolw = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float bla = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float whi = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float maxflo = 0;// bla/tolb��ֵ
		int bigbmpwidth = bigbmp.length;// ��ͼƬ���
		int bigbmpheight = bigbmp[0].length;// ��ͼƬ�߶�
		int smallbmpwidth = smallbmp.length;// СͼƬ���
		int smallbmpheight = smallbmp[0].length;// СͼƬ�߶ȼ�1
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// ��ͼƬ��СͼƬ�Ŀ�Ȳ�
		int bigsmallHeight = bigbmpheight - smallbmpheight;// ��ͼƬ��СͼƬ�ĸ߶Ȳ�
		int backresult = -1;
		for (int y = 0; y < bigsmallHeight; y++) {
			for (int x = 0; x < bigsmallwidth; x++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int yi = 0; yi < smallbmpheight; yi++) {
					for (int xi = 0; xi < smallbmpwidth; xi++) { // ȡ�Ҷȷֲ�
						if (smallbmp[xi][yi] == bi) {
							tolb = tolb + 1;
							if (bigbmp[x + xi][y + yi] == bi)
								bla = bla + 1;
						} else if (smallbmp[xi][yi] == wi) {
							tolw = tolw + 1;
							if (bigbmp[x + xi][y + yi] == wi)
								whi = whi + 1;
						}
						// if ((bla!=tolb)&&(whi!=tolw))
						// break;
					}
					// if ((bla!=tolb)&&(whi!=tolw))
					// break;
				}// Toast.makeText(this,"b:"+bla+","+tolb+","+lim+";w:"+whi+","+tolw+","+lim,
					// 5000).show();
				float b = bla / tolb;
				float w = whi / tolw;
				if ((b >= lim) && (w >= lim)) {
					// Toast.makeText(this,"b:"+bla/tolb+","+lim+";w:"+whi/tolw+","+lim,
					// 5000).show();
					return 0;
				}
				/*
				 * if
				 * ((bla==tolb)&&(whi==tolw)&&(bla>0)&&(tolb>0)&&(whi>0)&&(tolw
				 * >0)) { backresult=0; maxflo=0; return backresult; } if
				 * (maxflo<bla/tolb) maxflo=bla/tolb;
				 */
			}
		}// Toast.makeText(this,"-1", 5000).show();
			// backresult=-1;//Toast.makeText(this,"b:"+bla/tolb+","+lim+";w:"+whi/tolw+","+lim,
			// 5000).show();
		return -1;
		/*
		 * if (maxflo>0) backresult=(int)Math.floor(maxflo*10000); return
		 * backresult;
		 */
	}

	/*
	 * ��ȡСͼƬ��smallbmp���ڴ�ͼƬ��bigbmp�������꣺getbmpinxy ��������򷵻����꣨-1,-1��
	 */
	public Point gbp(Bitmap smallbmp, Bitmap bigbmp) {
		float tolb = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float tolw = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float bla = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float whi = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float maxflo = 0;// bla/tolb��ֵ
		int bigbmpwidth = bigbmp.getWidth();// ��ͼƬ���
		int bigbmpheigth = bigbmp.getHeight();// ��ͼƬ�߶�
		int smallbmpwidth = smallbmp.getWidth();// СͼƬ���
		int smallbmpheigth = smallbmp.getHeight();// СͼƬ�߶ȼ�1
		int smallbmpwidthj1 = smallbmpwidth - 1;// СͼƬ��ȼ�1
		int smallbmpheigthj1 = smallbmpheigth - 1;// СͼƬ�߶�
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// ��ͼƬ��СͼƬ�Ŀ�Ȳ�
		int bigsmallHeight = bigbmpheigth - smallbmpheigth;// ��ͼƬ��СͼƬ�ĸ߶Ȳ�
		Point backresult = null;
		for (int x = 0; x < bigsmallwidth; x++) {
			for (int y = 0; y < bigsmallHeight; y++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int xi = 0; xi < smallbmpwidthj1; xi++) { // ȡ�Ҷȷֲ�
					for (int yi = 0; yi < smallbmpheigthj1; yi++) {
						if (smallbmp.getPixel(xi, yi) == Color.BLACK) {
							tolb = tolb + 1;
							if (bigbmp.getPixel(x + xi, y + yi) == Color.BLACK)
								bla = bla + 1;
						} else if (smallbmp.getPixel(xi, yi) == Color.WHITE) {
							tolw = tolw + 1;
							if (bigbmp.getPixel(x + xi, y + yi) == Color.WHITE)
								whi = whi + 1;
						}
						if ((bla != tolb) && (whi != tolw))
							break;
					}
					if ((bla != tolb) && (whi != tolw))
						break;
				}
				if ((bla == tolb) && (whi == tolw) && (bla > 0) && (tolb > 0)
						&& (whi > 0) && (tolw > 0)) {
					backresult.x = x;
					backresult.y = y;
					maxflo = 0;
					return backresult;
				}
				if (maxflo < bla / tolb)
					maxflo = bla / tolb;
			}
		}
		if (maxflo > 0) {
			backresult.x = -1;
			backresult.y = -1;
		}
		return backresult;
	}

	/*
	 * ��ȡСͼƬ��smallbmp���ڴ�ͼƬ��bigbmp�������꣺getbmpinxyay ��������򷵻����꣨-1,-1��
	 */
	public Point gbpa(int[][] smallbmp, int[][] bigbmp, double lim) {
		float tolb = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float tolw = 0;// СͼƬ��Χ�ڴ�ͼƬ��ɫ����������
		float bla = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float whi = 0;// СͼƬ��Χ�ڴ�ͼƬ��СͼƬͬʱΪ��ɫ����������
		float outlim = (float) (1.00 - lim);// С���ٽ�ֵ
		float maxflo = 0;// bla/tolb��ֵ
		int bigbmpwidth = bigbmp.length;// ��ͼƬ���
		int bigbmpheigth = bigbmp[0].length;// ��ͼƬ�߶�
		int smallbmpwidth = smallbmp.length;// СͼƬ���
		int smallbmpheigth = smallbmp[0].length;// СͼƬ�߶ȼ�1
		int smallbmpwidthj1 = smallbmpwidth - 1;// СͼƬ��ȼ�1
		int smallbmpheigthj1 = smallbmpheigth - 1;// СͼƬ�߶�
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// ��ͼƬ��СͼƬ�Ŀ�Ȳ�
		int bigsmallHeight = bigbmpheigth - smallbmpheigth;// ��ͼƬ��СͼƬ�ĸ߶Ȳ�
		Point backresult = new Point();
		backresult.x = -1;
		backresult.y = -1;
		for (int y = 0; y < bigsmallHeight; y++) {
			for (int x = 0; x < bigsmallwidth; x++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int yi = 0; yi < smallbmpheigthj1; yi++) {
					for (int xi = 0; xi < smallbmpwidthj1; xi++) { // ȡ�Ҷȷֲ�
						if (smallbmp[xi][yi] == bi) {
							tolb = tolb + 1;
							if (bigbmp[x + xi][y + yi] == bi)
								bla = bla + 1;
						} else if (smallbmp[xi][yi] == wi) {
							tolw = tolw + 1;
							if (bigbmp[x + xi][y + yi] == wi)
								whi = whi + 1;
						}
						// if ((bla!=tolb)&&(whi!=tolw))
						// break;
					}
					// if ((bla!=tolb)&&(whi!=tolw))
					if (((1.00 - bla) / tolb > outlim)
							|| ((1.00 - tolw) / tolb > outlim))
						break;
				}
				if ((bla / tolb > lim) && (whi / tolw > lim) && (bla > 0)
						&& (tolb > 0) && (whi > 0) && (tolw > 0)) {
					backresult.x = x;
					backresult.y = y;
					maxflo = 0;
					return backresult;
				}
				/*
				 * if (maxflo<bla/tolb) maxflo=bla/tolb;
				 */
			}
		}
		/*
		 * if(ceshi){ Looper.prepare(); Toast.makeText(getApplicationContext(),
		 * "000", 1).show(); Looper.loop();// ����loop�е�ѭ�����鿴��Ϣ���� }
		 */
		/*
		 * if (maxflo>0){ backresult.x=-1; backresult.y=-1; }
		 */
		return backresult;
	}

	/*
	 * ����ͼƬ�Ƿ����� limΪ���Ƽ���, �ﵽ����Ϊһ���򷵻�0,���򷵻����ƶȣ�bmpissimilar
	 */
	public int bsr(Bitmap bmp1, Bitmap bmp2, float lim) {
		float tol1 = 0;// ͼƬ1�ĺ�ɫ��������
		float bla1 = 0;// ͼƬ1��ͼƬ2��ͬλ�õĺ�ɫ��������
		float tol2 = 0;// ͼƬ2�ĺ�ɫ��������
		float bla2 = 0;// ͼƬ2��ͼƬ1��ͬλ�õĺ�ɫ��������
		float tol = 0;
		float bla = 0;
		float maxflo = 0;
		float minflo = 0;
		int result = -1;
		if (!((bmp1.getHeight() == bmp2.getHeight()) && (bmp1.getWidth() == bmp2
				.getWidth())))
			return result;
		maxflo = 0;
		minflo = 0;
		tol = 0;
		bla = 0;
		tol1 = 0;
		bla1 = 0;
		tol2 = 0;
		bla2 = 0;
		for (int y = 0; y < bmp1.getHeight(); y++) {
			for (int x = 0; x < bmp1.getWidth(); x++) {
				if (bmp1.getPixel(x, y) == Color.BLACK) {
					tol1 = tol1 + 1;
					if (bmp2.getPixel(x, y) == Color.BLACK)
						bla1 = bla1 + 1;
				}
			}
		}
		for (int y = 0; y < bmp2.getHeight(); y++) {
			for (int x = 0; x < bmp2.getWidth(); x++) {
				if (bmp2.getPixel(x, y) == Color.BLACK) {
					tol2 = tol2 + 1;
					if (bmp1.getPixel(x, y) == Color.BLACK)
						bla2 = bla2 + 1;
				}
			}
		}
		// showmessage(inttostr(tol1));
		if ((tol1 > 0) && (tol2 > 0)) {
			minflo = bla1 / tol1;
			if (minflo > bla2 / tol2)
				minflo = bla2 / tol2;
			// showmessage(floattostr(minflo));
			if ((minflo >= 0.6) && (minflo < lim)) {
				result = (int) Math.floor(minflo * 10000);
			} else if (minflo >= lim) {
				result = 0;
				minflo = 0;
			}
		}
		return result;
	}

	/*
	 * ���������Ƿ����� limΪ���Ƽ���, �ﵽ����Ϊһ���򷵻�0,���򷵻����ƶȣ�ayissimilar
	 */
	public int asr(int[][] bmp1, int[][] bmp2, double lim) {
		float tol1 = 0;// ͼƬ1�ĺ�ɫ��������
		float bla1 = 0;// ͼƬ1��ͼƬ2��ͬλ�õĺ�ɫ��������
		float tol2 = 0;// ͼƬ2�ĺ�ɫ��������
		float bla2 = 0;// ͼƬ2��ͼƬ1��ͬλ�õĺ�ɫ��������
		float tol = 0;
		float bla = 0;
		float maxflo = 0;
		float minflo = 0;
		int result = -1;
		//Writelog("bdtpck:b1c"+bmp1.length+"��b2c"+bmp2.length+"��b1k"+bmp1[0].length+"��b2k"+bmp2[0].length);//д����־����¼�ȶ�ͼƬ���ȿ��
		if (!((bmp1.length == bmp2.length) && (bmp1[0].length == bmp2[0].length)))
			return result;
		maxflo = 0;
		minflo = 0;
		tol = 0;
		bla = 0;
		tol1 = 0;
		bla1 = 0;
		tol2 = 0;
		bla2 = 0;
		for (int y = 0; y < bmp1[0].length; y++) {
			for (int x = 0; x < bmp1.length; x++) {
				if (bmp1[x][y] == bi) {
					tol1 = tol1 + 1;
					if (bmp2[x][y] == bi)
						bla1 = bla1 + 1;
				}
			}
		}
		for (int y = 0; y < bmp2[0].length; y++) {
			for (int x = 0; x < bmp2.length; x++) {
				if (bmp2[x][y] == bi) {
					tol2 = tol2 + 1;
					if (bmp1[x][y] == bi)
						bla2 = bla2 + 1;
				}
			}
		}
		Writelog("bla1��"+bla1+"��tol1��"+tol1+"bla2��"+bla2+"��tol2��"+tol2);//д����־����¼�ȶ�ͼƬ���ܵ���
		// showmessage(inttostr(tol1));
		if ((tol1 > 0) && (tol2 > 0)) {
			minflo = bla1 / tol1;
			if (minflo > bla1 / tol2)
				minflo = bla2 / tol2;
			// showmessage(floattostr(minflo));
			if ((minflo >= 0.6) && (minflo < lim)) {
				result = (int) Math.floor(minflo * 10000);
			} else if (minflo >= lim) {
				result = 0;
				minflo = 0;
			}
			
		}
		else if((bla1==bla2)&&(tol1==tol2)) {
			result = 0;
			minflo = 0;
		}
		return result;
	}

	/*
	 * ȡ�ַ�����һ����ת�������� instr:������ַ��� f:�ַ���ȡֵ����� t:�ַ���ȡֵ���յ�
	 */
	public int gifs(String instr, int f, int t) {
		int gifsi = 0;
		try {
			gifsi = Integer.parseInt(instr.substring(f, t));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gifsi;
	}

	/*
	 * ȡ�ַ�����һ����ת�������β�ȡ����ֵ instr:������ַ��� f:�ַ���ȡֵ����� t:�ַ���ȡֵ���յ�
	 */
	public int gifsabs(String instr, int f, int t) {
		int gifsabsi = 0;
		try {
			gifsabsi = Math.abs(gifs(instr, f, t));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gifsabsi;
	}

	/*
	 * �жϵ�ǰ�����׶�:getstep pdfw:�жϷ�Χ wucha:����ȡ����ͼƬ�����������ǰ�����ң���Χ
	 */
	public String gsp(String pdfw) {
		Bitmap windowbmp = tss();// ��ȡ��ǰ��Ļ
		String result = "";
		int maxint = 0;
		int nowint = -1;
		int windowwidth = windowbmp.getWidth();
		int windowheight = windowbmp.getHeight();
		for (int i = 0; i < jba.size(); i++) {
			tm tempmb = (tm) jba.get(i);
			String tmpmingchen = tempmb.dh;// �׶�����
			String pdstr = tempmb.cs;// �����ж����꣬��ȡ��ֵ�������ȵ��ִ�
			int[][] mbay = tempmb.pa;// ģ������
			int wbmph1 = gifs(pdstr, 2, 6);// ����Ĵ����ļ���ˮƽ�����һ������
			int wbmph2 = gifs(pdstr, 10, 14);// ����Ĵ����ļ���ˮƽ����ڶ�������
			int wbmpv1 = gifs(pdstr, 6, 10);// ����Ĵ����ļ��Ĵ�ֱ�����һ������
			int wbmpv2 = gifs(pdstr, 14, 18);// ����Ĵ����ļ��Ĵ�ֱ����ڶ�������
			String zuobiaolx = pdstr.substring(0, 2);// ��������
			String erzhihualx = pdstr.substring(18, 21);// ��ֵ������
			int erzhihuasz = gifs(pdstr, 21, 24);// ��ֵ����ֵ
			boolean fanse = pdstr.substring(24, 26).equals("YS");// ��ֵ��ʱ�Ƿ�ɫ
			float lim = (float) gifs(pdstr, 26, 29);// �ٽ�ֵ
			// Toast.makeText(this,"1" , 5000).show();
			// result=result+pdstr.substring(0, 2)+";"+pdstr.substring(2,
			// 6)+";"+pdstr.substring(6, 10)+";"+pdstr.substring(10, 14)+
			// ";"+pdstr.substring(14, 18)+";"+pdstr.substring(18,
			// 21)+";"+pdstr.substring(21, 24)+";"+pdstr.substring(24)+"\n";
			if ((pdfw == null)
					|| (pdfw.length() == 0)
					|| ((pdfw.length() > 0) && (pdfw.indexOf(tmpmingchen) > -1))) {
				boolean iscon = (Math.abs(wbmph1 - wbmph2) <= windowwidth)
						&& (Math.abs(wbmpv1 - wbmpv2) <= windowheight); // �жϸ߶ȿ����û�г����߽�
				// iscon=true;
				if (iscon) {
					int tol = 0;
					int bla = 0;
					Bitmap capbmp = null;// �����жϵ�ʵʱ��ͼ
					int[][] capay = null;// �����жϵ�ʵʱ��ͼת���ɵ�����
					// �������ͻ�ȡ����ȡ��Ϊ���ٸ�Ϊ���ٵ�ͼƬ
					if (zuobiaolx.equals("LT"))
						capbmp = Bitmap.createBitmap(windowbmp, wbmph1, wbmpv1,
								Math.abs(wbmph2 - wbmph1),
								Math.abs(wbmpv2 - wbmpv1));
					else if (zuobiaolx.equals("RT"))
						capbmp = Bitmap.createBitmap(windowbmp, windowwidth
								- wbmph1, wbmpv1, Math.abs(wbmph2 - wbmph1),
								Math.abs(wbmpv2 - wbmpv1));
					else if (zuobiaolx.equals("LB"))
						capbmp = Bitmap.createBitmap(windowbmp, wbmph1,
								windowheight - wbmpv1,
								Math.abs(wbmph2 - wbmph1),
								Math.abs(wbmpv2 - wbmpv1));
					/*
					 * if(tmpmingchen.equals("ͨѶ¼ѡ�-����")){
					 * capbmp=twovaluebycolor(capbmp,erzhihuasz,fanse);
					 */
					/*
					 * if(ceshi){ SimpleDateFormat sdf = new SimpleDateFormat(
					 * "yyyy-MM-dd_HH-mm-ss"); File chongxinf = new File(tct,
					 * sdf.format(new Date())+ "-scs.png"); FileOutputStream
					 * outcx; try { outcx = new FileOutputStream(chongxinf);
					 * //resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90,
					 * outtz); capbmp.compress(Bitmap.CompressFormat.PNG, 90,
					 * outcx); outcx.flush(); outcx.close(); } catch (Exception
					 * e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); } }
					 */
					if (erzhihualx.equals("ITX"))
						capay = tmixa(capbmp, erzhihuasz, fanse);
					else if (erzhihualx.equals("XTI"))
						capay = tmxia(capbmp, erzhihuasz, fanse);
					else if ((erzhihualx.equals("VAL"))
							|| (erzhihualx.equals("VAI")))
						capay = tbca(capbmp, erzhihuasz, fanse);

					/*
					 * for(int x = 0;x<capay.length;x++){ for(int y
					 * =0;y<capay[x].length;y++){ tol=tol+1; if
					 * (capay[x][y]==bi) bla=bla+1; } }
					 */
					if (erzhihualx.equals("VAI"))
						nowint = bsia(mbay, capay, lim / 100);
					else
						nowint = asr(mbay, capay, lim / 100);
					// Toast.makeText(this,"��ֵ����"+nowint , 5000).show();
					if (nowint == 0)
						result = result + ';' + tmpmingchen;
					capbmp.recycle();
				}
			}
		}
		// toasthandler.sendEmptyMessage(1);
		return result;
	}

	/*
	 * �ж�����ͼ�Ƿ��ڷ�Χ�ڣ�һ�����ڶ�λ����ͼ:getdwpoint pdfw:�жϷ�Χ wucha:����ȡ����ͼƬ�����������ǰ�����ң���Χ
	 */
	public Point gdp(int dingweibitmapayi, int left, int top, int right,
			int bottom) {
		Bitmap windowbmp = tss();// ��ȡ��ǰ��Ļ
		Point result = new Point();
		result.x = -1;
		result.y = -1;
		int maxint = 0;
		int nowint = -1;
		int windowwidth = windowbmp.getWidth();
		int windowheight = windowbmp.getHeight();
		// Toast.makeText(this,dingweibitmapayi+"��"+dingweibitmapay.size(),
		// 5000).show();
		if (dingweibitmapayi < dba.size()) {
			// Toast.makeText(this,dingweibitmapayi+"��"+dingweibitmapay.size(),
			// 5000).show();
			tm tempmb = (tm) dba.get(dingweibitmapayi);
			String tmpmingchen = tempmb.dh;// �׶�����
			String pdstr = tempmb.cs;// �����ж����꣬��ȡ��ֵ�������ȵ��ִ�
			int[][] mbay = tempmb.pa;// ģ������
			int wbmph1 = gifs(pdstr, 2, 6);// ����Ĵ����ļ���ˮƽ�����һ������
			if (left > 0)
				wbmph1 = left;
			int wbmph2 = gifs(pdstr, 10, 14);// ����Ĵ����ļ���ˮƽ����ڶ�������
			if (right > 0)
				wbmph2 = right;
			int wbmpv1 = gifs(pdstr, 6, 10);// ����Ĵ����ļ��Ĵ�ֱ�����һ������
			if (top > 0)
				wbmpv1 = top;
			int wbmpv2 = gifs(pdstr, 14, 18);// ����Ĵ����ļ��Ĵ�ֱ����ڶ�������
			if (bottom > 0)
				wbmpv2 = bottom;
			String zuobiaolx = pdstr.substring(0, 2);// ��������
			String erzhihualx = pdstr.substring(18, 21);// ��ֵ������
			int erzhihuasz = gifs(pdstr, 21, 24);// ��ֵ����ֵ
			boolean fanse = pdstr.substring(24, 26).equals("YS");// ��ֵ��ʱ�Ƿ�ɫ
			float lim = (float) gifs(pdstr, 26, 29);// �ٽ�ֵ
			// Toast.makeText(this,"1" , 5000).show();
			// result=result+pdstr.substring(0, 2)+";"+pdstr.substring(2,
			// 6)+";"+pdstr.substring(6, 10)+";"+pdstr.substring(10, 14)+
			// ";"+pdstr.substring(14, 18)+";"+pdstr.substring(18,
			// 21)+";"+pdstr.substring(21, 24)+";"+pdstr.substring(24)+"\n";
			boolean iscon = (Math.abs(wbmph1 - wbmph2) <= windowwidth)
					&& (Math.abs(wbmpv1 - wbmpv2) <= windowheight); // �жϸ߶ȿ����û�г����߽�
			// iscon=true;
			if (iscon) {
				int tol = 0;
				int bla = 0;
				Bitmap capbmp = null;// �����жϵ�ʵʱ��ͼ
				int[][] capay = null;// �����жϵ�ʵʱ��ͼת���ɵ�����
				// �������ͻ�ȡ����ȡ��Ϊ���ٸ�Ϊ���ٵ�ͼƬ
				// Toast.makeText(this,zuobiaolx , 5000).show();
				if (zuobiaolx.equals("LT"))
					capbmp = Bitmap.createBitmap(windowbmp, wbmph1, wbmpv1,
							Math.abs(wbmph2 - wbmph1),
							Math.abs(wbmpv2 - wbmpv1));
				else if (zuobiaolx.equals("RT"))
					capbmp = Bitmap.createBitmap(windowbmp, windowwidth
							- wbmph1, wbmpv1, Math.abs(wbmph2 - wbmph1),
							Math.abs(wbmpv2 - wbmpv1));
				else if (zuobiaolx.equals("LB"))
					capbmp = Bitmap.createBitmap(windowbmp, wbmph1,
							windowheight - wbmpv1, Math.abs(wbmph2 - wbmph1),
							Math.abs(wbmpv2 - wbmpv1));

				/*
				 * if(tmpmingchen.equals("ͨѶ¼ѡ�-����")){
				 * capbmp=tbc(capbmp,erzhihuasz,fanse); SimpleDateFormat sdf =
				 * new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss"); File chongxinf
				 * = new File(tct, sdf.format(new Date())+ "-scs.png");
				 * FileOutputStream outcx; try { outcx = new
				 * FileOutputStream(chongxinf);
				 * //resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90,
				 * outtz); capbmp.compress(Bitmap.CompressFormat.PNG, 90,
				 * outcx); outcx.flush(); outcx.close(); } catch (Exception e) {
				 * // TODO Auto-generated catch block e.printStackTrace(); } }
				 */
				if (erzhihualx.equals("ITX"))
					capay = tmixa(capbmp, erzhihuasz, fanse);
				else if (erzhihualx.equals("XTI"))
					capay = tmxia(capbmp, erzhihuasz, fanse);
				else if ((erzhihualx.equals("VAL"))
						|| (erzhihualx.equals("VAI")))
					capay = tbca(capbmp, erzhihuasz, fanse);

				/*
				 * for(int x = 0;x<capay.length;x++){ for(int y
				 * =0;y<capay[x].length;y++){ tol=tol+1; if (capay[x][y]==bi)
				 * bla=bla+1; } }
				 */
				// Toast.makeText(this,"��λ", 5000).show();
				result = gbpa(mbay, capay, lim / 100);
				if (result.x > -1) {
					result.x = result.x + wbmph1;
					result.y = result.y + wbmpv1;
				}
				capbmp.recycle();
			}
		}
		return result;
	}

	/*
	 * �жϵ�ǰ�����Ƿ�仯:isscreenchange pdfw:�жϷ�Χ wucha:����ȡ����ͼƬ�����������ǰ�����ң���Χ
	 * getx:ȡ�������жϵ�ͼƬ��x���� gety:ȡ�������жϵ�ͼƬ��y���� getwidth:ȡ�������жϵ�ͼƬ�Ŀ��
	 * getheight:ȡ�������жϵ�ͼƬ�ĸ߶�
	 */
	public boolean isc(int erzhihuasz, boolean fanse, float lim, int getx,
			int gety, int getwidth, int getheight) {
		Bitmap windowbmp = null;// ��ȡ��ǰ��Ļ
		windowbmp = tss();// ��ȡ��ǰ��Ļ
		windowbmp = Bitmap.createBitmap(windowbmp, getx, gety, getwidth,
				getheight);
		int nowint = -1;
		boolean resultbol = false;
		int[][] nowscay = tbca(windowbmp, erzhihuasz, fanse);// ��ʱ����Ļͼ�����飺nowscreenay
		if (lsay == null)
			lsay = nowscay;// ���һ�ε���Ļͼ�����飺lastscreenay
		nowint = asr(lsay, nowscay, lim / 100);
		//Writelog("xsd��"+nowint);//д����־�����ƶ�
		// mFloatView.setText("��ֵ����"+nowint);
		lsay = nowscay;// ���һ�ε���Ļͼ�����飺lastscreenay
		// Toast.makeText(this,"��ֵ����"+nowint , 5000).show();
		if (nowint == 0)
			resultbol = true;
		else
			resultbol = false;
		// toasthandler.sendEmptyMessage(1);
		return resultbol;
	}

	/*
	 * ����Ļ���н�ͼ������ͼ�����ʽ���أ�takeScreenShot
	 */
	@SuppressLint("SimpleDateFormat")
	public Bitmap tss() {
		Bitmap returnbitmap = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			// String mpSavedPath =
			// Environment.getExternalStorageDirectory()+File.separator+sdf.format(new
			// Date())+ "-screenshot.png" ;
			String mpSavedPath = cwxtp + "/" + sdf.format(new Date())
					+ "-screenshot.png";
			/*
			 * Process sh = Runtime.getRuntime().exec("su", null,null);
			 * OutputStream os = sh.getOutputStream();
			 * os.write(("/system/bin/screencap -p " +
			 * mpSavedPath).getBytes("ASCII")); os.flush(); os.close();
			 * sh.waitFor();
			 */
			edb("/system/bin/screencap -p " + mpSavedPath);
			// Toast.makeText(this,mpSavedPath , 5000).show();
			// �ж�ͼƬ�ļ��Ƿ��Ѿ�����
			File inibitmap = new File(mpSavedPath);
			if (inibitmap.exists()) {
				// �̶��߿�����ͼƬ
				BitmapFactory.Options options = new BitmapFactory.Options();

				options.inJustDecodeBounds = false;

				Bitmap mBitmap = BitmapFactory.decodeFile(mpSavedPath, options);
				// Bitmap mBitmap =
				// BitmapFactory.decodeFile(sdcardpath+"/2016-01-06_21-59-14-screenshot.png",
				// options);
				// mBitmap = twovaluebycolor(mBitmap,229,true);
				// �ǵð�assetsĿ¼�µ�ͼƬ������SD����
				// ԭʼ�ļ���png��׼���±���󱣴�����
				/*
				 * File chongxinf = new File(tct, sdf.format(new Date())+
				 * "-scs.png"); FileOutputStream outcx = new
				 * FileOutputStream(chongxinf);
				 * //resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90,
				 * outtz); mBitmap.compress(Bitmap.CompressFormat.PNG, 90,
				 * outcx); outcx.flush(); outcx.close();
				 */
				// ��������inJustDecodeBoundsΪtrue�����ִ����������bitmapΪ��
				// toasthandler.sendEmptyMessage(1);
				int bmpWidth = mBitmap.getWidth();

				int bmpHeight = mBitmap.getHeight();
				// toasthandler.sendEmptyMessage(1);
				// ����ͼƬ�ĳߴ�

				float scaleWidth = (float) sswh / bmpWidth; // ���̶���С���� sWidth
															// д���Ͷ��

				float scaleHeight = (float) ssht / bmpHeight; //

				Matrix matrix = new Matrix();

				matrix.postScale(scaleWidth, scaleHeight);// �������ź��Bitmap����
				// toasthandler.sendEmptyMessage(1);
				// Bitmap resizeBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
				// bmpWidth, bmpHeight, matrix, true);
				// resizeBitmap = twovaluebycolor(resizeBitmap,229,true);//
				// Toast.makeText(this,getstep(resizeBitmap,"΢��ѡ�-����,ͨѶ¼ѡ�-����,����ѡ�-����,��ѡ�-����,΢��ѡ�-����-����Ϣ,΢��ѡ�-����Ϣ",0),
				// 5000).show();
				// Toast.makeText(this,getstep(resizeBitmap,"",0), 5000).show();
				// getstep(resizeBitmap,"",0);
				returnbitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth,
						bmpHeight, matrix, true);
				// toasthandler.sendEmptyMessage(1);
				// ����������ֵΪ��jietuʱ��ͼ
				if ((ceshi)
						|| ((mlg != null) && (mlg.length() > 0) && (mlg
								.indexOf("jietu") > -1))) {
					File tiaozhengf = new File(cwxtp, sdf.format(new Date())
							+ "-tzf.png");
					FileOutputStream outtz = new FileOutputStream(tiaozhengf);
					returnbitmap.compress(Bitmap.CompressFormat.PNG, 90, outtz);
					outtz.flush();
					outtz.close();
				}
				// ��(100, 20)Ϊ�����ȡ��200��300��ͼƬ
				/*
				 * Bitmap bitmap = Bitmap.createBitmap(mBitmap, 100, 20, 200,
				 * 300); Toast.makeText(this,"123" , 5000).show(); //Bitmap temp
				 * = twovaluebycolor(resizeBitmap,100,true); //File f = new
				 * File(Environment.getExternalStorageDirectory(),
				 * sdf.format(new Date())+ "-tra.jpg"); File f = new File(tct,
				 * sdf.format(new Date())+ "-tra.png"); FileOutputStream out =
				 * new FileOutputStream(f);
				 * //resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				 * //temp.compress(Bitmap.CompressFormat.PNG, 90, out);
				 * //bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				 * bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				 * out.flush(); out.close();
				 */
				// resizeBitmap.
				// temp.recycle();
				// bitmap.recycle();
				// toasthandler.sendEmptyMessage(1);
				mBitmap.recycle();
				inibitmap.delete();// ɾ��ԭʼͼƬ
				// resizeBitmap.recycle();
				// sh.waitFor();
				// ImageView image = (ImageView) findViewById(R.id.image);
				// image.setImageBitmap(resizeBitmap);

				/*
				 * try { Runtime.getRuntime().exec("screencap -p " +
				 * mpSavedPath); } catch (Exception e) { e.printStackTrace();}
				 */
			}
			// else
			// toasthandler.sendEmptyMessage(1);
			// Toast.makeText(this,"��ͼ���", 5000).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnbitmap;
	}

	// ��ʱ����ִ�ж�����dotimerwork
	public void dtw() {
		try {
			// int audiomode=audioManager.getMode();
			// Toast.makeText(this,"��Ƶģʽ��"+audiomode, 5000).show();
			// isc(230,false,90);
			/*
			 * if(isc(230,false,90)) mFloatView.setText("һ��");
			 * //Toast.makeText(this,"һ��", 5000).show(); else
			 * mFloatView.setText("����");
			 */
			// SimpleDateFormat idd = new SimpleDateFormat("mmss");//��ȡ����ʱ��ĸ�ʽ

			String stepstr = gsp("");
			if (stepstr.indexOf("qltjmscan1") > -1) {
				// Toast.makeText(this,"��Ƶģʽ��", 5000).show();
				// wmp.gravity = Gravity.RIGHT | Gravity.TOP;
				// wmp.x = 300;
			}
			stv.setText(stepstr);
			/*
			 * if(!dig){ dig=true;
			 * if((ceshi)||((mlg!=null)&&(mlg.length()>0)&&(mlg
			 * .equals("ceshi")))) Toast.makeText(this,gsp(""), 5000).show();
			 * 
			 * dmti=true;//���봦��΢����Ϣ���߳�
			 * 
			 * }gsp("")
			 */
			// Toast.makeText(getBaseContext(),gsp(""), 5000).show();
			// Looper.prepare();
			// Toast.makeText(getApplicationContext(), "123",
			// Toast.LENGTH_SHORT).show();
			// Looper.loop();

			// Toast.makeText(WxMainService.this,gsp(""), 5000).show();
			// Toast.makeText(getApplicationContext(),gsp(""), 5000).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// dotime1ing=false;
			e.printStackTrace();
		}

	}

	// ��ʱ���Ļص�����
	private Handler handler = new Handler() {
		// ���µĲ���
		@Override
		public void handleMessage(Message msg) {
			dtw();// ִ�м�ʱ����
			// Looper.prepare();
			// Toast.makeText(MainActivity.this, "��Ϣ1",
			// Toast.LENGTH_SHORT).show();
			// Looper.loop();// ����loop�е�ѭ�����鿴��Ϣ����
			super.handleMessage(msg);
		}
	};

	/*
	 * ��ʼ��ʱ������starttimer
	 */
	public void stt() {
		// ������ʱ�� ��ʼ�� time ���� �� timetask ����
		if (tm1 == null)
			tm1 = new Timer();
		tm1t = new TimerTask() {
			// ��ʱ���̷߳���
			@Override
			public void run() {
				handler.sendEmptyMessage(1); // ������Ϣ
			}
		};
		// Timer1.schedule(Timer1Task, 1000, 1000);//ÿ1000����ִ��һ�μ�ʱ����
		tm1.schedule(tm1t, tm1s, tm1s);// ÿ1000����ִ��һ�μ�ʱ����
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// ����Ӧ�ñ���֪ͨ��
		/*
		 * Intent notificationIntent = new Intent(this, MainActivity.class);
		 * PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
		 * notificationIntent, 0); Notification noti = new
		 * Notification.Builder(this) .setContentTitle("΢������ת��Ӧ�ñ��ִ���")
		 * .setContentText("�벻Ҫ�رմ˴��ڣ�лл") .setSmallIcon(R.drawable.ic_launcher)
		 * .setContentIntent(pendingIntent) .build(); startForeground(12346,
		 * noti);
		 */

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Notification notification = new
		// Notification(R.drawable.logo_icon_16,"�ƶ�Ӫ��",
		// System.currentTimeMillis());
		Notification notification = new Notification(R.drawable.ic_launcher,
				"΢������ת��", System.currentTimeMillis());
		Intent stayintent = new Intent(Intent.ACTION_MAIN);
		stayintent.addCategory(Intent.CATEGORY_LAUNCHER);
		stayintent.setClass(this, MainActivity.class);
		stayintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		notification.flags = Notification.FLAG_ONGOING_EVENT; // ���ó�פ Flag
		PendingIntent contextIntent = PendingIntent.getActivity(this, 0,
				stayintent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "΢������ת��",
				"�벻Ҫ�رմ˴��ڣ�лл", contextIntent);
		// notificationManager.notify(R.drawable.ic_launcher, notification);
		startForeground(12346, notification);

		flags = START_STICKY;// START_STICKY����START_STICKY_COMPATIBILITY����service��kill�����Զ���д����
		return super.onStartCommand(intent, flags, startId);
		// return START_REDELIVER_INTENT;
		// return Service.START_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		if (!isf) {// ��������Լ��رյ�
			// �ͷ�Ӧ�ñ���֪ͨ��

			stopForeground(true);
			Intent localIntent = new Intent();
			localIntent.setClass(this, WxMainService.class); // ����ʱ��������Service
			this.startService(localIntent);
		}
		if (flt != null) {
			wmr.removeView(flt);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		// throw new UnsupportedOperationException("Not yet implemented");
		return null;
	}

}
