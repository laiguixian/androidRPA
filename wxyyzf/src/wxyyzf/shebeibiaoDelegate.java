package wxyyzf;

import tdrtool.databasebean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import javax.annotation.Resource;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@javax.jws.WebService(targetNamespace = "http://wxyyzf/", serviceName = "shebeibiaoService", portName = "shebeibiaoPort", wsdlLocation = "WEB-INF/wsdl/shebeibiaoService.wsdl")
@javax.jws.soap.SOAPBinding(style = javax.jws.soap.SOAPBinding.Style.RPC)
public class shebeibiaoDelegate {

	wxyyzf.shebeibiao shebeibiao = new wxyyzf.shebeibiao();
	@Resource
	private WebServiceContext wsContext;
	public String huoqushebeixinxi(String imei, String imsi, String shebeiinfo,
			String shujubanben) {
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest)(mc.get(MessageContext.SERVLET_REQUEST));
		String remortAddress = request.getHeader("User-Agent");
		String userinfo=request.getRemoteAddr()+"---"+
				request.getRemoteHost()+"---"+
				request.getRemotePort()+"---"+
				request.getRemoteUser();
		//System.out.println("客户端："+remortAddress);
		if(remortAddress.equals("ksoap2-android/2.6.0+"))//如果是手机的Webservice则返回正确值，否则返回空值，防止被利用
			return shebeibiao.huoqushebeixinxi(imei, imsi, shebeiinfo, shujubanben,userinfo);
		else
			return "";
	}

}