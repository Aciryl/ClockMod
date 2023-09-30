package com.lyrica.clockmod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

public class SaveAndLoadDataEventHandler {
	// ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆
	//                                                              データ
	// ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆

	FileManager file;

	public SaveAndLoadDataEventHandler(FileManager file) {
		this.file = file;
	}

	// データのロード
	@SubscribeEvent
	public void loadDataEvent(Load event) {
		// ファイルの読み込み
		file.updateSaveFileName();
		file.loadFile();

		// データのロード
		ClockMod.alarmData.loadData();
	}

	// データのセーブ
	@SubscribeEvent
	public void saveDataEvent(Save event) {
		// データのセーブ
		ClockMod.alarmData.saveData();

		// ファイルの書き込み
		file.saveFile();
	}
}
