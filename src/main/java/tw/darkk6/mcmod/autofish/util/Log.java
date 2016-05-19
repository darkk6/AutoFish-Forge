package tw.darkk6.mcmod.autofish.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.darkk6.mcmod.autofish.config.Reference;

public class Log {
	public static Logger log=null;
	
	private static void initLogger(){
		if(log==null) log=LogManager.getLogger(Reference.LOG_TAG);
	}
	
	public static Logger info(String str){
		initLogger();
		log.info(str);
		return log;
	}
	
	public static void showDesktopNotifyUnsupport(){
		TextComponentString txt = new TextComponentString(TextFormatting.RED + "[AutoFish] ");
		txt.appendText(TextFormatting.GOLD+Lang.get("autofish.notiy.unavailable"));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(txt);
	}
	
	//當主因輛關閉時所要呈現的警告訊息
	public static void warnMasterSoundMute(){
		TextComponentString txt = new TextComponentString(TextFormatting.RED + "[AutoFish] ");
		txt.appendText(TextFormatting.GOLD+Lang.get("autofish.mastersound.mute.warn"));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(txt);
	}
	
	//自動釣魚開始或結束時要顯示的文字訊息
	public static void showFishingMessage(boolean isStop){
		TextComponentString txt = new TextComponentString(TextFormatting.GOLD + "[AutoFish] ");
		if(isStop){
			txt.appendText(TextFormatting.RESET+Lang.get("autofish.fishing.stop.info"));
		}else{
			txt.appendText(TextFormatting.GREEN+Lang.get("autofish.fishing.start.info"));
		}
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(txt);
	}
}
