package com.lyrica.clockmod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

import net.minecraftforge.common.DimensionManager;

public class FileManager {
	public File file;
	private final String filename = "ClockMod.dat";
	private TreeMap<String, String> data;
	private String saveRootDirectory;

	public FileManager() {
		//readData();
		//writeData();
	}

	public boolean hasKey(String key) {
		return data.containsKey(key);
	}

	public void delete(String key) {
		data.remove(key);
	}

	// ------------------ Getter ------------------

	public String getStringData(String key) {
		return data.get(key);
	}

	public int getIntData(String key) {
		return Integer.valueOf(data.get(key));
	}

	public long getLongData(String key) {
		return Long.valueOf(data.get(key));
	}

	public float getFloatData(String key) {
		return Float.valueOf(data.get(key));
	}

	public double getDoubleData(String key) {
		return Double.valueOf(data.get(key));
	}

	public boolean getBooleanData(String key) {
		return Boolean.valueOf(data.get(key));
	}

	// ------------------ Setter ------------------

	public void setStringData(String key, String value) {
		data.put(key, value);
	}

	public void setIntData(String key, int value) {
		data.put(key, String.valueOf(value));
	}

	public void setLongData(String key, long value) {
		data.put(key, String.valueOf(value));
	}

	public void setFloatData(String key, float value) {
		data.put(key, String.valueOf(value));
	}

	public void setDoubleData(String key, double value) {
		data.put(key, String.valueOf(value));
	}

	public void setBooleanData(String key, boolean value) {
		data.put(key, String.valueOf(value));
	}

	// --------------------------------------------

	public void updateSaveFileName() {
		String newDirectory = "saves\\" + DimensionManager.getCurrentSaveRootDirectory().getName() + "\\";
		// 同じ名前なら何もしない
		if (saveRootDirectory != null && saveRootDirectory.equals(newDirectory)) return;

		saveRootDirectory = newDirectory;
		// 新しい File のインスタンスを作る
		file = new File(saveRootDirectory + filename);
		// ファイルがないときは作成する
		createNewFile();
		// データをリセットする
		data = null;
	}

	public boolean isDataLoaded() {
		return data != null;
	}

	public void loadFile() {
		if (file == null || isDataLoaded()) return;

		// データの初期化
		data = new TreeMap<String, String>();

		// ファイルの読み込み
		try {
			// FileReaderクラスのオブジェクトを生成する
			FileReader filereader = new FileReader(file);
			BufferedReader br = new BufferedReader(filereader);

			// ファイルを一行ずつ読み込む
			String line;
			while ((line = br.readLine()) != null) {
				String[] pair = line.split("=");
				// a = b の形のもののみ読み込む
				if (pair.length == 2) {
					// 前後のタブとスペースは消す
					for (int i = 0; i < 2; ++i) {
						pair[i] = pair[i].replaceAll("^[ 　\t]+", "");
						pair[i] = pair[i].replaceAll("[ 　\t]+$", "");
					}
					// データを代入
					data.put(pair[0], pair[1]);
				}
			}

			// ファイルクローズ
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveFile() {
		if (file == null) return;

		try {
			// FileWriterクラスのオブジェクトを生成する
			FileWriter filewriter = new FileWriter(saveRootDirectory + filename);
			// PrintWriterクラスのオブジェクトを生成する
			PrintWriter pw = new PrintWriter(new BufferedWriter(filewriter));

			// ファイルに書き込む
			for (String key : data.keySet())
				pw.println(key + " = " + data.get(key));

			// ファイルを閉じる
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createNewFile() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
