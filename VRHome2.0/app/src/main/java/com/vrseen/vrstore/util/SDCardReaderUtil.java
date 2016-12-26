package com.vrseen.vrstore.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SDCardReaderUtil {
	private static String mPath = "";
	private static String TAG = SDCardReaderUtil.class.getName();
	private static File mFileRoot = null;
	
	
	public static String CheckSDCardPath()
	{
		//检测SD卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
        	 List<String> list = GetExtSDCardPath();
        	 if(list.size() > 0)
        	 {
        		 mPath = GetExtSDCardPath().get(0);
        	 }
        	 else
        	 {
        		 mPath = GetInnerSDCardPath();
        	 }
        	     
        	
        }
        else{
           Log.e(TAG,"该设备没有SD卡！");
        }

        return mPath;
	}
	
	 /** 
     * 获取内置SD卡路径 
     * @return 
     */ 
    public static String GetInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }  
   
    /** 
     * 获取外置SD卡路径 
     * @return  应该就一条记录或空 
     * 只有一个SD卡的手机只会返回一个路径，多个可用存储位置的会返回多个路径。
     * 但有一点，是必须的，paths.get(0)肯定是外置SD卡的位置，因为它是primary external storage.
     */ 
    public static List<String> GetExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }  
    
    public static void GetAllFiles(File root, Map<String, String[]> contentList){
    	
        File files[] = root.listFiles();
        if(files != null){  
            for (File f : files){
                if(f.isDirectory()){  
                	try {
                		contentList.put(f.getName(), f.list());
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
//                    GetAllFiles(f,contentList);  
//                }else{  
//                	System.out.println(f.getName());
//                }  
            }  
        }  
    }  
    
//	public static void GetAllFiles(File root,List<String> contentList){  
//	    	
//	    if(root.isDirectory())
//	    {
//	    	for(int i=0;i<root.list().length;i++)
//	    	{
//	    		contentList.add(i, root.list()[i]);
//	    	}
//	    }
//	}  
	
	public static void GetAllFiles(File root, List<String> contentList){
    	
	    if(root.isDirectory())
	    {
	    	int len = root.listFiles().length;
	    	for(int i=0;i<len;i++)
	    	{
	    		File file = root.listFiles()[i];
	    		if(file.isFile())
	    		{
	    			try {
	    				contentList.add(file.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			
	    		}
	    	}
	    }
	}  
}
