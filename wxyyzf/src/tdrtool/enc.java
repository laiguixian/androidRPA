package tdrtool;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class enc {
public  static String encc(String originstr){//ecodeByMD5
originstr="adf"+originstr+"369";
String result = null;
char hexDigits[] = {//�������ֽ�ת���� 16 ���Ʊ�ʾ���ַ�
'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd','e', 'f'};
if(originstr != null){
try {
//����ʵ��ָ��ժҪ�㷨�� MessageDigest ����
MessageDigest md = MessageDigest.getInstance("MD5");
//ʹ��utf-8���뽫originstr�ַ������벢���浽source�ֽ�����
byte[] source = originstr.getBytes("utf-8");
//ʹ��ָ���� byte �������ժҪ
md.update(source);
//ͨ��ִ���������֮������ղ�����ɹ�ϣ���㣬�����һ��128λ�ĳ�����
byte[] tmp = md.digest();
//��16��������ʾ��Ҫ32λ
char[] str = new char[32];
for(int i=0,j=0; i < 16; i++){
//j��ʾת������ж�Ӧ���ַ�λ��
//�ӵ�һ���ֽڿ�ʼ���� MD5 ��ÿһ���ֽ�
//ת���� 16 �����ַ�
byte b = tmp[i];
//ȡ�ֽ��и� 4 λ������ת��
//�޷������������>>> ������������߲�0
//0x�������������ʮ�����Ƶ�����. fת����ʮ���ƾ���15
str[j++] = hexDigits[b>>>4 & 0xf];
// ȡ�ֽ��е� 4 λ������ת��
str[j++] = hexDigits[b&0xf];
}
result = new String(str);//���ת�����ַ������ڷ���
} catch (NoSuchAlgorithmException e) {
//�������ض��ļ����㷨�����ڸû����в�����ʱ�׳����쳣
e.printStackTrace();
} catch (UnsupportedEncodingException e) {
//��֧���ַ������쳣
e.printStackTrace();
}
}
return result;
}
//ʹ��MD5�������
public boolean checkPWD(String inputPWD, String sqlPWD){
if(encc(inputPWD).equals(encc(sqlPWD))){
return true;
}
return false;
}
}