create database wxyyzf;--΢������ת��
go
use wxyyzf;
go
--�����豸�������豸�ļ�¼���ж��Ƿ�����Ч��
create table shebeibiao(
zhujian int IDENTITY(1,1) primary key ,--����
leixing varchar(30) default 'imei',--�豸���ͣ�imsi��imei
imsi varchar(100),--�ƶ��豸���������(��sim��Ψһ��Ӧ)
imei varchar(100),--�����ƶ��û�ʶ����(IMEI�����ֻ�����)
shoujihao varchar(30),--�ֻ���
shibiejiange int default 5,--ʶ���� ��λ����
screenwidth  float,--�豸��Ļ���
screenheight  float,--�豸��Ļ�߶�
fmtstr varchar(50) default 'FL=ZmlsZTovL21udA==T=Z=g',--¼������ʱ��Ҫ�����ļ�ɨ��Ĺ㲥�����㲥�Ĳ�����Ҫ�ԡ�file://mnt����ͷ��Ĭ��Ϊ��file://mnt���ļ��ܴ���FL=ZmlsZTovL21udA==T=Z=g
videotype varchar(50) default 'CI=Lm1wNA==P=U=d',--¼�����ͣ���mp4,3gp�ȣ�Ĭ��Ϊ��.mp4���ļ��ܴ���
sdcardpath varchar(200) default 'v4=L3NkY2FyZC8=BD=JP',--�洢��·����Ĭ��Ϊ��/sdcard/���ļ��ܴ���v4=L3NkY2FyZC8=BD=JP
tdrcwx varchar(200) default '=U=L3NkY2FyZC90ZHJjd3g=bd=j=',--��Ӧ�õ�·����Ĭ��Ϊ��/sdcard/tdrcwx���ļ��ܴ���=U=L3NkY2FyZC90ZHJjd3g=bd=j=
tdrcwxtemp varchar(200) default 'AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW',--Ӧ�õ���ʱ·����Ĭ��Ϊ��/sdcard/tdrcwx/tdrcwxtemp���ļ��ܴ���AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW
tdrcwxmov varchar(200) default 'O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm',--¼��Ŀ¼��Ĭ��Ϊ��/sdcard/tdrcwx/tdrcwxmov���ļ��ܴ���O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm
mingling varchar(100),--Ҫִ�е�����
beizhu varchar(1000),--��ע
lastlogin datetime default getdate(),--�����¼ʱ��
daoqishijian datetime default getdate()+100,--����ʱ��
xinzengshijian datetime default getdate()--����ʱ��
)
--����ģ��������豸ʶ��΢�Ž���
create table mubanbiao(
zhujian int IDENTITY(1,1) primary key ,--����
weixinbb int,--΢�Ű汾-�ڲ�ʶ��
indexnum int,--���
mingchen varchar(100),--ģ�����ƣ������Լ��Ķ�
daihao varchar(100),--ģ����ţ�Ϊģ�����Ƶ�ƴ����д������ʶ��
canshu varchar(50),--ʶ�����
picstr text,--ͼƬ�ַ���
leixing varchar(10),--ģ�����ͣ��磺"�׶�"ֱָ�������жϽ׶Σ�����λ��ָ���ڶ�λ����ͼ��������
isactive bit default 1,--�Ƿ���Ч
xinzengshijian datetime default getdate()--����ʱ��
)
--����΢�Ű汾��Ϣ�����ڼ�¼΢�Ű汾��Ϣ
create table wxbbbiao(
zhujian int IDENTITY(1,1) primary key ,--����
weixinbb int,--΢�Ű汾-�ڲ�ʶ��
weixinver varchar(20),--΢�ŵİ汾��
bi int,--��ɫ�����������������ɫ�����ڽ�ͼƬת������
wi int,--��ɫ�����������������ɫ�����ڽ�ͼƬת������
bstr varchar(10),--��ɫ�ַ���Ϊ�����֣������ڸ��ַ�����Χ�ڵ����ִ����ɫ����֮�����ڷ�Χ�ڵ���Ϊ��ɫ
beizhu varchar(500),--��ע
xinzengshijian datetime default getdate()--����ʱ��
)
--�����豸��½�����ڼ�¼�豸��½��Ϣ
create table sbdlbiao(
zhujian int IDENTITY(1,1) primary key ,--����
imsi varchar(100),--�ƶ��豸���������(��sim��Ψһ��Ӧ)
imei varchar(100),--�����ƶ��û�ʶ����(IMEI�����ֻ�����)
duqushebei varchar(100),--��ȡ�豸״̬
duqumuban varchar(100),--��ȡ�豸״̬
beizhu varchar(7000),--��ע
denglushijian datetime default getdate()--��½ʱ��
)
--������־�����ڼ�¼�豸������Ϣ
create table logbiao(
zhujian int IDENTITY(1,1) primary key ,--����
imsi varchar(100),--�ƶ��豸���������(��sim��Ψһ��Ӧ)
imei varchar(100),--�����ƶ��û�ʶ����(IMEI�����ֻ�����)
logstr varchar(7000),--��־
addshijian datetime default getdate()--����ʱ��
)