package wxyyzf;

import java.sql.ResultSet;
import java.util.Date;

import tdrtool.databasebean;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@javax.jws.WebService(targetNamespace = "http://wxyyzf/", serviceName = "mubanbiaoService", portName = "mubanbiaoPort", wsdlLocation = "WEB-INF/wsdl/mubanbiaoService.wsdl")
@javax.jws.soap.SOAPBinding(style = javax.jws.soap.SOAPBinding.Style.RPC)
public class mubanbiaoDelegate {

	wxyyzf.mubanbiao mubanbiao = new wxyyzf.mubanbiao();
	@Resource
	private WebServiceContext wsContext;
	public String huoqumuban(String imei, String imsi, int weixinbanben,
			String somevalue) {
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest)(mc.get(MessageContext.SERVLET_REQUEST));
		String remortAddress = request.getHeader("User-Agent");
		//System.out.println("客户端："+remortAddress);
		if(remortAddress.equals("ksoap2-android/2.6.0+"))//如果是手机的Webservice则返回正确值，否则返回空值，防止被利用
			return mubanbiao.huoqumuban(imei, imsi, weixinbanben, somevalue);
		else
			return "";
	}

}