package com.lyrica.clockmod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ClockMod.MODID, version = ClockMod.VERSION)
public class ClockMod
{
    public static final String MODID = "clockmod";
    public static final String VERSION = "1.0.0";

    private static final FileManager fileManager = new FileManager();
	public static final AlarmData alarmData = new AlarmData(fileManager);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	ClockEventHandler clockEventHandler = new ClockEventHandler();
    	SaveAndLoadDataEventHandler sandlEventHandler = new SaveAndLoadDataEventHandler(fileManager);

    	MinecraftForge.EVENT_BUS.register(clockEventHandler);
    	MinecraftForge.EVENT_BUS.register(sandlEventHandler);
    }
}
