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
	// 定时器
	private Timer tm1 = null;// 计时器
	// 定时线程
	private TimerTask tm1t = null;// 时间任务
	// 定位时间间隔
	private int tm1s = 10 * 1000;// 时间间隔
	private String ei = "";// 国际移动用户识别码(IMEI，即手机串号)
	private String si = "";// 移动设备国际身份码(跟sim卡唯一对应)
	private String sjh = "";// 手机号
	private String dsb = "";// 读取设备返回的信息
	private String slx = "";// 设备类型
	String mlg = "";// 执行命令：如：包含jietu：全程截图，如:包含jieduan：判断阶段
	float swh = 0;// 手机屏幕宽度
	float sht = 0;// 手机屏幕高度
	float sswh = 432;// 截图屏幕宽度
	float ssht = 768;// 截图屏幕高度
	float hxbl = 0;// 横向屏幕和截图比率
	float zxbl = 0;// 纵向屏幕和截图比率
	private Thread dmt; // 声明处理微信消息的线程：dealwxmsgThread
	private boolean dmtc = true; // 处理微信消息的线程是否继续运行：dealwxmsgThreadcon
	private boolean dmti = false; // 进入处理微信消息的线程的标志：dealwxmsgThreadin
	private AudioManager aum;// 音频管理器：audioManager
	private int bi;// 黑色所用整型数，代表黑色，用于将图片转成数组
	private int wi;// 白色所用整型数，代表白色，用于将图片转成数组
	private String bstr;// 黑色字符，为纯数字，代表在该字符串范围内的数字代表黑色，反之，不在范围内的则为白色
	boolean dig = false;// 正在执行计时器1的任务：dotime1ing
	boolean ceshi = false;// 测试标志，用于开发中需要做额外动作的标志
	// 设备信息是否已经发送
	private boolean dis = false;// 已经发送设备短信：deviceinfosend
	ArrayList jba = new ArrayList();// 判断阶段用图片模板列表：jieduanbitmapay
	ArrayList dba = new ArrayList();// 定位特征坐标用图片模板列表：dingweibitmapay
	Point tp1 = null;// 定义点1用于定位标识：temppoint1
	Point tp2 = null;// 定义点2用于定位标识：temppoint2
	Point tp3 = null;// 定义点3用于定位标识：temppoint3

	MediaRecorder mr;// 录像机：mediarecorder
	private String vex = "";// 录像类型：videoext
	boolean iri = false;// 是否正在记录（录像）， isRecording
	String lvfn = "";// 上次使用的录像全路径：lastvideoFileName

	String fmt = "";// 录像生成时需要发送文件扫描的广播：filemnt
	String scp = "";// 存储卡路径：sdcardpath
	String cwxp = "";// 本应用的路径：controlweixinpath
	String cwxtp = "";// 应用的临时路径：controlweixintemppath
	String cwxmp = "";// 录像目录：controlweixinmoviespath

	String logstr="";//日志字符
	
	// 以下的字符加密都采用自己开发的软件开发专用的加密工具

	// 命名空间
	// 读取设备信息的Webservice的“nameSpace = "http://wxyyzf/";”的加密字符串
	String rdns = "j=uaHR0cDovL3d4eXl6Zi8=y=35A";

	// 调用的方法名称
	// 读取设备信息的Webservice的“methodName = "huoqushebeixinxi";”的加密字符串
	String rdmn = "g==aHVvcXVzaGViZWl4aW54aQ==vx24B";

	// EndPoint
	// 读取设备信息的Webservice的“endPoint =
	// "http://taiderui.com:8883/wxyyzf/shebeibiaoPort";”的加密字符串
	String rdep = "Ye=aHR0cDovL3RhaWRlcnVpLmNvbTo4ODgzL3d4eXl6Zi9zaGViZWliaWFvUG9ydA==np=t0";
	// 读取设备信息的Webservice的“endPoint =
	// "http://192.168.1.100:8080/wxyyzf/shebeibiaoPort";”的加密字符串
	// String
	// rdep="w3=aHR0cDovLzE5Mi4xNjguMS4xMDA6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=CD=JP";
	// 读取设备信息的Webservice的“endPoint =
	// "http://192.168.1.101:8080/wxyyzf/shebeibiaoPort";”的加密字符串
	// String
	// rdep="=suaHR0cDovLzE5Mi4xNjguMS4xMDE6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=yy=37";
	// 读取设备信息的Webservice的“endPoint =
	// "http://192.168.1.102:8080/wxyyzf/shebeibiaoPort";”的加密字符串
	// String
	// rdep="==JaHR0cDovLzE5Mi4xNjguMS4xMDI6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=P=T=d";
	// 读取设备信息的Webservice的“endPoint =
	// "http://192.168.1.103:8080/wxyyzf/shebeibiaoPort";”的加密字符串
	// String
	// rdep="=bhaHR0cDovLzE5Mi4xNjguMS4xMDM6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=km=qw";
	// 读取设备信息的Webservice的“endPoint =
	// "http://192.168.1.104:8080/wxyyzf/shebeibiaoPort";”的加密字符串
	// String
	// rdep="=LRaHR0cDovLzE5Mi4xNjguMS4xMDQ6ODA4MC93eHl5emYvc2hlYmVpYmlhb1BvcnQ=VXZ=g";

	// SOAP Action
	// 读取设备信息的Webservice的“soapAction = "http://wxyyzf/huoqushebeixinxi";”的加密字符串
	String rdsa = "X=haHR0cDovL3d4eXl6Zi9odW9xdXNoZWJlaXhpbnhpkmo=w";

	// 命名空间
	// 读取模板信息的Webservice的“nameSpace = "http://wxyyzf/";”的加密字符串
	String rmns = "J=TaHR0cDovL3d4eXl6Zi8===ZZf";

	// 调用的方法名称
	// 读取模板信息的Webservice的“methodName = "huoqumuban";”的加密字符串
	String rmmn = "=g=aHVvcXVtdWJhbg==p=t==";

	// EndPoint
	// 读取模板信息的Webservice的“endPoint =
	// "http://taiderui.com:8883/wxyyzf/mubanbiaoPort";”的加密字符串
	String rmep = "sy5aHR0cDovL3RhaWRlcnVpLmNvbTo4ODgzL3d4eXl6Zi9tdWJhbmJpYW9Qb3J08ACGK";
	// 读取模板信息的Webservice的“endPoint =
	// "http://192.168.1.100:8080/wxyyzf/mubanbiaoPort";”的加密字符串
	// String
	// rmep="=ntaHR0cDovLzE5Mi4xNjguMS4xMDA6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA===0==B";
	// 读取模板信息的Webservice的“endPoint =
	// "http://192.168.1.101:8080/wxyyzf/mubanbiaoPort";”的加密字符串
	// String
	// rmep="=79aHR0cDovLzE5Mi4xNjguMS4xMDE6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA====EGK";
	// 读取模板信息的Webservice的“endPoint =
	// "http://192.168.1.102:8080/wxyyzf/mubanbiaoPort";”的加密字符串
	// String
	// rmep="=puaHR0cDovLzE5Mi4xNjguMS4xMDI6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==0==7=";
	// 读取模板信息的Webservice的“endPoint =
	// "http://192.168.1.103:8080/wxyyzf/mubanbiaoPort";”的加密字符串
	// String
	// rmep="=7=aHR0cDovLzE5Mi4xNjguMS4xMDM6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==G=KMS";
	// 读取模板信息的Webservice的“endPoint =
	// "http://192.168.1.104:8080/wxyyzf/mubanbiaoPort";”的加密字符串
	// String
	// rmep="=7=aHR0cDovLzE5Mi4xNjguMS4xMDQ6ODA4MC93eHl5emYvbXViYW5iaWFvUG9ydA==G=KMS";

	// SOAP Action
	// 读取模板信息的Webservice的“soapAction = "http://wxyyzf/huoqumuban";”的加密字符串
	String rmsa = "2=CaHR0cDovL3d4eXl6Zi9odW9xdW11YmFu=I=NV";

	// 关闭指令是否来源于软件本身：isselfclose
	private boolean isf = false;
	// 是否正在执行任务：istasking
	private boolean iti = false;

	// 定义浮动窗口布局：floatLayout
	LinearLayout flt;
	WindowManager.LayoutParams wmp;// wmParams
	// 创建浮动窗口设置布局参数的对象
	WindowManager wmr;// windowmanager

	Button mvbt;// 悬浮录制视频按钮：makevideobt
	CheckBox aschk;// 悬浮自动发送勾选框：autosendchk
	Button svbt;// 悬浮发送视频按钮：sendvideobt
	Button ebt;// 悬浮关闭按钮：exitbt
	SurfaceView vrv;// 录像显示界面：videorecordView
	TextView stv;// 状态显示：statustv

	int[][] lsay = null;// 最后一次的屏幕图形数组：lastscreenay
	Camera cam = null;// 用于浏览的旋转

	String sjxh = "";// 手机型号：shoujixinghao
	String sjpp = "";// 手机品牌：shoujipinpai
	String sdkbb = "";// SDK版本：sdkbanben
	String xtbb = "";// 系统版本：xitongbanben
	String mubb = "";// MIUI版本：miuibanben

	// 该种方法的提示只能使用一次
	/*
	 * Looper.prepare(); Toast.makeText(getApplicationContext(), "消息1",
	 * 1).show(); Looper.loop();// 进入loop中的循环，查看消息队列
	 */
	// Thread.currentThread().sleep(10000);会占着cpu，所以不用
	// Thread.sleep(10000);会占着cpu，所以不用

	@SuppressLint({ "ShowToast", "NewApi" })
	@Override
	public void onCreate() {
		// 获取存储卡路径
		// sdp=Environment.getExternalStorageDirectory().toString()+"/";
		try {
			super.onCreate();

			aum = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			android.view.Display display = wm.getDefaultDisplay();
			Point pt = new Point();
			display.getRealSize(pt);

			sjxh = android.os.Build.MODEL;// 手机型号：shoujixinghao
			sjpp = android.os.Build.BRAND;// 手机品牌：shoujipinpai
			sdkbb = android.os.Build.VERSION.SDK;// SDK版本：sdkbanben
			xtbb = android.os.Build.VERSION.RELEASE;// 系统版本：xitongbanben
			mubb = android.os.Build.VERSION.INCREMENTAL;// MIUI版本：miuibanben

			/*
			 * Toast.makeText(this, pt.x+","+pt.y, 1).show();
			 * //截图判断大小，并以此来获得图像分辨率，并作为手机分辨率 //截图
			 * edb("/system/bin/screencap -p " + tct+"/getsc.png"); //载入图片
			 * BitmapFactory.Options options = new BitmapFactory.Options();
			 * options.inJustDecodeBounds = false; Bitmap tempBitmap =
			 * BitmapFactory.decodeFile(tct+"/getsc.png", options); //获取图片宽度和高度
			 * swh = tempBitmap.getWidth();//手机屏幕宽度 sht =
			 * tempBitmap.getHeight();//手机屏幕高度 //Toast.makeText(this,
			 * screenwidth+","+screenheight, 5000).show();
			 */
			swh = pt.x;// 手机屏幕宽度
			sht = pt.y;// 手机屏幕高度
			// 获取分辨率和截图高宽比率
			hxbl = swh / sswh;// 横向屏幕和截图比率
			zxbl = sht / ssht;// 纵向屏幕和截图比率
			
			if ((hxbl > 0) && (zxbl > 0)) {
				// 获取imei和imsi
				boolean gds = false;// 获取数据成功：getdatasuccess
				TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				si = TelephonyMgr.getSubscriberId();// 移动设备国际身份码(跟sim卡唯一对应)
				if ((si == null) || (si.length() <= 0))
					si = "000000000000000";
				ei = TelephonyMgr.getDeviceId();// 国际移动用户识别码(IMEI，即手机串号)
				if (si.length() > 0 && ei.length() > 0) {
					rdv();// 读取设备信息
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
					// 判断存储卡路径下本应用的临时目录是否存在，不存在则创建
					File tdrcwtemp = new File(cwxtp);
					if (!tdrcwtemp.exists())
						tdrcwtemp.mkdir();
					// 判断存储卡路径下本应用的录像目录是否存在，不存在则创建
					File tdrcwmov = new File(cwxmp);
					if (!tdrcwmov.exists())
						tdrcwmov.mkdir();
					File logf = new File(cwxp + "/runlog.txt");
					if (!logf.exists())
						logf.createNewFile();
					rmb();// 读取模板信息
					// Toast.makeText(this, "b:"+bi+";w:"+wi+";bstr:"+bstr,
					// 5000).show();//readmuban();//读取模板信息
					gds = (jba.size() > 0) && (fmt.length() > 0);// 如果获取的阶段模板数大于0则代表获取数据成功
					// if((jba.size()>0)&&(dba.size()>0)){
					// 判断存储卡路径下本应用的临时目录是否存在，不存在则创建

					if (gds) {
						// 创建悬浮窗
						createFloatView();

						Toast.makeText(this, dsr("l=s5ZCv5Yqo5oiQ5Yqfu==y3"), 1)
								.show();
						if ((ceshi)
								|| ((mlg != null) && (mlg.length() > 0) && (mlg
										.indexOf("jieduan") > -1))) {
							stt();// 开始识别任务
						}
					} else
						Toast.makeText(this, dsr("J=T5ZCv5Yqo5aSx6LSlXZ=f="), 1)
								.show();

					// stt();//开始识别任务
					// Toast.makeText(this, "启动成功", 1).show();
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
	 * 获取微信版本名：getweixinversion，缩写：gwxv
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

			if(tmpInfo.appName.equals("微信"))
				weixinbanbenming=tmpInfo.versionCode+"---"+tmpInfo.versionName;

		}// 好啦 这下手机上安装的应用数据都存在appList里了。
		return weixinbanbenming;
	}
	
	/*
	 * 获取应用列表：getapplist，缩写：gal
	 */
	public ArrayList<AppInfo> gal() {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据
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

		}// 好啦 这下手机上安装的应用数据都存在appList里了。
		return appList;
	}

	// 创建悬浮窗体
	private void createFloatView() {
		wmp = new WindowManager.LayoutParams();
		// 获取WindowManagerImpl.CompatModeWrapper
		wmr = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		// 设置window type
		wmp.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmp.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmp.flags =
		// LayoutParams.FLAG_NOT_TOUCH_MODAL |
		LayoutParams.FLAG_NOT_FOCUSABLE
		// LayoutParams.FLAG_NOT_TOUCHABLE
		;

		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmp.gravity = Gravity.LEFT | Gravity.TOP;

		// 以屏幕左上角为原点，设置x、y初始值
		wmp.x = 1;
		wmp.y = 105;

		// 设置悬浮窗口长宽数据
		// wmp.width = 40;
		// wmp.height = 330;

		// 设置悬浮窗口长宽数据
		wmp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		flt = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
		// 添加mFloatLayout
		wmr.addView(flt, wmp);
		/*
		 * Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
		 * Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
		 * Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop()); Log.i(TAG,
		 * "mFloatLayout-->bottom" + mFloatLayout.getBottom());
		 */
		mvbt = (Button) flt.findViewById(R.id.makevideobt);// 悬浮录制视频按钮：makevideobt
		aschk = (CheckBox) flt.findViewById(R.id.autosendchk);// 悬浮录制视频按钮：makevideobt
		aschk.setPivotX(1);
		aschk.setTextColor(Color.RED); 
		svbt = (Button) flt.findViewById(R.id.sendvideobt);// 悬浮发送视频按钮：：sendvideobt
		ebt = (Button) flt.findViewById(R.id.exitbt);// 悬浮关闭按钮：exitbt
		ebt.setPivotX(50);
		ebt.setPivotY(1);
		stv = (TextView) flt.findViewById(R.id.statustv);// 状态显示：statustv
		stv.setTextColor(Color.RED);
		vrv = (SurfaceView) flt.findViewById(R.id.videorecordview);// 录像显示界面：videorecordView
		flt.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		/*
		 * Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
		 * Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);
		 */
		// 设置任务按钮的点击事件
		ebt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!iti) {
					isf = true;// 设置关闭指令来源于软件本身
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

		// 设置任务按钮的触摸移动
		mvbt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/*
				 * 不移动 // TODO Auto-generated method stub
				 * //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标 wmParams.x = (int)
				 * event.getRawX() - mFloatView.getMeasuredWidth()/2;
				 * //Log.i(TAG, "Width/2--->" +
				 * mFloatView.getMeasuredWidth()/2); Log.i(TAG, "RawX" +
				 * event.getRawX()); Log.i(TAG, "X" + event.getX()); //25为状态栏的高度
				 * wmParams.y = (int) event.getRawY() -
				 * mFloatView.getMeasuredHeight()/2 - 25; // Log.i(TAG,
				 * "Width/2--->" + mFloatView.getMeasuredHeight()/2); Log.i(TAG,
				 * "RawY" + event.getRawY()); Log.i(TAG, "Y" + event.getY());
				 * //刷新 mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				 */
				return false;
			}
		});
		// 设置录制视频按钮的点击事件
		mvbt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { 

				try {stv.setText("开始任务");
					// if((sjxh+sjpp+sdkbb+xtbb+mubb).equals("2014813Xiaomi194.4.45.12.17")){//判断手机型号品牌sdk版本安卓版本miui版本是否符合要求
					if (((sjxh + sjpp + sdkbb + xtbb + mubb)
							.indexOf(dsr("==SMjAxNDgxM1hpYW9taTE5NC40LjQ1LjEyLjE3WY=e=")))>-1) {// 判断手机型号品牌sdk版本安卓版本miui版本是否符合要求
						iti = true;// 正在执行任务置为是
						SimpleDateFormat iddf = new SimpleDateFormat(
								"yyyyMMddHHmmss");// 设置按日期生成文件名的格式
						SimpleDateFormat idd = new SimpleDateFormat("mmss");// 获取结束时间的格式
						int checky = 0;// 遍历的y坐标
						// 遍历当前页的语音
						Writelog("yybsdq");//写入日志：语音标识读取
						tp2 = gdp(0, 0, checky, 0, 0);// 读取语音消息标识
						// if(tp2.x<0)
						// tp2=gdp(0,0,checky,0,0);//读取语音消息标识
						while (tp2.x > -1) {// 如果读取语音消息标识的横向坐标大于-1
							Writelog("lxks");//写入日志：录像开始
							sar("video" + iddf.format(new Date()));// 开始录像
							Writelog("yybf");//写入日志：播放语音
							csn("LT", 100, tp2.y + 5);// 播放语音
							synchronized (this) {
								wait(1500);
							}// 暂停4000毫秒
							while (aum.isMusicActive()) {// 判断是否还在播放
								synchronized (this) {
									wait(2000);
								}// 暂停2000毫秒
							}
							synchronized (this) {
								wait(1500);
							}// 暂停2000毫秒
							Writelog("lxtz");//写入日志：录像停止
							sor();// 停止录像
							// 已经播放完成
							checky = tp2.y + 3;// 遍历的y坐标加3，防止语音被再找到，跳过此条语音
							tp2 = gdp(0, 0, checky, 0, 0);// 读取语音消息标识
						}
						Writelog("xxtz");//写入日志
						// 开始上移界面遍历语音
						boolean isend = false;// 是否到头
						boolean lastfindclickvoice = false;// 上一次移动找到并点击了语音，因为安卓移动不准确，而语音标识之间相差大于此误差，所以要加此判断
						int sametimes = 0;// 屏幕一样的次数
						boolean screensame = false;// 屏幕没有变化 
						screensame = isc(230, false, 100, 16, 593, 400, 115);// 判断屏幕有无变化，此处主要是获取初始屏幕
						while (!isend) {// 如果没有到达屏幕末尾
							if (msn(216, 320, 216, 300)) {// 往上移动一个语音标识高度的位置
								Writelog("syyb");//写入日志：上移一步
								// if(msn(216,358,216,300)){//往上移动一个语音标识高度的位置
								synchronized (this) {
									wait(1000);
								}// 暂停1000毫秒
								screensame = isc(230, false, 100, 16, 593, 400,
										115);// 判断屏幕有无变化
								if (!screensame) {// 如果屏幕有变化
									Writelog("pmyb");//写入日志：屏幕有变
									sametimes = 0;// 重置屏幕一样的次数，置为0
									// tp2=gdp(0,0,641,0,708);//读取语音消息标识：temppoint2
									tp2 = gdp(0, 0, 669, 0, 708);// 读取语音消息标识：temppoint2
									if (tp2.x > -1) {// 如果未读消息和读取语音消息标识的横向坐标均大于-1
										if (!lastfindclickvoice) {// 如果上次移动后是语音且点击了
											Writelog("lxks");//写入日志：开始录像
											sar("video"
													+ iddf.format(new Date()));// 开始录像
											Writelog("yybf");//写入日志：播放语音
											csn("LT", 100, tp2.y + 5);// 播放语音
											synchronized (this) {
												wait(1500);
											}// 暂停4000毫秒
											while (aum.isMusicActive())
												// 判断是否还在播放
												synchronized (this) {
													wait(2000);
												}// 暂停2000毫秒
											lastfindclickvoice = true;// 上一次移动找到了语音
											synchronized (this) {
												wait(1000);
											}// 暂停4000毫秒
											Writelog("lxtz");//写入日志：停止录像
											sor();// 停止录像
										}
									} else
										lastfindclickvoice = false;// 上一次移动找到了语音
								} else {
									Writelog("pmwb");//写入日志：屏幕有变
									sametimes = sametimes + 1;// 屏幕一样的次数累加，加1
									if (sametimes > 5) {// 屏幕一样的次数大于临界，临界值设置为5
										sametimes = 0;// 重置屏幕一样的次数，置为0
										mvbt.setText(idd.format(new Date()));// 设置结束的时间
										Writelog("lzjs");//写入日志：录制结束
										isend = true;// 到头了
									}
								}

							}
						}
						csn("LT", 28, 59);// 返回到微信主界面
						synchronized (this) {
							wait(1000);
						}// 暂停4000毫秒
						if (aschk.isChecked())
							svbt.performClick();
					}
					
					// sar("video"+iddf.format(new Date()));//开始录像
					// Thread.currentThread().sleep(5000);
					// sor();//停止录像
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 设置发送视频按钮的点击事件
		svbt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					// if(((sjxh+sjpp+sdkbb+xtbb+mubb).equals("2014813Xiaomi194.4.45.12.17")))>-1){//判断手机型号品牌sdk版本安卓版本miui版本是否符合要求
					if (((sjxh + sjpp + sdkbb + xtbb + mubb)
							.indexOf(dsr("==SMjAxNDgxM1hpYW9taTE5NC40LjQ1LjEyLjE3WY=e=")))>-1) {// 判断手机型号品牌sdk版本安卓版本miui版本是否符合要求
						iti = true;// 正在执行任务置为是

						SimpleDateFormat iddf = new SimpleDateFormat(
								"yyyyMMddHHmmss");// 设置按日期生成文件名的格式
						SimpleDateFormat idd = new SimpleDateFormat("mmss");// 获取结束时间的格式
						int checky = 0;// 遍历的y坐标
						String stepstr = "";// 阶段字符串
						// 一直等待，直到切换到微信主界面
						while (gsp("txlxxk-fty1").indexOf("txlxxk-fty1") < 0)
							synchronized (this) {
								wait(500);
							}// 暂停500毫秒
						csn("LB", 162, 32);// 点击通讯录选项卡按钮
						// 一直等待，直到找到群聊按钮，此时代表进入了通讯录选项卡，并返回群聊按钮的坐标
						tp1 = gdp(1, 0, 0, 0, 0);
						while (tp1.x < 0) {
							synchronized (this) {
								wait(500);
							}// 暂停500毫秒
							tp1 = gdp(1, 0, 0, 0, 0);
						}
						csn("LT", tp1.x + 18, tp1.y + 10);// 点击通讯录选项卡群聊按钮
						// 一直等待，直到切换到微信群聊群列表界面
						while (gsp("qlqlbjm1").indexOf("qlqlbjm1") < 0)
							synchronized (this) {
								wait(500);
							}// 暂停500毫秒
						// 准备进入群发
						ArrayList needtosendal = new ArrayList();
						// 读取需要发送的群
						needtosendal = rtftal(cwxp + "/sendto.txt");

						// 如果取到的群记录小于或等于0
						if (needtosendal.size() <= 0) {
							// stv.setText("没有待发送的群，请到存储卡目录下检查："+cwxp+"/sendto.txt是否存在及里面是否有群的记录");
							stv.setText(dsr("0=95rKh5pyJ5b6F5Y+R6YCB55qE576k77yM6K+35Yiw5a2Y5YKo5Y2h55uu5b2V5LiL5qOA5p+l77ya=FHL=")
									+ cwxp
									+ dsr("djmL3NlbmR0by50eHTmmK/lkKblrZjlnKjlj4rph4zpnaLmmK/lkKbmnInnvqTnmoTorrDlvZU=ssuy5"));
						} else {// 如果取到的群记录大于0
								// 打开第一个群
								// 先将第一个群的群名称放入粘贴板

							ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clipboardManager.setPrimaryClip(ClipData
									.newPlainText(null,
											(String) needtosendal.get(0)));
							csn("LT", 327, 57);// 点击群聊群列表界面的搜索按钮
							synchronized (this) {
								wait(1000);
							}// 暂停1000毫秒
							csn("LT", 180, 60);// 点击编辑框
							synchronized (this) {
								wait(500);
							}// 暂停1000毫秒
							csn("LT", 180, 60);// 点击编辑框，两次点击后会跳出粘贴菜单
							synchronized (this) {
								wait(500);
							}// 暂停1000毫秒
							isc(195, false, 100, 15, 96, 32, 15);// 判断屏幕有无变化，此处主要是获取初始屏幕
							csn("LT", 100, 90);// 点击粘贴菜单
							while (isc(195, false, 100, 15, 96, 32, 15))
								// 如果首行标志（群聊字样）没有变化（即被第一个群覆盖），则一直等待
								synchronized (this) {
									wait(500);
								}// 暂停1000毫秒
							csn("LT", 85, 125);// 点击打开搜索到的第一个群
							// 开始发送任务

							boolean sendvideoistoppage = false;// 发送的视频是否到首页
							boolean continuesend = true;// 继续发送
							int usedvideoi = 0;// 已经使用到的视频序号
							int usedrowi = 1;// 已经使用到的行号
							int toppagey = 650;// 遍历首页时的y坐标
							String logstr = "";// 日志文件
							int sendoki = 0;// 发送完毕的视频数

							while (continuesend) {
								// while(gsp("qltjm1").indexOf("qltjm1")<0)//如果没有进入群聊天界面，则一直等待
								synchronized (this) {
									wait(1500);
								}// 暂停1000毫秒
								// 查找未传完标志的坐标，如存在这个坐标，则说明没有传完，则继续等待
								tp1 = gdp(5, 0, 0, 0, 0);
								while (tp1.x > -1) {
									synchronized (this) {
										wait(500);
									}// 暂停500毫秒
									tp1 = gdp(5, 0, 0, 0, 0);
								}
								// 进入转发
								if (sendoki > 0) {
									for (int i = 1; i < needtosendal.size(); i++) {
										if (sendoki == 1)
											lps("LT", 295, 185);// 长按视频
										else if (sendoki == 2)
											lps("LT", 295, 375);// 长按视频
										lps("LT", 295, 580);// 长按视频
										// 一直尝试获取转发菜单按钮坐标，直至获取
										tp1 = gdp(7, 0, 0, 0, 0);
										while (tp1.x < 0) {
											synchronized (this) {
												wait(500);
											}// 暂停500毫秒
											tp1 = gdp(7, 0, 0, 0, 0);
										}
										// stv.setText("点击转发按钮");
										csn("LT", tp1.x + 18, tp1.y + 8);// 点击转发菜单按钮
										while (gsp("qltspzfqxzk1").indexOf(
												"qltspzfqxzk1") < 0)
											// 如果没有进入群转发选择界面，则一直等待
											synchronized (this) {
												wait(500);
											}// 暂停500毫秒
										// 先将第一个群的群名称放入粘贴板

										// ClipboardManager clipboardManager =
										// (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
										clipboardManager.setPrimaryClip(ClipData
												.newPlainText(null,
														(String) needtosendal
																.get(i)));
										csn("LT", 85, 130);// 点击编辑框
										synchronized (this) {
											wait(1000);
										}// 暂停1000毫秒
										csn("LT", 85, 130);// 点击编辑框
										synchronized (this) {
											wait(1000);
										}// 暂停1000毫秒
										isc(195, false, 100, 70, 185, 165, 70);// 判断屏幕有无变化，此处主要是获取初始屏幕
										csn("LT", 60, 100);// 点击粘贴菜单
										while (isc(195, false, 100, 70, 185,
												165, 70))
											// 如果首行标志（群聊字样）没有变化（即被第一个群覆盖），则一直等待
											synchronized (this) {
												wait(500);
											}// 暂停1000毫秒
										csn("LT", 115, 225);// 点击打开搜索到的第一个群
										// 一直尝试获取群聊天视频转发确认发送按钮坐标，直至获取
										tp1 = gdp(8, 0, 0, 0, 0);
										while (tp1.x < 0) {
											synchronized (this) {
												wait(500);
											}// 暂停500毫秒
											tp1 = gdp(8, 0, 0, 0, 0);
										}
										csn("LT", tp1.x + 18, tp1.y + 8);// 点击打开搜索到的第一个群
										// while(gsp("qltjm1").indexOf("qltjm1")<0)//如果没有进入群聊天界面，则一直等待
										synchronized (this) {
											wait(1500);
										}// 暂停500毫秒
									}
								}
								csn("RB", 33, 30);// 点击群聊天界面里的增加媒体按钮
								// 一直等待，直到找到群聊天界面图片按钮
								tp1 = gdp(4, 0, 0, 0, 0);
								while (tp1.x < 0) {
									synchronized (this) {
										wait(500);
									}// 暂停500毫秒
									tp1 = gdp(4, 0, 0, 0, 0);
								}
								csn("LT", tp1.x + 18, tp1.y + 13);// 点击群聊天界面图片按钮
								while (gsp("lttphspxzjm1").indexOf(
										"lttphspxzjm1") < 0)
									// 如果没有进入聊天图片和视频选择界面，则一直等待
									synchronized (this) {
										wait(500);
									}// 暂停500毫秒
								int sendvideoi = 0;// 发送到的视频序号
								int sendrowi = 1;// 发送到的
								boolean screensame = false;// 屏幕没有变化
								boolean isend = false;// 是否拉到头
								int sametimes = 0;// 屏幕一样的次数
								boolean havefound = false;// 已经找到符合条件的视频
								Point temppoint = new Point();// 临时点，用于记载获得的点用于点击
								temppoint.set(-1, -1);// 初始化为(-1,-1)

								if (!sendvideoistoppage) {// 如果发送图片还没选到首页
									screensame = isc(177, true, 100, 143, 210,
											145, 498);// 判断屏幕有无变化，此处主要是获取初始屏幕
									while (!isend) {// 有没有拉到头
										// if(msn(216,498,216,300)){//往上移动一个语音标识高度的位置
										if (msn(216, 600, 216, 100)) {// 往上移动一个语音标识高度的位置
											synchronized (this) {
												wait(1000);
											}// 暂停1000毫秒
											screensame = isc(177, true, 100,
													143, 210, 145, 498);// 判断屏幕有无变化
											if (screensame) {
												if (sametimes > 1) {// 屏幕一样的次数大于临界，临界值设置为5
													sametimes = 0;// 重置屏幕一样的次数，置为0
													// svbt.setText(idd.format(new
													// Date()));//设置结束的时间
													isend = true;// 到头了
												}
												sametimes = sametimes + 1;// 屏幕一样的次数累加，加1
											} else
												sametimes = 0;// 重置屏幕一样的次数，置为0
										}
									}
									isend = false;// 是否拉到头
									screensame = isc(177, true, 100, 151, 210,
											32, 498);// 判断屏幕有无变化，此处主要是获取初始屏幕
									boolean continueothertwo = true;// 判断其余两个
									while (!isend) {
										tp1 = gdp(2, 397, 560, 427, 708);// 读取语音消息标识：temppoint1
										if (tp1.x > -1) {
											temppoint.set(tp1.x + 10,
													tp1.y + 10);// 将需要点击的点赋予临时点
											if (usedrowi <= sendrowi) {
												continueothertwo = true;
												sendvideoi = sendvideoi + 1;// 遍历到的视频序号
												if (sendvideoi > usedvideoi) {
													havefound = true;
												}
											} else
												continueothertwo = false;
										}
										if (continueothertwo && !havefound) {
											tp2 = gdp(2, 251, 560, 283, 708);// 读取语音消息标识：temppoint1
											if (tp2.x > -1) {
												temppoint.set(tp2.x + 10,
														tp2.y + 10);// 将需要点击的点赋予临时点
												sendvideoi = sendvideoi + 1;// 遍历到的视频序号
												if (sendvideoi > usedvideoi) {
													havefound = true;
												}
											}
											if (!havefound) {
												tp3 = gdp(2, 107, 560, 139, 708);// 读取语音消息标识：temppoint1
												if (tp3.x > -1) {
													temppoint.set(tp3.x + 10,
															tp3.y + 10);// 将需要点击的点赋予临时点
													sendvideoi = sendvideoi + 1;// 遍历到的视频序号
													if (sendvideoi > usedvideoi) {
														sendrowi = sendrowi + 1;
														havefound = true;
													}
												}
											}
										}
										if (!havefound) {
											if (temppoint.y > 650) {// 如果当次勾选的纵坐标大于650
												msn(216, 300 + 650 - 600, 216,
														300);// 则将坐标修正到600的纵坐标
												synchronized (this) {
													wait(500);
												}// 暂停500毫秒
											}
											if (msn(216, 302, 216, 436)) {// 往上移动一个语音标识高度的位置
												synchronized (this) {
													wait(1000);
												}// 暂停500毫秒
												isend = gsp("lttphspxzjmpszp1")
														.indexOf(
																"lttphspxzjmpszp1") > -1;// 到头了
												sendvideoistoppage = isend;
												if (sendvideoistoppage)
													usedvideoi = 0;// 如果已经到首页了，则把已经使用到的视频序号重置为1
											}
										} else {
											isend = true;// 到头了，其实是找到了符合条件的视频，而不是到头
										}
									}
								}
								if (!havefound) {
									if (sendvideoistoppage) {
										while ((toppagey >= 20) && (!havefound)) {// 20是勾选按钮的高度：19再加上1
											tp1 = gdp(2, 397, toppagey - 134,
													427, toppagey);// 读取语音消息标识：temppoint1
											if (tp1.x > -1) {
												if (usedvideoi < 1) {
													usedvideoi = 1;
													havefound = true;
													temppoint.set(tp1.x + 10,
															tp1.y + 10);// 将需要点击的点赋予临时点
												}
											}
											if (!havefound) {
												tp2 = gdp(2, 251,
														toppagey - 134, 283,
														toppagey);// 读取语音消息标识：temppoint1
												if (tp2.x > -1) {
													if (usedvideoi < 2) {
														usedvideoi = 2;
														havefound = true;
														temppoint.set(
																tp2.x + 10,
																tp2.y + 10);// 将需要点击的点赋予临时点
													}
												}
											}
											if (!havefound) {
												tp3 = gdp(2, 107,
														toppagey - 134, 139,
														toppagey);// 读取语音消息标识：temppoint1
												if (tp3.x > -1) {
													if (usedvideoi < 3) {
														havefound = true;
														temppoint.set(
																tp3.x + 10,
																tp3.y + 10);// 将需要点击的点赋予临时点
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
									sendoki = sendoki + 1;// 发送完毕的视频数
									csn("LT", temppoint.x, temppoint.y);// 如果找到了就按临时点点击勾选
									while (gsp("ltspkfszt1").indexOf(
											"ltspkfszt1") < 0)
										// 如果没有进入聊天视频可发送状态，则一直等待
										synchronized (this) {
											wait(500);
										}// 暂停500毫秒
									csn("LT", 365, 60);// 点击发送
								}
							}
							// SimpleDateFormat iddf = new
							// SimpleDateFormat("yyyyMMddHHmmss");//
							// 设置按日期生成文件名的格式
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
						stv.setText(stv.getText() + "结束");

						/*
						 * //将视频列表拖到最后 int sendvideoi=0;//发送到的视频序号 boolean
						 * sendvideoistoppage=false;//发送的视频是否到首页 boolean
						 * screensame=false;//屏幕没有变化 boolean isend=false;//是否到头
						 * int sametimes=1;//屏幕一样的次数
						 * 
						 * screensame=isc(177,true,100,143,210, 145,
						 * 498);//判断屏幕有无变化，此处主要是获取初始屏幕 while(!isend){
						 * //if(msn(216,498,216,300)){//往上移动一个语音标识高度的位置
						 * if(msn(216,600,216,100)){//往上移动一个语音标识高度的位置
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * screensame=isc(177,true,100,143,210, 145,
						 * 498);//判断屏幕有无变化 if(screensame){
						 * if(sametimes>1){//屏幕一样的次数大于临界，临界值设置为5
						 * sametimes=0;//重置屏幕一样的次数，置为0
						 * //svbt.setText(idd.format(new Date()));//设置结束的时间
						 * isend=true;//到头了 }
						 * sametimes=sametimes+1;//屏幕一样的次数累加，加1 } else
						 * sametimes=0;//重置屏幕一样的次数，置为0 } }
						 * 
						 * int findi=0;//遍历到的视频序号 sametimes=0;//屏幕一样的次数
						 * isend=false;//是否到头 int toppagey=650;//遍历首页时的y坐标
						 * 
						 * screensame=isc(177,true,100,151,210, 32,
						 * 498);//判断屏幕有无变化，此处主要是获取初始屏幕 while(!isend){
						 * tp1=gdp(2,397,560,427,708);//读取语音消息标识：temppoint1
						 * if(tp1.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp1.x+10,tp1.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp1.x+10,tp1.y+10);//点击勾选 }
						 * tp2=gdp(2,251,560,283,708);//读取语音消息标识：temppoint1
						 * if(tp2.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp2.x+10,tp2.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp2.x+10,tp2.y+10);//点击勾选 }
						 * tp3=gdp(2,107,560,139,708);//读取语音消息标识：temppoint1
						 * if(tp3.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp3.x+10,tp3.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp3.x+10,tp3.y+10);//点击勾选 }
						 * 
						 * if(tp3.y>650){//如果当次勾选的纵坐标大于650
						 * msn(216,300+650-600,216,300);//则将坐标修正到600的纵坐标
						 * synchronized(this){wait(500);}//暂停500毫秒 }
						 * //tbt.setText("v"+sendvideoi);//设置结束的时间
						 * if(msn(216,302,216,436)){//往上移动一个语音标识高度的位置
						 * synchronized(this){wait(500);}//暂停500毫秒
						 * isend=gsp("lttphspxzjmpszp1"
						 * ).indexOf("lttphspxzjmpszp1")>-1;//到头了
						 * sendvideoistoppage=isend; } }
						 * 
						 * sendvideoistoppage=true; //sendvideoi=0;//遍历到的视频序号
						 * if(sendvideoistoppage){
						 * while(toppagey>=20){//20是勾选按钮的高度：19再加上1
						 * tp1=gdp(2,397,
						 * toppagey-134,427,toppagey);//读取语音消息标识：temppoint1
						 * if(tp1.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp1.x+10,tp1.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp1.x+10,tp1.y+10);//点击勾选 }
						 * tp2=gdp(2,251,toppagey
						 * -134,283,toppagey);//读取语音消息标识：temppoint1
						 * if(tp2.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp2.x+10,tp2.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp2.x+10,tp2.y+10);//点击勾选 }
						 * tp3=gdp(2,107,toppagey
						 * -134,139,toppagey);//读取语音消息标识：temppoint1
						 * if(tp3.x>-1){ sendvideoi=sendvideoi+1;//遍历到的视频序号
						 * csn("LT",tp3.x+10,tp3.y+10);//点击勾选
						 * synchronized(this){wait(1000);}//暂停1000毫秒
						 * csn("LT",tp3.x+10,tp3.y+10);//点击勾选 }
						 * toppagey=toppagey-144; } }
						 * svbt.setText("v"+sendvideoi);//设置结束的时间
						 */
					}
					// sar("video"+iddf.format(new Date()));//开始录像
					// Thread.currentThread().sleep(5000);
					// sor();//停止录像
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
	 * 开始录像机：startRecording mr：mediarecorder
	 */

	public void sar(String savename) {
		mr = new MediaRecorder();// 创建mediarecorder对象
		// 加上如下代码解决视频浏览界面旋转90度的问题
		cam = Camera.open();
		cam.setDisplayOrientation(90);
		cam.unlock();
		mr.setCamera(cam);
		// 加上如下一句代码解决录制的视频旋转90度的问题
		mr.setOrientationHint(90);

		mr.setAudioSource(MediaRecorder.AudioSource.MIC); // 获得声音数据源
		// 设置录制视频源为Camera(相机)
		mr.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// mr.setCaptureRate(10.0);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		// mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 只能设置成AAC，否则在IOS（苹果）系统上无法播放
		// 设置录制的视频编码h263 h264
		// mr.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mr.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		mr.setVideoSize(176, 144);
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		mr.setVideoFrameRate(24);
		mr.setVideoEncodingBitRate(60 * 60);// 设置清晰度
		mr.setPreviewDisplay(vrv.getHolder().getSurface());// 显示出来
		// 设置视频文件输出的路径，同时给上次使用的录像路径赋值
		lvfn = cwxmp + "/" + savename + vex;
		// Toast.makeText(this,"开始录像，保存路径："+lvfn, 5000).show();
		mr.setOutputFile(lvfn);
		try {
			// 准备录制
			mr.prepare();
			// 开始录制
			mr.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iri = true;// 是否正在录像
	}

	/*
	 * 停止录像:stopRecording mr：mediarecorder
	 */

	public void sor() {
		if (mr != null) {
			Writelog("czlx");//写入日志
			// 停止
			mr.stop();
			mr.release();
			mr = null;
			// cam.stop();
			cam.release();
			cam = null;
			// Toast.makeText(this,"录完"+lvfn, 5000).show();
			File f = new File(lvfn);
			if (f.exists())
				// 通过广播将得到的文件加入媒体库
				Writelog("gblxks");//写入日志
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.parse(fmt + lvfn)));// mnt是引用时必须加入的，这个不在路径中，但必须有，否则广播失败
				Writelog("gblxtz");//写入日志
		}
	}

	/*
	 * 执行adb命令：execadb
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
	 * 按坐标点击屏幕：clickscreen
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
	 * 按坐标点击屏幕：longpressscreen
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
	 * 按起始和终到坐标移动屏幕：movescreen
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
//写入日志
	public void Writelog(String logtxt)
	{
		if(mlg.indexOf("rizhi")>-1){
			SimpleDateFormat iddf = new SimpleDateFormat("mmHHss");// 设置按日期生成文件名的格式
			logstr=logstr+iddf.format(new Date())+logtxt+"\n";
			//Toast.makeText(this, "写入："+logstr, 5000).show();
			svf(logstr,cwxp + "/runlog.txt");
		}
	}
	//读取文本文件中的内容
    public String ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
        String content = ""; //文件内容字符串
            //打开文件
            File file = new File(path);
            //如果path是传递过来的参数，可以做一个非目录的判断
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
                        //分行读取
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
	 * 读取文本文件中的内容并转为列表：ReadTxtFileToArrayList
	 */
	public ArrayList rtftal(String strFilePath) {
		ArrayList resultal = new ArrayList();
		String path = strFilePath;
		String content = ""; // 文件内容字符串
		// 打开文件
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (file.exists() && !file.isDirectory()) {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream, "GB2312");// 加上GB2312是为了有中文时不乱码
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
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
	 * 直接调用短信接口发短信：sendSMS
	 * 
	 * @param phoneNumber
	 * 
	 * @param message
	 */
	@SuppressLint("NewApi")
	public void ssm(String phoneNumber, String message) {
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		// 拆分短信内容（手机短信长度限制）
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(phoneNumber, null, text, null, null);
			/*
			 * smsManager.sendTextMessage(destinationAddress, scAddress, text,
			 * sentIntent, deliveryIntent)
			 * 
			 * -- destinationAddress：目标电话号码 -- scAddress：短信中心号码，测试可以不填 -- text:
			 * 短信内容 -- sentIntent：发送 -->中国移动 --> 中国移动发送失败 --> 返回发送成功或失败信号 -->
			 * 后续处理 即，这个意图包装了短信发送状态的信息 -- deliveryIntent： 发送 -->中国移动 -->
			 * 中国移动发送成功 --> 返回对方是否收到这个信息 --> 后续处理
			 * 即：这个意图包装了短信是否被对方收到的状态信息（供应商已经发送成功，但是对方没有收到）。
			 */
		}
	}

	/*
	 * 加密数字字符串转为图片：numberstrtoBitmap
	 */
	public Bitmap ntb(String bmpstr) {
		// 将字符串转换成Bitmap类型
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
					Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			/*
			 * Canvas c = new Canvas(returnmap); c.drawColor(Color.WHITE);
			 * c.save( Canvas.ALL_SAVE_FLAG );//保存 c.restore();
			 */
			// 依次循环，对图像的像素进行处理
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
	 * 加密数字字符串转为识别数组：numberstrtointay
	 */
	public int[][] nta(String bmpstr) {
		// 将字符串转换成Bitmap类型
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
	 * 图片转为识别数组：bitmaptointay
	 */
	public int[][] bta(Bitmap inbmp) {
		// 将字符串转换成Bitmap类型
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
	 * 　　* 将文本保存为文件：saveFile 　　
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
	 * decoderBase64File:(将base64字符解码保存文件). <br/>
	 * 
	 * @author guhaizhou@126.com
	 * @param base64Code
	 *            编码后的字串
	 * @param savePath
	 *            文件保存路径
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
	 * 将base64字符串转为图片：stringtoBitmap
	 */
	@SuppressLint("NewApi")
	public Bitmap stb(String string) {
		// 将字符串转换成Bitmap类型
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
	 * 字符串解密，使用base64加解密后前后各加一个字符：decodestring
	 */
	@SuppressLint("NewApi")
	public String dsr(String instr) {
		String returnstr = "";
		// 伪装开始
		String linshistr = "";
		for (int i = instr.length(); i > 0; i--) {
			linshistr = linshistr + instr.substring(i - 1, i);
		}
		returnstr = linshistr.substring(3);
		// 伪装结束
		instr = instr.substring(3, instr.length() - 5);
		// Toast.makeText(this, instr, 2000).show();

		// 伪装开始
		linshistr = "";
		for (int i = instr.length(); i > 0; i--) {
			linshistr = linshistr + instr.substring(i - 1, i);
		}
		returnstr = linshistr.substring(3);
		// 伪装结束
		returnstr = new String(Base64.decode(instr.getBytes(), Base64.DEFAULT));
		return returnstr;
	}

	/*
	 * 读取设备信息：readdevice
	 */
	public String rdv() {
		/*
		 * 调用Web Service之前你需要先弄清楚这4个的值分别是什么：命名空间、调用的方法名称、EndPoint和SOAP Action。
		 * 当在浏览器中访问WSDL时，很容易得知命名空间、调用的方法名称是什么（不明白的请看上篇文章），
		 * 至于EndPoint通常是将WSDL地址末尾的"?wsdl"去除后剩余的部分；而SOAP Action通常为命名空间 +
		 * 调用的方法名称。*
		 */
		// Toast.makeText(this, "进入获取设备信息接口1", 6000).show();
		String fanhuizhi = "";
		String shebeizhanshi = "";
		try {
			// 获取屏幕高度和宽度
			// Toast.makeText(this, "进入获取设备信息接口2", 6000).show();
			// 命名空间
			String nameSpace = dsr(rdns);// "http://wxyyzf/";
			// 调用的方法名称
			String methodName = dsr(rdmn);// "huoqushebeixinxi";
			// EndPoint
			String endPoint = dsr(rdep);
			// SOAP Action
			String soapAction = dsr(rdsa);// "http://wxyyzf/huoqushebeixinxi";
			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// 设置需调用WebService接口需要传入的参数arg0
			// Toast.makeText(this, "传入参数："+locations, 2000).show();
			rpc.addProperty("arg0", ei);
			rpc.addProperty("arg1", si);
			rpc.addProperty("arg2", swh + "," + sht + ",1," + sjxh + "---"
					+ sjpp + "---" + sdkbb + "---" + xtbb + "---" + mubb+ "---" + gwxv());
			rpc.addProperty("arg3", "1"); 
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService 用java的千万不要用上这句，否则参数传不了
			// envelope.dotNet = true;
			// 等价于envelope.bodyOut = rpc;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(endPoint, 50000);
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取返回的数据
			SoapObject objectbc = (SoapObject) envelope.bodyIn;
			// 获取返回的结果
			fanhuizhi = objectbc.getProperty(0).toString();
			dsb = fanhuizhi;// Toast.makeText(this, "接口返回值："+duqushebei,
							// 6000).show();
			// Toast.makeText(this, "获取的设备信息："+dsb, 6000).show();
			// if (dsb.equals("无记录")) {
			if (dsb.length() <= 6) {
				if (!dis) {
					// 设置短信已发送
					dis = true;
					// 发送短信
					// ssm("13799291380", imei + "," +
					// imsi+","+screenwidth+","+screenheight);
					// Toast.makeText(this, "发送短信："+imei+","+imsi, 2000).show();
					// 设置关闭软件为自身发送
					isf = true;
					// 关闭软件

				}
				// 先屏蔽判断注册
				/*
				 * } else if ((duqushebei.equals("注册到期")) ||
				 * (duqushebei.equals("没有注册或已到期")) ||
				 * (duqushebei.equals("连接接口失败"))) {
				 */

			} else {
				// Toast.makeText(this, "获取的设备信息："+duqushebei, 6000).show();
				// 读取设备
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
						.substring(0, shebeizhanshi.indexOf(",")));// =".mp4";//录像类型：videoext
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

			// 将WebService返回的结果显示在TextView中
			// resultView.setText(result);
		} catch (Exception e) {
			// e.printStackTrace();
			// return "连接接口失败";
			// return e.toString();
		}
		/*
		 * if (shoujihao.length() > 0) return "获取成功"; else return "获取失败";
		 */
		return fanhuizhi;
	}

	/*
	 * 读取模板信息：readmuban
	 */
	public String rmb() {
		/*
		 * 调用Web Service之前你需要先弄清楚这4个的值分别是什么：命名空间、调用的方法名称、EndPoint和SOAP Action。
		 * 当在浏览器中访问WSDL时，很容易得知命名空间、调用的方法名称是什么（不明白的请看上篇文章），
		 * 至于EndPoint通常是将WSDL地址末尾的"?wsdl"去除后剩余的部分；而SOAP Action通常为命名空间 +
		 * 调用的方法名称。*
		 */
		// Toast.makeText(this, "进入获取设备信息接口1", 6000).show();
		String fanhuizhi = "";
		String shebeizhanshi = "";
		try {
			// 获取屏幕高度和宽度
			// Toast.makeText(this, "进入获取设备信息接口2", 6000).show();
			// 命名空间
			String nameSpace = dsr(rmns);// "http://wxyyzf/";
			// 调用的方法名称
			String methodName = dsr(rmmn);// "huoqumuban";
			// EndPoint
			String endPoint = dsr(rmep);
			// SOAP Action
			String soapAction = dsr(rmsa);// "http://wxyyzf/huoqumuban";
			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// 设置需调用WebService接口需要传入的参数arg0
			// Toast.makeText(this, "传入参数："+locations, 2000).show();
			rpc.addProperty("arg0", ei);
			rpc.addProperty("arg1", si);
			rpc.addProperty("arg2", "1");
			rpc.addProperty("arg3", "1,"+logstr);
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService 用java的千万不要用上这句，否则参数传不了
			// envelope.dotNet = true;
			// 等价于envelope.bodyOut = rpc;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(endPoint, 50000);
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取返回的数据
			SoapObject objectbc = (SoapObject) envelope.bodyIn;
			// 获取返回的结果
			fanhuizhi = objectbc.getProperty(0).toString();
			// Toast.makeText(this, "获取的设备信息："+duqushebei, 6000).show();
			// if (fanhuizhi.equals("无记录")) {
			if (fanhuizhi.length() <= 6) {

				/*
				 * } else if ((fanhuizhi.equals("注册到期")) ||
				 * (fanhuizhi.equals("没有注册或已到期")) ||
				 * (fanhuizhi.equals("连接接口失败"))) {
				 */
			} else {
				// Toast.makeText(this, "获取的返回信息："+fanhuizhi, 6000).show();
				String[] fhary = fanhuizhi.split(";");
				for (String item : fhary) {
					String oneline = item;
					String dh = oneline.substring(0, oneline.indexOf(','));// 模板代号
					oneline = oneline.substring(oneline.indexOf(',') + 1);
					String cs = oneline.substring(0, oneline.indexOf(','));// 判断参数
					oneline = oneline.substring(oneline.indexOf(',') + 1);
					String ps = oneline.substring(0, oneline.indexOf(','));// 图片字符串
					String lx = oneline.substring(oneline.indexOf(',') + 1);// 模板类型
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

			// 将WebService返回的结果显示在TextView中
			// resultView.setText(result);
		} catch (Exception e) {
			// e.printStackTrace();
			// return "连接接口失败";
			// return e.toString();
		}
		/*
		 * if (jieduanbitmapay.size() > 0) return "获取成功"; else return "获取失败";
		 */
		return fanhuizhi;
	}

	/*
	 * 该函数实现根据阀值（灰度值（1-255））对图像进行二值化处理：twovaluebycolor inbmp：输入的图像
	 * limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public Bitmap tbc(Bitmap inbmp, int limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		// 创建二值化图像
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现根据阀值（灰度值（1-255））对图像进行二值化处理，转为整数数组：twovaluebycoloray inbmp：输入的图像
	 * limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public int[][] tbca(Bitmap inbmp, int limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int[][] returnmap = new int[width][height];
		// 创建二值化图像
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现从小到大根据阀值（指取的有效点占图像总面积的比例）对图像进行二值化处理：twovaluemintomax inbmp：输入的图像
	 * limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public Bitmap tmix(Bitmap inbmp, float limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		// 创建二值化图像
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);

		// 依次循环，对图像的像素进行遍历
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, j)) * 0.3
						+ Color.green(returnmap.getPixel(x, j)) * 0.59 + Color
						.blue(returnmap.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // 按临界值选取灰度阀值
		int i = 0;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i + 1;
		}
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现从小到大根据阀值（指取的有效点占图像总面积的比例）对图像进行二值化处理，转为整数数组：twovaluemintomaxay
	 * inbmp：输入的图像 limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public int[][] tmixa(Bitmap inbmp, float limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		int[][] returnmap = new int[width][height];
		// 创建二值化图像

		// 依次循环，对图像的像素进行遍历
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, j)) * 0.3
						+ Color.green(inbmp.getPixel(x, j)) * 0.59 + Color
						.blue(inbmp.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // 按临界值选取灰度阀值
		int i = 0;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i + 1;
		}
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现从大到小根据阀值（指取的有效点占图像总面积的比例）对图像进行二值化处理：twovaluemaxtomin inbmp：输入的图像
	 * limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public Bitmap tmxi(Bitmap inbmp, float limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		// 创建二值化图像
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);

		// 依次循环，对图像的像素进行遍历
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(returnmap.getPixel(x, j)) * 0.3
						+ Color.green(returnmap.getPixel(x, j)) * 0.59 + Color
						.blue(returnmap.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // 按临界值选取灰度阀值
		int i = 255;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i - 1;
		}
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现从大到小根据阀值（指取的有效点占图像总面积的比例）对图像进行二值化处理，转为整数数组：twovaluemaxtominay
	 * inbmp：输入的图像 limvalue：阀值，这里指灰度值（1-255） fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public int[][] tmxia(Bitmap inbmp, float limvalue, boolean fanse) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		int colorint[] = new int[255];
		int[][] returnmap = new int[width][height];
		// 创建二值化图像

		// 依次循环，对图像的像素进行遍历
		for (int x = 0; x < width; x++) {
			for (int j = 0; j < height; j++) {
				int gray = (int) (Color.red(inbmp.getPixel(x, j)) * 0.3
						+ Color.green(inbmp.getPixel(x, j)) * 0.59 + Color
						.blue(inbmp.getPixel(x, j)) * 0.11);
				colorint[gray] = colorint[gray];
			}
		}
		int totalpoint = width * height;
		int maxi = 0; // 按临界值选取灰度阀值
		int i = 254;
		while (maxi / totalpoint < limvalue) {
			maxi = maxi + colorint[i];
			i = i - 1;
		}
		// 依次循环，对图像的像素进行处理
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
	 * 该函数实现从图片取轮廓：getlkbmp inbmp：输入的图像 limvalue：阀值，这里指灰度值（1-255）
	 * fans：反色，指是否将处理后的图片进行黑白反色
	 */
	public Bitmap glk(Bitmap inbmp) {
		// 得到图形的宽度和长度
		int width = inbmp.getWidth();
		int height = inbmp.getHeight();
		String okpoint = "";
		// 创建返回图像
		Bitmap returnmap = null;
		returnmap = inbmp.copy(Config.ARGB_8888, true);
		// 从上到下依次循环取边缘点
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// 从下到上依次循环取边缘点
		for (int x = 0; x < width; x++) {
			for (int y = height - 1; y > -1; y--) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// 从左到右依次循环取边缘点
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// 从右到左依次循环取边缘点
		for (int y = 0; y < height; y++) {
			for (int x = width - 1; x > -1; x--) {
				if (returnmap.getPixel(x, y) != Color.WHITE) {
					okpoint = okpoint + x + "," + y + ";";
					break;
				}
			}
		}
		// Toast.makeText(this,"1" , 5000).show();
		// 依次循环，对图像的像素进行处理
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
	 * 判断小图片（smallbmp）是否包含在大图片（bigbmp）里，如果是则返回0：bmpisin
	 * 如果不是则返回bla*10000/tolb取整的值
	 */
	public int bsi(Bitmap smallbmp, Bitmap bigbmp) {
		float tolb = 0;// 小图片范围内大图片黑色的总像素数
		float tolw = 0;// 小图片范围内大图片白色的总像素数
		float bla = 0;// 小图片范围内大图片和小图片同时为黑色的总像素数
		float whi = 0;// 小图片范围内大图片和小图片同时为白色的总像素数
		float maxflo = 0;// bla/tolb的值
		int bigbmpwidth = bigbmp.getWidth();// 大图片宽度
		int bigbmpheight = bigbmp.getHeight();// 大图片高度
		int smallbmpwidth = smallbmp.getWidth();// 小图片宽度
		int smallbmpheight = smallbmp.getHeight();// 小图片高度减1
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// 大图片和小图片的宽度差
		int bigsmallHeight = bigbmpheight - smallbmpheight;// 大图片和小图片的高度差
		int backresult = -1;
		for (int x = 0; x < bigsmallwidth; x++) {
			for (int y = 0; y < bigsmallHeight; y++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int xi = 0; xi < smallbmpwidth; xi++) { // 取灰度分布
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
	 * 判断小数组（smallay）是否包含在大数组（bigay）里，如果是则返回0：bmpisinay
	 * 如果不是则返回bla*10000/tolb取整的值
	 */
	public int bsia(int[][] smallbmp, int[][] bigbmp, double lim) {
		float tolb = 0;// 小图片范围内大图片黑色的总像素数
		float tolw = 0;// 小图片范围内大图片白色的总像素数
		float bla = 0;// 小图片范围内大图片和小图片同时为黑色的总像素数
		float whi = 0;// 小图片范围内大图片和小图片同时为白色的总像素数
		float maxflo = 0;// bla/tolb的值
		int bigbmpwidth = bigbmp.length;// 大图片宽度
		int bigbmpheight = bigbmp[0].length;// 大图片高度
		int smallbmpwidth = smallbmp.length;// 小图片宽度
		int smallbmpheight = smallbmp[0].length;// 小图片高度减1
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// 大图片和小图片的宽度差
		int bigsmallHeight = bigbmpheight - smallbmpheight;// 大图片和小图片的高度差
		int backresult = -1;
		for (int y = 0; y < bigsmallHeight; y++) {
			for (int x = 0; x < bigsmallwidth; x++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int yi = 0; yi < smallbmpheight; yi++) {
					for (int xi = 0; xi < smallbmpwidth; xi++) { // 取灰度分布
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
	 * 获取小图片（smallbmp）在大图片（bigbmp）的坐标：getbmpinxy 如果不是则返回坐标（-1,-1）
	 */
	public Point gbp(Bitmap smallbmp, Bitmap bigbmp) {
		float tolb = 0;// 小图片范围内大图片黑色的总像素数
		float tolw = 0;// 小图片范围内大图片白色的总像素数
		float bla = 0;// 小图片范围内大图片和小图片同时为黑色的总像素数
		float whi = 0;// 小图片范围内大图片和小图片同时为白色的总像素数
		float maxflo = 0;// bla/tolb的值
		int bigbmpwidth = bigbmp.getWidth();// 大图片宽度
		int bigbmpheigth = bigbmp.getHeight();// 大图片高度
		int smallbmpwidth = smallbmp.getWidth();// 小图片宽度
		int smallbmpheigth = smallbmp.getHeight();// 小图片高度减1
		int smallbmpwidthj1 = smallbmpwidth - 1;// 小图片宽度减1
		int smallbmpheigthj1 = smallbmpheigth - 1;// 小图片高度
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// 大图片和小图片的宽度差
		int bigsmallHeight = bigbmpheigth - smallbmpheigth;// 大图片和小图片的高度差
		Point backresult = null;
		for (int x = 0; x < bigsmallwidth; x++) {
			for (int y = 0; y < bigsmallHeight; y++) {
				tolb = 0;
				tolw = 0;
				bla = 0;
				whi = 0;
				for (int xi = 0; xi < smallbmpwidthj1; xi++) { // 取灰度分布
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
	 * 获取小图片（smallbmp）在大图片（bigbmp）的坐标：getbmpinxyay 如果不是则返回坐标（-1,-1）
	 */
	public Point gbpa(int[][] smallbmp, int[][] bigbmp, double lim) {
		float tolb = 0;// 小图片范围内大图片黑色的总像素数
		float tolw = 0;// 小图片范围内大图片白色的总像素数
		float bla = 0;// 小图片范围内大图片和小图片同时为黑色的总像素数
		float whi = 0;// 小图片范围内大图片和小图片同时为白色的总像素数
		float outlim = (float) (1.00 - lim);// 小于临界值
		float maxflo = 0;// bla/tolb的值
		int bigbmpwidth = bigbmp.length;// 大图片宽度
		int bigbmpheigth = bigbmp[0].length;// 大图片高度
		int smallbmpwidth = smallbmp.length;// 小图片宽度
		int smallbmpheigth = smallbmp[0].length;// 小图片高度减1
		int smallbmpwidthj1 = smallbmpwidth - 1;// 小图片宽度减1
		int smallbmpheigthj1 = smallbmpheigth - 1;// 小图片高度
		int bigsmallwidth = bigbmpwidth - smallbmpwidth;// 大图片和小图片的宽度差
		int bigsmallHeight = bigbmpheigth - smallbmpheigth;// 大图片和小图片的高度差
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
					for (int xi = 0; xi < smallbmpwidthj1; xi++) { // 取灰度分布
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
		 * "000", 1).show(); Looper.loop();// 进入loop中的循环，查看消息队列 }
		 */
		/*
		 * if (maxflo>0){ backresult.x=-1; backresult.y=-1; }
		 */
		return backresult;
	}

	/*
	 * 两个图片是否相似 lim为相似极限, 达到即认为一样则返回0,否则返回相似度：bmpissimilar
	 */
	public int bsr(Bitmap bmp1, Bitmap bmp2, float lim) {
		float tol1 = 0;// 图片1的黑色的总像素
		float bla1 = 0;// 图片1和图片2相同位置的黑色的总像素
		float tol2 = 0;// 图片2的黑色的总像素
		float bla2 = 0;// 图片2和图片1相同位置的黑色的总像素
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
	 * 两个数组是否相似 lim为相似极限, 达到即认为一样则返回0,否则返回相似度：ayissimilar
	 */
	public int asr(int[][] bmp1, int[][] bmp2, double lim) {
		float tol1 = 0;// 图片1的黑色的总像素
		float bla1 = 0;// 图片1和图片2相同位置的黑色的总像素
		float tol2 = 0;// 图片2的黑色的总像素
		float bla2 = 0;// 图片2和图片1相同位置的黑色的总像素
		float tol = 0;
		float bla = 0;
		float maxflo = 0;
		float minflo = 0;
		int result = -1;
		//Writelog("bdtpck:b1c"+bmp1.length+"；b2c"+bmp2.length+"；b1k"+bmp1[0].length+"；b2k"+bmp2[0].length);//写入日志：记录比对图片长度宽度
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
		Writelog("bla1："+bla1+"；tol1："+tol1+"bla2："+bla2+"；tol2："+tol2);//写入日志：记录比对图片的总点数
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
	 * 取字符串的一部分转换成整形 instr:输入的字符串 f:字符串取值的起点 t:字符串取值的终点
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
	 * 取字符串的一部分转换成整形并取绝对值 instr:输入的字符串 f:字符串取值的起点 t:字符串取值的终点
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
	 * 判断当前所处阶段:getstep pdfw:判断范围 wucha:误差，即取特征图片允许的误差（上下前后左右）范围
	 */
	public String gsp(String pdfw) {
		Bitmap windowbmp = tss();// 截取当前屏幕
		String result = "";
		int maxint = 0;
		int nowint = -1;
		int windowwidth = windowbmp.getWidth();
		int windowheight = windowbmp.getHeight();
		for (int i = 0; i < jba.size(); i++) {
			tm tempmb = (tm) jba.get(i);
			String tmpmingchen = tempmb.dh;// 阶段名称
			String pdstr = tempmb.cs;// 用于判断坐标，获取二值化参数等的字串
			int[][] mbay = tempmb.pa;// 模板数组
			int wbmph1 = gifs(pdstr, 2, 6);// 输入的窗口文件的水平方向第一个参数
			int wbmph2 = gifs(pdstr, 10, 14);// 输入的窗口文件的水平方向第二个参数
			int wbmpv1 = gifs(pdstr, 6, 10);// 输入的窗口文件的垂直方向第一个参数
			int wbmpv2 = gifs(pdstr, 14, 18);// 输入的窗口文件的垂直方向第二个参数
			String zuobiaolx = pdstr.substring(0, 2);// 坐标类型
			String erzhihualx = pdstr.substring(18, 21);// 二值化类型
			int erzhihuasz = gifs(pdstr, 21, 24);// 二值化数值
			boolean fanse = pdstr.substring(24, 26).equals("YS");// 二值化时是否反色
			float lim = (float) gifs(pdstr, 26, 29);// 临界值
			// Toast.makeText(this,"1" , 5000).show();
			// result=result+pdstr.substring(0, 2)+";"+pdstr.substring(2,
			// 6)+";"+pdstr.substring(6, 10)+";"+pdstr.substring(10, 14)+
			// ";"+pdstr.substring(14, 18)+";"+pdstr.substring(18,
			// 21)+";"+pdstr.substring(21, 24)+";"+pdstr.substring(24)+"\n";
			if ((pdfw == null)
					|| (pdfw.length() == 0)
					|| ((pdfw.length() > 0) && (pdfw.indexOf(tmpmingchen) > -1))) {
				boolean iscon = (Math.abs(wbmph1 - wbmph2) <= windowwidth)
						&& (Math.abs(wbmpv1 - wbmpv2) <= windowheight); // 判断高度宽度有没有超出边界
				// iscon=true;
				if (iscon) {
					int tol = 0;
					int bla = 0;
					Bitmap capbmp = null;// 用于判断的实时截图
					int[][] capay = null;// 用于判断的实时截图转换成的数组
					// 根据类型获取坐标取宽为多少高为多少的图片
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
					 * if(tmpmingchen.equals("通讯录选项卡-焦点")){
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
					// Toast.makeText(this,"二值化："+nowint , 5000).show();
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
	 * 判断特征图是否在范围内，一般用于定位特征图:getdwpoint pdfw:判断范围 wucha:误差，即取特征图片允许的误差（上下前后左右）范围
	 */
	public Point gdp(int dingweibitmapayi, int left, int top, int right,
			int bottom) {
		Bitmap windowbmp = tss();// 截取当前屏幕
		Point result = new Point();
		result.x = -1;
		result.y = -1;
		int maxint = 0;
		int nowint = -1;
		int windowwidth = windowbmp.getWidth();
		int windowheight = windowbmp.getHeight();
		// Toast.makeText(this,dingweibitmapayi+"；"+dingweibitmapay.size(),
		// 5000).show();
		if (dingweibitmapayi < dba.size()) {
			// Toast.makeText(this,dingweibitmapayi+"；"+dingweibitmapay.size(),
			// 5000).show();
			tm tempmb = (tm) dba.get(dingweibitmapayi);
			String tmpmingchen = tempmb.dh;// 阶段名称
			String pdstr = tempmb.cs;// 用于判断坐标，获取二值化参数等的字串
			int[][] mbay = tempmb.pa;// 模板数组
			int wbmph1 = gifs(pdstr, 2, 6);// 输入的窗口文件的水平方向第一个参数
			if (left > 0)
				wbmph1 = left;
			int wbmph2 = gifs(pdstr, 10, 14);// 输入的窗口文件的水平方向第二个参数
			if (right > 0)
				wbmph2 = right;
			int wbmpv1 = gifs(pdstr, 6, 10);// 输入的窗口文件的垂直方向第一个参数
			if (top > 0)
				wbmpv1 = top;
			int wbmpv2 = gifs(pdstr, 14, 18);// 输入的窗口文件的垂直方向第二个参数
			if (bottom > 0)
				wbmpv2 = bottom;
			String zuobiaolx = pdstr.substring(0, 2);// 坐标类型
			String erzhihualx = pdstr.substring(18, 21);// 二值化类型
			int erzhihuasz = gifs(pdstr, 21, 24);// 二值化数值
			boolean fanse = pdstr.substring(24, 26).equals("YS");// 二值化时是否反色
			float lim = (float) gifs(pdstr, 26, 29);// 临界值
			// Toast.makeText(this,"1" , 5000).show();
			// result=result+pdstr.substring(0, 2)+";"+pdstr.substring(2,
			// 6)+";"+pdstr.substring(6, 10)+";"+pdstr.substring(10, 14)+
			// ";"+pdstr.substring(14, 18)+";"+pdstr.substring(18,
			// 21)+";"+pdstr.substring(21, 24)+";"+pdstr.substring(24)+"\n";
			boolean iscon = (Math.abs(wbmph1 - wbmph2) <= windowwidth)
					&& (Math.abs(wbmpv1 - wbmpv2) <= windowheight); // 判断高度宽度有没有超出边界
			// iscon=true;
			if (iscon) {
				int tol = 0;
				int bla = 0;
				Bitmap capbmp = null;// 用于判断的实时截图
				int[][] capay = null;// 用于判断的实时截图转换成的数组
				// 根据类型获取坐标取宽为多少高为多少的图片
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
				 * if(tmpmingchen.equals("通讯录选项卡-焦点")){
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
				// Toast.makeText(this,"定位", 5000).show();
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
	 * 判断当前界面是否变化:isscreenchange pdfw:判断范围 wucha:误差，即取特征图片允许的误差（上下前后左右）范围
	 * getx:取得用于判断的图片的x坐标 gety:取得用于判断的图片的y坐标 getwidth:取得用于判断的图片的宽度
	 * getheight:取得用于判断的图片的高度
	 */
	public boolean isc(int erzhihuasz, boolean fanse, float lim, int getx,
			int gety, int getwidth, int getheight) {
		Bitmap windowbmp = null;// 截取当前屏幕
		windowbmp = tss();// 截取当前屏幕
		windowbmp = Bitmap.createBitmap(windowbmp, getx, gety, getwidth,
				getheight);
		int nowint = -1;
		boolean resultbol = false;
		int[][] nowscay = tbca(windowbmp, erzhihuasz, fanse);// 此时的屏幕图形数组：nowscreenay
		if (lsay == null)
			lsay = nowscay;// 最后一次的屏幕图形数组：lastscreenay
		nowint = asr(lsay, nowscay, lim / 100);
		//Writelog("xsd："+nowint);//写入日志：相似度
		// mFloatView.setText("二值化："+nowint);
		lsay = nowscay;// 最后一次的屏幕图形数组：lastscreenay
		// Toast.makeText(this,"二值化："+nowint , 5000).show();
		if (nowint == 0)
			resultbol = true;
		else
			resultbol = false;
		// toasthandler.sendEmptyMessage(1);
		return resultbol;
	}

	/*
	 * 对屏幕进行截图，并以图像的形式返回：takeScreenShot
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
			// 判断图片文件是否已经生成
			File inibitmap = new File(mpSavedPath);
			if (inibitmap.exists()) {
				// 固定高宽缩放图片
				BitmapFactory.Options options = new BitmapFactory.Options();

				options.inJustDecodeBounds = false;

				Bitmap mBitmap = BitmapFactory.decodeFile(mpSavedPath, options);
				// Bitmap mBitmap =
				// BitmapFactory.decodeFile(sdcardpath+"/2016-01-06_21-59-14-screenshot.png",
				// options);
				// mBitmap = twovaluebycolor(mBitmap,229,true);
				// 记得把assets目录下的图片拷贝到SD卡中
				// 原始文件按png标准重新编码后保存起来
				/*
				 * File chongxinf = new File(tct, sdf.format(new Date())+
				 * "-scs.png"); FileOutputStream outcx = new
				 * FileOutputStream(chongxinf);
				 * //resizeBitmap.compress(Bitmap.CompressFormat.PNG, 90,
				 * outtz); mBitmap.compress(Bitmap.CompressFormat.PNG, 90,
				 * outcx); outcx.flush(); outcx.close();
				 */
				// 由于设置inJustDecodeBounds为true，因此执行下面代码后bitmap为空
				// toasthandler.sendEmptyMessage(1);
				int bmpWidth = mBitmap.getWidth();

				int bmpHeight = mBitmap.getHeight();
				// toasthandler.sendEmptyMessage(1);
				// 缩放图片的尺寸

				float scaleWidth = (float) sswh / bmpWidth; // 按固定大小缩放 sWidth
															// 写多大就多大

				float scaleHeight = (float) ssht / bmpHeight; //

				Matrix matrix = new Matrix();

				matrix.postScale(scaleWidth, scaleHeight);// 产生缩放后的Bitmap对象
				// toasthandler.sendEmptyMessage(1);
				// Bitmap resizeBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
				// bmpWidth, bmpHeight, matrix, true);
				// resizeBitmap = twovaluebycolor(resizeBitmap,229,true);//
				// Toast.makeText(this,getstep(resizeBitmap,"微信选项卡-焦点,通讯录选项卡-焦点,发现选项卡-焦点,我选项卡-焦点,微信选项卡-焦点-有消息,微信选项卡-有消息",0),
				// 5000).show();
				// Toast.makeText(this,getstep(resizeBitmap,"",0), 5000).show();
				// getstep(resizeBitmap,"",0);
				returnbitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth,
						bmpHeight, matrix, true);
				// toasthandler.sendEmptyMessage(1);
				// 如果命令包含值为：jietu时截图
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
				// 以(100, 20)为坐标截取宽200高300的图片
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
				inibitmap.delete();// 删除原始图片
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
			// Toast.makeText(this,"截图完成", 5000).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnbitmap;
	}

	// 定时任务执行动作：dotimerwork
	public void dtw() {
		try {
			// int audiomode=audioManager.getMode();
			// Toast.makeText(this,"音频模式："+audiomode, 5000).show();
			// isc(230,false,90);
			/*
			 * if(isc(230,false,90)) mFloatView.setText("一样");
			 * //Toast.makeText(this,"一样", 5000).show(); else
			 * mFloatView.setText("变了");
			 */
			// SimpleDateFormat idd = new SimpleDateFormat("mmss");//获取结束时间的格式

			String stepstr = gsp("");
			if (stepstr.indexOf("qltjmscan1") > -1) {
				// Toast.makeText(this,"音频模式：", 5000).show();
				// wmp.gravity = Gravity.RIGHT | Gravity.TOP;
				// wmp.x = 300;
			}
			stv.setText(stepstr);
			/*
			 * if(!dig){ dig=true;
			 * if((ceshi)||((mlg!=null)&&(mlg.length()>0)&&(mlg
			 * .equals("ceshi")))) Toast.makeText(this,gsp(""), 5000).show();
			 * 
			 * dmti=true;//进入处理微信消息的线程
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

	// 定时器的回调函数
	private Handler handler = new Handler() {
		// 更新的操作
		@Override
		public void handleMessage(Message msg) {
			dtw();// 执行计时任务
			// Looper.prepare();
			// Toast.makeText(MainActivity.this, "消息1",
			// Toast.LENGTH_SHORT).show();
			// Looper.loop();// 进入loop中的循环，查看消息队列
			super.handleMessage(msg);
		}
	};

	/*
	 * 开始计时器任务：starttimer
	 */
	public void stt() {
		// 开启定时器 初始化 time 对象 和 timetask 对象
		if (tm1 == null)
			tm1 = new Timer();
		tm1t = new TimerTask() {
			// 定时器线程方法
			@Override
			public void run() {
				handler.sendEmptyMessage(1); // 发送消息
			}
		};
		// Timer1.schedule(Timer1Task, 1000, 1000);//每1000毫秒执行一次计时任务
		tm1.schedule(tm1t, tm1s, tm1s);// 每1000毫秒执行一次计时任务
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 建立应用保持通知栏
		/*
		 * Intent notificationIntent = new Intent(this, MainActivity.class);
		 * PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
		 * notificationIntent, 0); Notification noti = new
		 * Notification.Builder(this) .setContentTitle("微信语音转发应用保持窗口")
		 * .setContentText("请不要关闭此窗口，谢谢") .setSmallIcon(R.drawable.ic_launcher)
		 * .setContentIntent(pendingIntent) .build(); startForeground(12346,
		 * noti);
		 */

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Notification notification = new
		// Notification(R.drawable.logo_icon_16,"移动营销",
		// System.currentTimeMillis());
		Notification notification = new Notification(R.drawable.ic_launcher,
				"微信语音转发", System.currentTimeMillis());
		Intent stayintent = new Intent(Intent.ACTION_MAIN);
		stayintent.addCategory(Intent.CATEGORY_LAUNCHER);
		stayintent.setClass(this, MainActivity.class);
		stayintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		notification.flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag
		PendingIntent contextIntent = PendingIntent.getActivity(this, 0,
				stayintent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "微信语音转发",
				"请不要关闭此窗口，谢谢", contextIntent);
		// notificationManager.notify(R.drawable.ic_launcher, notification);
		startForeground(12346, notification);

		flags = START_STICKY;// START_STICKY（或START_STICKY_COMPATIBILITY）是service被kill掉后自动重写创建
		return super.onStartCommand(intent, flags, startId);
		// return START_REDELIVER_INTENT;
		// return Service.START_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		if (!isf) {// 如果不是自己关闭的
			// 释放应用保持通知栏

			stopForeground(true);
			Intent localIntent = new Intent();
			localIntent.setClass(this, WxMainService.class); // 销毁时重新启动Service
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
