package com.lyrica.clockmod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class ClockEventHandler {

	// ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆
	//                                                               時計
	// ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆ - + - ☆

	private timeNamePair[] specialTimeArray;

	public ClockEventHandler(){
		setSpecialTimeArray();
	}

	// ツールチップの表示
	@SubscribeEvent
	public void ItemTooltipEvent(ItemTooltipEvent event) {
		EntityPlayer player = event.entityPlayer;
		// 時計がインベントリに入っている時のみ
		Item item = event.itemStack.getItem();
		if (item != Items.clock)
			return;
		if (!player.inventory.hasItem(Items.clock))
			return;

		// ツールチップに時刻を追加
		event.toolTip.add("･*:.｡. ☆ " + getTime(true) + " ☆ .｡.:*･゜");
	}

	// クリックしたときのメッセージ
	@SubscribeEvent
	public void useClockEvent(PlayerInteractEvent event) {
		// サーバーのみの処理
		if (event.world.isRemote) return;

		EntityPlayer player = event.entityPlayer;
		ItemStack items = player.inventory.mainInventory[player.inventory.currentItem];
		AlarmData data = ClockMod.alarmData;
		if (items == null) return;

		// 持っているものが時計の時
		if (items.getItem() != Items.clock) return;

		// ブロックに対してシフト左クリックした時
		if (event.action == Action.LEFT_CLICK_BLOCK && player.isSneaking()) {
			chat(player, "アラームを解除するね。");
			data.alarmFlag = false;
		}

		// 右クリックしたとき。AIR はブロックをクリックしたときにも呼ばれる
		if (event.action == Action.RIGHT_CLICK_AIR) {
			long worldTime = event.world.getWorldTime();
			if (!player.isSneaking())
			{
				// スニークしていない時
				int time = (int)(worldTime % 24000);
				chat(player, getTime(false) + "だよ！");
				// アラームがセットされている時
				if (data.alarmFlag) {
					int targetTime = specialTimeArray[data.alarmNum].time - time;
					if (targetTime < 0) targetTime += 24000;
					chat(player, specialTimeArray[data.alarmNum].name + "まであと" + getTimeFromTick(targetTime) + "くらいだよ。");
				}
			}else{
				// スニークしている時
				nextTime();
				chat(player, specialTimeArray[data.alarmNum].name + "になったら知らせるね♪");
			}
		}
	}

	// 毎tick読み込んでアラームをチェックする
	@SubscribeEvent
	public void worldTimeEvent(LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			World world = player.worldObj;
			AlarmData data = ClockMod.alarmData;
			if (world == null) return;
			long time = world.getWorldTime();

			// アラームがセットされている時
			if (data.prevTime >= 0 && data.alarmFlag) {
				long day = data.prevTime / 24000 * 24000;
				int alarmTime = specialTimeArray[data.alarmNum].time;

				// 前回アラームを鳴らした日付と違っていれば
				if (day + alarmTime != data.prevAlarmTime)
				{
					// アラームを鳴らす時間なら
					boolean flag1 = data.prevTime - day < alarmTime && alarmTime <= time - day;
					boolean flag2 = data.prevTime - day < alarmTime + 24000 && alarmTime + 24000 <= time - day;
					if (flag1 || flag2)
					{
						// インベントリに時計が入っているときのみメッセージを表示する
						if (player.inventory.hasItem(Items.clock))
						{
							chat(player, specialTimeArray[data.alarmNum].name + "になったよ！ " + specialTimeArray[data.alarmNum].additionalSentence);
							// サウンドを鳴らす
							player.worldObj.playSoundAtEntity(player, "random.levelup", 1.0f, 1.0F);
							data.prevAlarmTime = day + alarmTime;
						}
						// アラームを解除する
						//data.alarmFlag = false;
					}
				}
			}

			data.prevTime = time;
		}
	}

	// アラームをセット
	private void nextTime()
	{
		World world = Minecraft.getMinecraft().theWorld;
		boolean rainFlag = world.isRaining();
		AlarmData data = ClockMod.alarmData;

		// すでにアラームがセットされている時
		if (data.alarmFlag) {
			switch (data.alarmNum){
			case 0:
				data.alarmNum = rainFlag ? 1 : 2;
				break;
			case 1:
			case 2:
				data.alarmNum = rainFlag ? 4 : 3;
				break;
			case 3:
			case 4:
				data.alarmNum = 0;
				break;
			}
		} else {
			// アラームがセットされていない時
			// 一番近い時間にアラームをセットする
			int time = (int)(world.getWorldTime() % 24000);
			data.alarmNum = 0;
			int cnt = 0;
			for (timeNamePair pair : specialTimeArray) {
				if (time < pair.time) {
					data.alarmNum = cnt;
					break;
				}
				++cnt;
			}
		}

		// アラームをセットする
		data.alarmFlag = true;
	}

	// アラームの時間と名前を設定
	private void setSpecialTimeArray() {
		specialTimeArray = new timeNamePair[5];
		specialTimeArray[0] = new timeNamePair(12517, "ベッドで眠れる時間", "早く寝よー(*´～｀*)｡o○ﾑﾆｬﾑﾆｬ");
		specialTimeArray[1] = new timeNamePair(12969, "モンスターが出てくる時間", "気をつけて！");
		specialTimeArray[2] = new timeNamePair(13183, "モンスターが出てくる時間", "気をつけて！");
		specialTimeArray[3] = new timeNamePair(22800, "モンスターが出てこなくなる時間", "怖かった～>_<");
		specialTimeArray[4] = new timeNamePair(23017, "モンスターが出てこなくなる時間", "怖かった～>_<");
	}

	// 時刻を取得
	protected String getTime(boolean b) {
		Minecraft mc = Minecraft.getMinecraft();
		long time = mc.theWorld.getWorldTime() + 6000;
		int d = (int) (time / 24000) % 30 + 1;
		int m = (int) (time / 24000) / 30 % 12 + 1;
		int t = (int) (time % 24000);
		int hou = t / 1000;
		int min = ((t % 1000) * 60 / 1000) / 15 * 15;
		String s = hou < 4 ? "よなかの" : hou < 10 ? "あさの" : hou < 16 ? "おひるの"
				: hou < 18 ? "ゆうがたの" : hou < 23 ? "よるの" : "よなかの";
		if (b)
		{
			if (min == 0) return "" + m + "月" + d + "日の" + hou + "時";
			return "" + m + "月" + d + "日の" + hou + "時" + min + "分";
		} else {
			if (hou > 12)
				hou -= 12;
			if (min == 0) return s + hou + "時";
			return s + hou + "時" + min + "分";
		}
	}

	// Tickから時間を取得(絶対値)
	private String getTimeFromTick(int tick) {
		tick = Math.abs(tick);
		String re = "";
		int hou = tick / 1000;
		int min = ((tick % 1000) * 60 / 1000) / 15 * 15;
		if (hou != 0) re += hou + "時間";
		if (min != 0) re += min + "分";
		if (hou == 0 && min == 0) re += (tick % 1000) * 60 / 1000 + "分";
		return re;
	}

	// チャットを表示
	private static void chat(EntityPlayer player, String text)
	{
		player.addChatComponentMessage(new ChatComponentText(text));
	}

	private class timeNamePair{
		public final int time;
		public final String  name;
		public final String additionalSentence;

		public timeNamePair(int time, String name, String additionalSentence) {
			this.time = time;
			this.name = name;
			this.additionalSentence = additionalSentence;
		}
	}

}
