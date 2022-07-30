create database wxyyzf;--微信语音转发
go
use wxyyzf;
go
--创建设备表，用于设备的记录和判定是否在有效期
create table shebeibiao(
zhujian int IDENTITY(1,1) primary key ,--主键
leixing varchar(30) default 'imei',--设备类型：imsi或imei
imsi varchar(100),--移动设备国际身份码(跟sim卡唯一对应)
imei varchar(100),--国际移动用户识别码(IMEI，即手机串号)
shoujihao varchar(30),--手机号
shibiejiange int default 5,--识别间隔 单位：秒
screenwidth  float,--设备屏幕宽度
screenheight  float,--设备屏幕高度
fmtstr varchar(50) default 'FL=ZmlsZTovL21udA==T=Z=g',--录像生成时需要发送文件扫描的广播，而广播的参数需要以“file://mnt”开头，默认为“file://mnt”的加密串：FL=ZmlsZTovL21udA==T=Z=g
videotype varchar(50) default 'CI=Lm1wNA==P=U=d',--录像类型，如mp4,3gp等，默认为“.mp4”的加密串：
sdcardpath varchar(200) default 'v4=L3NkY2FyZC8=BD=JP',--存储卡路径，默认为“/sdcard/”的加密串：v4=L3NkY2FyZC8=BD=JP
tdrcwx varchar(200) default '=U=L3NkY2FyZC90ZHJjd3g=bd=j=',--本应用的路径，默认为“/sdcard/tdrcwx”的加密串：=U=L3NkY2FyZC90ZHJjd3g=bd=j=
tdrcwxtemp varchar(200) default 'AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW',--应用的临时路径，默认为“/sdcard/tdrcwx/tdrcwxtemp”的加密串：AGKL3NkY2FyZC90ZHJjd3gvdGRyY3d4dGVtcA===QQSW
tdrcwxmov varchar(200) default 'O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm',--录像目录，默认为“/sdcard/tdrcwx/tdrcwxmov”的加密串：O==L3NkY2FyZC90ZHJjd3gvdGRyY3d4bW92cd=jm
mingling varchar(100),--要执行的命令
beizhu varchar(1000),--备注
lastlogin datetime default getdate(),--最近登录时间
daoqishijian datetime default getdate()+100,--到期时间
xinzengshijian datetime default getdate()--新增时间
)
--创建模板表，用于设备识别微信界面
create table mubanbiao(
zhujian int IDENTITY(1,1) primary key ,--主键
weixinbb int,--微信版本-内部识别
indexnum int,--序号
mingchen varchar(100),--模板名称，仅供自己阅读
daihao varchar(100),--模板代号，为模板名称的拼音缩写，用于识别
canshu varchar(50),--识别参数
picstr text,--图片字符串
leixing varchar(10),--模板类型，如："阶段"指直接用于判断阶段；“定位”指用于定位特征图所在坐标
isactive bit default 1,--是否有效
xinzengshijian datetime default getdate()--新增时间
)
--创建微信版本信息表，用于记录微信版本信息
create table wxbbbiao(
zhujian int IDENTITY(1,1) primary key ,--主键
weixinbb int,--微信版本-内部识别
weixinver varchar(20),--微信的版本号
bi int,--黑色所用整型数，代表黑色，用于将图片转成数组
wi int,--白色所用整型数，代表白色，用于将图片转成数组
bstr varchar(10),--黑色字符，为纯数字，代表在该字符串范围内的数字代表黑色，反之，不在范围内的则为白色
beizhu varchar(500),--备注
xinzengshijian datetime default getdate()--新增时间
)
--创建设备登陆表，用于记录设备登陆信息
create table sbdlbiao(
zhujian int IDENTITY(1,1) primary key ,--主键
imsi varchar(100),--移动设备国际身份码(跟sim卡唯一对应)
imei varchar(100),--国际移动用户识别码(IMEI，即手机串号)
duqushebei varchar(100),--读取设备状态
duqumuban varchar(100),--读取设备状态
beizhu varchar(7000),--备注
denglushijian datetime default getdate()--登陆时间
)
--创建日志表，用于记录设备运行信息
create table logbiao(
zhujian int IDENTITY(1,1) primary key ,--主键
imsi varchar(100),--移动设备国际身份码(跟sim卡唯一对应)
imei varchar(100),--国际移动用户识别码(IMEI，即手机串号)
logstr varchar(7000),--日志
addshijian datetime default getdate()--增加时间
)