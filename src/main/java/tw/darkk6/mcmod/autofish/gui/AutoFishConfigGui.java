package tw.darkk6.mcmod.autofish.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import tw.darkk6.mcmod.autofish.AutoFishMod;
import tw.darkk6.mcmod.autofish.config.Reference;
import tw.darkk6.mcmod.autofish.util.Lang;

public class AutoFishConfigGui extends GuiConfig {
	public AutoFishConfigGui(GuiScreen parent) {
		super(parent,
			new ConfigElement(AutoFishMod.config.file.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
				Reference.MOD_ID,
				false,//需要重新進入世界 ?
				false,//需要重新啟動 MC ?
				Lang.get("autofish.setting.gui.title")//標題
			);
		// 設定標題 2 顯示文字
		// GuiConfig.getAbridgedConfigPath() 可以把檔案路徑改成  .minecraft/ 底下的對應路徑呈現
		this.titleLine2 = GuiConfig.getAbridgedConfigPath(AutoFishMod.config.file.toString());
	}
}
