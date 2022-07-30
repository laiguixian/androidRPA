package com.tdr.controlweixin;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaScanner {


private MediaScannerConnection mediaScanConn = null;


private MusicSannerClient client = null;


private String filePath = null;



private String fileType = null;



private String[] filePaths = null;
/**
 * Ȼ�����MediaScanner.scanFile("/sdcard/2.mp3");
 * */

public MediaScanner(Context context) {
//����MusicSannerClient


if (client == null) {




client = new MusicSannerClient();


}



if (mediaScanConn == null) {




mediaScanConn = new MediaScannerConnection(context, client);


}

}


class MusicSannerClient implements



MediaScannerConnection.MediaScannerConnectionClient {



public void onMediaScannerConnected() {







if(filePath != null){









mediaScanConn.scanFile(filePath, fileType);



}







if(filePaths != null){









for(String file: filePaths){











mediaScanConn.scanFile(file, fileType);




}



}







filePath = null;







fileType = null;







filePaths = null;


}



public void onScanCompleted(String path, Uri uri) {



// TODO Auto-generated method stub



mediaScanConn.disconnect();


}


}


/**
 * ɨ���ļ���ǩ��Ϣ
 * @param filePath �ļ�·�� eg:/sdcard/MediaPlayer/dahai.mp3
 * @param fileType �ļ����� eg: audio/mp3 media/* application/ogg
 * */

public void scanFile(String filepath,String fileType) {



this.filePath = filepath;





this.fileType = fileType;
//����֮�����MusicSannerClient��onMediaScannerConnected()����


mediaScanConn.connect();

}
/**
 * @param filePaths �ļ�·��
 * @param fileType �ļ�����
 * */

public void scanFile(String[] filePaths,String fileType){





this.filePaths = filePaths;





this.fileType = fileType;





mediaScanConn.connect();




}



public String getFilePath() {



return filePath;

}


public void setFilePath(String filePath) {



this.filePath = filePath;

}


public String getFileType() {





return fileType;

}


public void setFileType(String fileType) {





this.fileType = fileType;

}



}