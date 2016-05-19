package tw.darkk6.mcmod.autofish.config;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tw.darkk6.mcmod.autofish.util.Lang;

public class AutoFishConfig {
	private static final String SOUNDEVENTNAME="entity.bobber.splash";
	private static final int BREAKVALUE=5;
	
	private static ArrayList<String> propOrder;
	
	private static AutoFishConfig instance=null;
	public static AutoFishConfig getInstance(File configFile){
        if(instance==null) instance=new AutoFishConfig(configFile);
        return instance;
    }
	
	
	public Configuration file;
	// 一般使用者設定
	public Property isEnabled , isPreventBroken , isAutoSwitch , isShowText;
	public Property isCheckDistance , maxDistance;
	public Property useNotify;
	// 特殊設定
	public Property soundName , preventBreakValue;
	
    private AutoFishConfig(File configFile){
        this.file=new Configuration(configFile);
        reload();
	}

	public void save() {
		this.file.save();
    }
    
	void reload() {
		this.file.load();
		
		boolean setupOrder = (propOrder==null);
		
		file.setCategoryComment("general",Lang.get("autofish.setting.general.comment"));
		
		this.isEnabled = this.file.get("general", Lang.get("autofish.setting.enable"), true,Lang.get("autofish.setting.enable.comment"));
		//this.isEnabled.comment = Lang.get("autofish.setting.enable.comment");
		this.isEnabled.set(this.isEnabled.getBoolean());

		this.isPreventBroken = this.file.get("general", Lang.get("autofish.setting.preventBreak"), true,Lang.get("autofish.setting.preventBreak.comment"));
		//this.isPreventBroken.comment = Lang.get("autofish.setting.preventBreak.comment");
		this.isPreventBroken.set(this.isPreventBroken.getBoolean());
		
		this.isAutoSwitch = this.file.get("general", Lang.get("autofish.setting.switch"), true,Lang.get("autofish.setting.switch.comment"));
		//this.isAutoSwitch.comment = Lang.get("autofish.setting.switch.comment");
		this.isAutoSwitch.set(this.isAutoSwitch.getBoolean());
		
		this.isShowText = this.file.get("general", Lang.get("autofish.setting.showtext"), true,Lang.get("autofish.setting.showtext.comment"));
		//this.isShowText.comment = Lang.get("autofish.setting.showtext.comment");
		this.isShowText.set(this.isShowText.getBoolean());
		
		this.isCheckDistance = this.file.get("general", Lang.get("autofish.setting.checkdistance") , false , Lang.get("autofish.setting.checkdistance.comment"));
		//this.isCheckDistance.comment = Lang.get("autofish.setting.checkdistance.comment");
		this.isCheckDistance.set(this.isCheckDistance.getBoolean());
		
		this.maxDistance = this.file.get("general", Lang.get("autofish.setting.maxdistance") , 1.7D , Lang.get("autofish.setting.maxdistance.comment"));
		//this.maxDistance.comment = Lang.get("autofish.setting.maxdistance.comment");
		this.maxDistance.set(this.maxDistance.getDouble());
		
		this.useNotify = this.file.get("general", Lang.get("autofish.setting.useNotify"), false , Lang.get("autofish.setting.useNotify.comment"));
		this.useNotify.set(this.useNotify.getBoolean());
		
		if(setupOrder){
			propOrder=new ArrayList<String>();
			propOrder.add(Lang.get("autofish.setting.enable"));
			propOrder.add(Lang.get("autofish.setting.preventBreak"));
			propOrder.add(Lang.get("autofish.setting.switch"));
			propOrder.add(Lang.get("autofish.setting.showtext"));
			propOrder.add(Lang.get("autofish.setting.checkdistance"));
			propOrder.add(Lang.get("autofish.setting.maxdistance"));
			propOrder.add(Lang.get("autofish.setting.useNotify"));
			file.setCategoryPropertyOrder("general", propOrder);
		}
		
		
		file.setCategoryComment("special",Lang.get("autofish.setting.special.comment"));
		
		this.soundName = this.file.get("special", Lang.get("autofish.setting.soundname"), SOUNDEVENTNAME , Lang.get("autofish.setting.soundname.comment"));
		//this.soundName.comment = Lang.get("autofish.setting.soundname.comment");
		this.soundName.set(this.soundName.getString());

		this.preventBreakValue = this.file.get("special", Lang.get("autofish.setting.breakvalue"), BREAKVALUE , Lang.get("autofish.setting.breakvalue.comment"));
		this.preventBreakValue.setMinValue(2);
		this.preventBreakValue.setMaxValue(64);
		//this.preventBreakValue.comment = Lang.get("autofish.setting.breakvalue.comment");
		int tmp = this.preventBreakValue.getInt();
		if( tmp > 64 ) tmp = 64;
		else if(tmp<2) tmp=2;
		this.preventBreakValue.set(tmp);
		
		this.file.save();
	}
    
}
