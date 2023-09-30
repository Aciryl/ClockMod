package com.lyrica.clockmod;


public class AlarmData {
	// ファイル
	private final FileManager file;

	// 新しい Data か区別するためのID
	private int loadID = 0;

	// 時計
	public boolean alarmFlag;
	public int alarmNum;
	public long prevTime;
	public long prevAlarmTime;

	// コンストラクタ
	public AlarmData(FileManager file) {
		this.file = file;
	}

	// ロードID
	public int getLoadID() {
		return loadID;
	}

	// データ
	private boolean getDefaultAlarmFlag() {
		return false;
	}

	private int getDefaultAlarmNum() {
		return 0;
	}

	private long getDefaultPrevTime() {
		return -1;
	}

	private long getDefaultPrevAlarmDay() {
		return -1;
	}

	// データのロード＆セーブ
	public void loadData() {
		loadData(true);
	}

	public void loadData(boolean flag) {
		// ロードIDを変える
		++loadID;

		alarmFlag =     getData("Clock Alarm Flag",          getDefaultAlarmFlag());
		alarmNum =      getData("Clock Alarm Number",        getDefaultAlarmNum());
		prevTime =      getData("Clock Previous Time",       getDefaultPrevTime());
		prevAlarmTime = getData("Clock Previous Alarm Time", getDefaultPrevAlarmDay());
	}

	public void saveData() {
		// データ
		setData("Clock Alarm Flag", alarmFlag);
		setData("Clock Alarm Number", alarmNum);
		setData("Clock Previous Time", prevTime);
		setData("Clock Previous Alarm Time", prevAlarmTime);
	}

	private void removeData(String key) {
		file.delete(key);
	}

	private int getData(String key, int defaultValue) {
		return file.hasKey(key) ? file.getIntData(key) : defaultValue;
	}

	private long getData(String key, long defaultValue) {
		return file.hasKey(key) ? file.getLongData(key) : defaultValue;
	}

	private boolean getData(String key, boolean defaultValue) {
		return file.hasKey(key) ? file.getBooleanData(key) : defaultValue;
	}

	private void setData(String key, Object data) {
		if (data instanceof Integer)
			file.setIntData(key, (Integer)data);
		else if (data instanceof Long)
			file.setLongData(key, (Long)data);
		else if (data instanceof Boolean)
			file.setBooleanData(key, (Boolean)data);
	}
}
