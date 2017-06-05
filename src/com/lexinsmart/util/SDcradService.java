package com.lexinsmart.util;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by xushun on 16/8/2.
 */

public class SDcradService {
    private Context context;

    public SDcradService(Context context) {
        this.context = context;
    }

    /**
     * 以私有文件保存内容
     *
     * @param filename
     *            文件名称
     * @param content
     *            文件内容
     * @throws Exception
     */
    public void saveToSDCard(String filename, String content) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(),
                filename);
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(content.getBytes());
        outStream.close();
    }

    /**
     * 读取文件内容
     *
     * @param filename
     *            文件名称
     * @return
     * @throws Exception
     */
    public String readFile(String filename) throws Exception {
        FileInputStream inStream = context.openFileInput(filename);
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();// 得到文件的二进制数据
        outStream.close();
        inStream.close();
        return new String(data);
    }
}
