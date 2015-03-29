package cn.edu.sdu.online.util;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageService {
	public static byte[] getImage(String path) throws Exception{ 
        URL url = new URL(path); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
        //设置超时时间 
        conn.setConnectTimeout(5000); 
        conn.setRequestMethod("GET"); 
        if(conn.getResponseCode()==200){ 
            InputStream inStream = conn.getInputStream(); 
            byte[] data = StreamTool.readInputStream(inStream); 
            return data; 
        } 
        return null; 
    } 
}
