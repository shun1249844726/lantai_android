package com.lexinsmart.util;

import static com.lexinsmart.util.Constants.ONEROADDTL;
import static com.lexinsmart.util.Constants.ROADS;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import com.lexinsmart.planmanager.ImportFile;

import android.content.Context;

/**
 * ���ڶ�ȡini�����ļ�
 * 
 * @author USER
 * 
 */
public class IniReader {

	// ���ڴ�������ļ�������ֵ
	protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
	private transient String currtionSecion;
	private transient Properties current;

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param filename
	 *            �ļ���
	 * @throws IOException
	 */
	public IniReader(String name,String dd, Context context) throws IOException {


	//	 InputStream in = context.getAssets().open(name);
		FileInputStream fin = context.openFileInput(new File("xushun.txt")
				.toString());
		InputStream in = getInputStream(fin);

		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		BufferedReader read = null;
		try {
			if (reader != null) {
				read = new BufferedReader(reader);
				reader(read);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FileNotFoundException("�ļ������ڻ����ļ���ȡʧ��");
		}
	}

	/**
	 * ����ÿ�ζ�ȡ�ļ�һ��
	 * 
	 * @param reader
	 *            �ļ���
	 * @throws IOException
	 */
	private void reader(BufferedReader reader) throws IOException {
		// TODO Auto-generated method stub
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				parseLine(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("�ļ����ݶ�ȡʧ��");
		}
	}

	/**
	 * ��ȡini�ļ�������ֵ
	 * 
	 * @param line
	 *            ini�ļ�ÿ������
	 */
	private void parseLine(String line) {
		// TODO Auto-generated method stub
		try {
			if (line != null) {
				line = line.trim();
				if (line.matches("\\[.*\\]")) {
					currtionSecion = line.replaceFirst("\\[(.*)\\]", "$1");
					current = new Properties();
					sections.put(currtionSecion, current);
				} else if (line.matches(".*=.*")) {
					if (current != null) {
						int i = line.indexOf('=');
						String name = line.substring(0, i - 1);
						String value = line.substring(i + 2);
						current.setProperty(name, value);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ���ڻ�ȡ����ֵ��ֵ
	 * 
	 * @param section
	 *            �������Ե�ֵ
	 * @param name
	 *            ����ֵ����
	 * @return ����ֵ��ֵ
	 */
	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			return null;
		}
		String value = p.getProperty(name);
		return value;
	}

	public String[][] getAllValue(int length) {
		String[][] allValue = new String[length][9];
		for (int i = 0; i < allValue.length; i++) {
			for (int j = 0; j < allValue[0].length; j++) {
				allValue[i][j] = getValue(ROADS[i], ONEROADDTL[j]);
			}
		}
		return allValue;
	}

	public InputStream getInputStream(FileInputStream fileInput) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 50];
		int n = -1;
		InputStream inputStream = null;
		try {
			while ((n = fileInput.read(buffer)) != -1) {
				baos.write(buffer, 0, n);
			}
			byte[] byteArray = baos.toByteArray();
			inputStream = new ByteArrayInputStream(byteArray);
			return inputStream;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
