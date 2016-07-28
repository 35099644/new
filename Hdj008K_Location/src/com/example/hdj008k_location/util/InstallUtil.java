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
 * 安装和卸载
 * */
public class InstallUtil {
	
	/**
     * 检测是否具体root权限
     * 
     * @param cmd
     * @return true成功，false失败
     */ 
	public static boolean haveRoot(String cmd) { 
        int i = execRootCmdSilent(cmd); 
        Log.e("InstallUtil--haveRoot", "返回值:"+i);
        if (i == 1) { 
            return false; 
        } else if(i == 0){//0
        	return true;
        }
        return false; 
    } 
   
	/**
	 * 执行静默安装
	 * 
	 * @param paramString shell命令
	 * @return 0是正常root了，其他的都是失败
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
     * 清除应用数据
     * 
     * @param paramString shell命令
     * @return true成功了，false失败
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
//            // 有root权限，利用静默卸载实现
//            return clientUninstall(packageName);
//        
//    }
	 /**
     * 静默卸载
     * 
     * @param packageName应用包名
     * 
     * @return true是成功卸载，false失败
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
        // 代表成功  
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }  
    }
    
    /**
	 * 获取要卸载的app包名
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
    
    
    /**判断apk是否可以安装*/
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
     * 批量卸载应用
     * */
    public static void UninstallAppArray(List<MyFileInfo> list){

    	if(list==null)
    		return;
    	
    	for(int i = 0;i<list.size();i++){
    		MyFileInfo f = list.get(i);
    		String name = f.getFileName();
    		
    		if(clientUninstall(name))
    			Log.e("InstallUtil--UninstallAppArray", name+": 卸载成功");
    		
    	}
    }
    
    /**
     * 搜索文件夹下的可安装apk，并返回app包名列表
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
//	 * 删除目录下的所有文件，并卸载与目录下apk的包名相同的应用
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
//	 * 搜索并卸载
//	 * */
//	public static void checkFolderAndUninstall(Context context,File file) {
//		
//		if (file.isFile()) {
//			String name = getUninstallAPKInfo(context,file.getAbsolutePath());
//			if(name!=null){
//				if(clientUninstall(name))
//					Log.e("InstallUtil--checkFolderAndUninstall", name+": 卸载成功");
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
