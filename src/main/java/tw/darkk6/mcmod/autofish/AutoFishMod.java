package tw.darkk6.mcmod.autofish;

import java.awt.SystemTray;
import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tw.darkk6.mcmod.autofish.config.AutoFishConfig;
import tw.darkk6.mcmod.autofish.config.Reference;
import tw.darkk6.mcmod.autofish.util.Lang;
import tw.darkk6.mcmod.autofish.util.Log;

@Mod(modid=Reference.MOD_ID , version=Reference.MOD_VER , clientSideOnly=true , guiFactory = Reference.GUI_FACTORY , dependencies=Reference.FORGE_VER)
public class AutoFishMod {
	
	@Instance(Reference.MOD_ID)
	public static AutoFishMod instance;
	public static AutoFishConfig config;
	public static File configFolder;
	
	public static boolean isEnable,isPrevent,isSwitch,isShowText,isCheckDistance;
	public static double maxDistance;
	public static int breakValue;
	public static String soundName;
	
	public static boolean canDesktopNotify;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		configFolder = evt.getModConfigurationDirectory();
		//讀取設定檔 ( ClientOnly，因此不用 Proxy )
		config = AutoFishConfig.getInstance(evt.getSuggestedConfigurationFile());
		canDesktopNotify = SystemTray.isSupported();
		if(!canDesktopNotify) Log.info(Lang.get("autofish.notiy.unavailable"));
		loadConfig();
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		//註冊 Events ( ClientOnly，因此不用 Proxy )
		MinecraftForge.EVENT_BUS.register(new AutoFishEventHandler());
	}
	
	public void saveConfig(){
		if(config.file.hasChanged()) config.save();
	}
	
	public void loadConfig(){
		isEnable = config.isEnabled.getBoolean();
		isPrevent = config.isPreventBroken.getBoolean();
		isSwitch = config.isAutoSwitch.getBoolean();
		isShowText = config.isShowText.getBoolean();
		isCheckDistance = config.isCheckDistance.getBoolean();
		maxDistance = config.maxDistance.getDouble();
		
		soundName = config.soundName.getString();
		breakValue=config.preventBreakValue.getInt();
	}
	
}
