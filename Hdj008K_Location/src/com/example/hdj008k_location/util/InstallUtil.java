package com.example.hdj008k_location.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.example.hdj008k_location.obj.Folder;
import com.example.hdj008k_location.obj.MyFileInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

/**
 * ��װ��ж��
 * */
public class InstallUtil {
	
	/**
     * ����Ƿ����rootȨ��
     * 
     * @param cmd
     * @return true�ɹ���falseʧ��
     */ 
	public static boolean haveRoot(String cmd) { 
        int i = execRootCmdSilent(cmd); 
        Log.e("InstallUtil--haveRoot", "����ֵ:"+i);
        if (i == 1) { 
            return false; 
        } else if(i == 0){//0
        	return true;
        }
        return false; 
    } 
   
	/**
	 * ִ�о�Ĭ��װ
	 * 
	 * @param paramString shell����
	 * @return 0������root�ˣ������Ķ���ʧ��
	 */
    public static int execRootCmdSilent(String paramString) { 
        int result = -1;  
        try { 
            Process localProcess = Runtime.getRuntime().exec("su"); 
            OutputStream os = localProcess.getOutputStream(); 
            DataOutputStream dos = new DataOutputStream(os); 
            dos.writeBytes(paramString + "\n"); 
            dos.flush(); 
            dos.writeBytes("exit\n"); 
            dos.flush(); 
            localProcess.waitFor(); 
            result = localProcess.exitValue(); 
            
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return result; 
    } 
    
    /**
     * ���Ӧ������
     * 
     * @param paramString shell����
     * @return true�ɹ��ˣ�falseʧ��
     */ 
    public static boolean clearAppData(String paramString) {
		try {
				Process c = Runtime.getRuntime().exec("su");
				DataOutputStream b = new DataOutputStream(c.getOutputStream());
				InputStream d = c.getInputStream();
			b.writeBytes(paramString + "\n");
			b.flush();
			return true;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return false;
	}
    
//
//	public static boolean uninstall(String packageName,Context context){
//        
//            // ��rootȨ�ޣ����þ�Ĭж��ʵ��
//            return clientUninstall(packageName);
//        
//    }
	 /**
     * ��Ĭж��
     * 
     * @param packageNameӦ�ð���
     * 
     * @return true�ǳɹ�ж�أ�falseʧ��
     */
	public static boolean clientUninstall(String packageName){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value); 
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
    
    
    public static boolean returnResult(int value){
        // ����ɹ�  
        if (value == 0) {
            return true;
        } else if (value == 1) { // ʧ��
            return false;
        } else { // δ֪���
            return false;
        }  
    }
    
    /**
	 * ��ȡҪж�ص�app����
	 * */
    public static String getUninstallAPKInfo(Context ctx, String archiveFilePath) {

		String pakName = null;
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pakinfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (pakinfo != null) {
			ApplicationInfo appinfo = pakinfo.applicationInfo;

			pakName = appinfo.packageName;

		}
		return pakName;
	}
    
    
    /**�ж�apk�Ƿ���԰�װ*/
    public static boolean isApkCanInstall(Context context,String mSaveFileName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(mSaveFileName, PackageManager.GET_ACTIVITIES);
			if (info != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
    
    /**
     * ����ж��Ӧ��
     * */
    public static void UninstallAppArray(List<MyFileInfo> list){

    	if(list==null)
    		return;
    	
    	for(int i = 0;i<list.size();i++){
    		MyFileInfo f = list.get(i);
    		String name = f.getFileName();
    		
    		if(clientUninstall(name))
    			Log.e("InstallUtil--UninstallAppArray", name+": ж�سɹ�");
    		
    	}
    }
    
    /**
     * �����ļ����µĿɰ�װapk��������app�����б�
     * */
	public static List<MyFileInfo> checkFolder(Context context,File file) {
		
		List<MyFileInfo> list= new ArrayList<MyFileInfo>();
		
		if (file.isFile()) {
			String name = getUninstallAPKInfo(context,file.getAbsolutePath());
			if(name!=null){
				
				for(int i = 0;i<list.size();i++){
					if(list.get(i).getFileName().equals(name)){
						return list;
					}
				}
				
				MyFileInfo f = new MyFileInfo();
				f.setFileName(name);
				f.setFilePath(file.getAbsolutePath());
				list.add(f);
			}
		}else if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles != null && childFiles.length > 0) {
				for (int i = 0; i < childFiles.length; i++) {
					list.addAll(checkFolder(context,childFiles[i]));
				}
			}
		
		}
		return list;
		
	}
//	/**
//	 * ɾ��Ŀ¼�µ������ļ�����ж����Ŀ¼��apk�İ�����ͬ��Ӧ��
//	 * */
//	public static void UninstallAppArray(Context context,String path){
//		
//		if(path==null || path.length()==0)
//			return;
//		
//		File file = new File(path);
//		checkFolderAndUninstall(context,file);
//		
//	}
//	
//	/**
//	 * ������ж��
//	 * */
//	public static void checkFolderAndUninstall(Context context,File file) {
//		
//		if (file.isFile()) {
//			String name = getUninstallAPKInfo(context,file.getAbsolutePath());
//			if(name!=null){
//				if(clientUninstall(name))
//					Log.e("InstallUtil--checkFolderAndUninstall", name+": ж�سɹ�");
//			}
//		}else if (file.isDirectory()) {
//			File[] childFiles = file.listFiles();
//			if (childFiles != null && childFiles.length > 0) {
//				for (int i = 0; i < childFiles.length; i++) {
//					checkFolderAndUninstall(context,childFiles[i]);
//				}
//			}
//		}
//		
//	}
	
}
