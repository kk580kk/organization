package com.jivesoftware.spark.organization;

import java.io.*;
import java.util.*;

public class ConfigurationUtil {
	private Properties config = new Properties();// 记录配置项
	private String fn = null;// 记录配置文件名
	private Set<Object> keys;

	// 此构造方法用于新建配置文件
	public ConfigurationUtil() {
	}

	// 从指定文件名读入配置信息
	public ConfigurationUtil(String fileName) throws ConfigurationException {
		try {
			FileInputStream fin = new FileInputStream(fileName);
			// FileInputStream fin = (FileInputStream)
			// this.getClass().getResourceAsStream("/"+fileName);
			config.load(fin); // 载入文件
			fin.close();
		} catch (IOException ex) {
			throw new ConfigurationException("无法读取指定的配置文件:" + fileName);
		}
		fn = fileName;
		keys = new HashSet<Object>();
	}

	// 从指定文件读取Key的集合
	public Set<Object> keySet() {
		keys = config.keySet();
		return keys;
	}

	// 指定配置项名称，返回配置值
	public String getValue(String itemName) {
		return config.getProperty(itemName);
	}

	// 指定配置项名称，删除配置值
	public String removeValue(String itemName) {
		return (String)config.remove(itemName);
	}
	
	// 指定配置项名称和默认值，返回配置值
	public String getValue(String itemName, String defaultValue) {
		return config.getProperty(itemName, defaultValue);
	}

	// 设置配置项名称及其值
	public void setValue(String itemName, String value) {
		try{
		//value = new String(value.getBytes("ISO-8859-1"),"gbk");
		config.setProperty(itemName, value);
		}catch (Exception ex) {ex.printStackTrace();}
		return;
	}

	// 保存配置文件，指定文件名和抬头描述
	public void saveFile(String fileName, String description)
			throws ConfigurationException {
		try {
			File f = new File(fileName);
			FileOutputStream fos = new FileOutputStream(fileName);
			config.store(fos, description);// 保存文件
			fos.close();
		} catch (IOException ex) {
			throw new ConfigurationException("无法保存指定的配置文件:" + fileName);
		}
	}

	// 保存配置文件，指定文件名
	public void saveFile(String fileName) throws ConfigurationException {
		saveFile(fileName, "");
	}

	// 保存配置文件，采用原文件名
	public void saveFile() throws ConfigurationException {
		if (fn.length() == 0)
			throw new ConfigurationException("需指定保存的配置文件名");
		saveFile(fn);
	}
}
