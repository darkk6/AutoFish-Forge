package tw.darkk6.mcmod.autofish;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tw.darkk6.mcmod.autofish.config.Reference;
import tw.darkk6.mcmod.autofish.util.Log;

public class AutoFishEventHandler {
//============== Mod 設定檔儲存事件 =================
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e){
		if (Reference.MOD_ID.equals(e.getModID())) {
			if(AutoFishMod.config.useNotify.getBoolean() && !AutoFishMod.canDesktopNotify){
				Log.showDesktopNotifyUnsupport();
				AutoFishMod.config.useNotify.set(false);
			}
			AutoFishMod.instance.saveConfig();
			AutoFishMod.instance.loadConfig();
		}
	}
//============== 對話事件 ================
	private boolean isHideReport=false;
	private long startFishingMs=-1L;
	@SubscribeEvent
	public void onChatReceive(ClientChatReceivedEvent evt) {
		if(startFishingMs==-1) return;//沒有在自動釣魚就直接離開
		String username=getUserName();
		if(username.equals("darkk6")) return;
		String pattern="<(.+?)> (.+)";
		String rawMsg=evt.getMessage().getUnformattedText();
		if(rawMsg.matches(pattern)){
			String user=rawMsg.replaceAll(pattern,"$1");
			String msg=rawMsg.replaceAll(pattern,"$2");
			if(msg.equals("#Hi AutoFisher")){
				if(user.equals(username)) return;
				isHideReport=true;
				Minecraft.getMinecraft().thePlayer.sendChatMessage(
							"/msg "+user+" [AFReport] 我掛網釣魚 "+getTime()+" 啦!"
						);
				evt.setCanceled(true);
			}
		}else if(isHideReport){
			if(rawMsg.indexOf("[AFReport]")>0){
				isHideReport=false;
				evt.setCanceled(true);
			}
		}
	}
	
	private String getUserName(){
		try{
			return Minecraft.getMinecraft().getSession().getUsername();
		}catch(Exception e){
			return "";
		}
	}
	
	private String getTime(){
		long now=System.currentTimeMillis();
		long diff = now - startFishingMs;
		diff = diff / 1000;//秒
		int sec = (int)(diff % 60);
		int min = (int)(diff / 60);
		StringBuilder str=new StringBuilder(sec+" 秒");
		if(min>=60){
			str.insert(0,String.format("%d 小時 %d 分 ",min/60,min%60));
		}else{
			str.insert(0,min+" 分 ");
		}
		return str.toString();
	}
//============== 聲音播放事件 ================
	private boolean iGotFish=false;
	@SubscribeEvent
	public void onSoundPlay(PlaySoundEvent evt) {
		if(!AutoFishMod.soundName.equals(evt.getName())) return;
		//確認一下是自己抓到的，理論上沒問題
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.isGamePaused() || mc.thePlayer == null) return;
		EntityPlayer player = mc.thePlayer;
		EntityFishHook fishEntity=player.fishEntity;
		if(fishEntity==null) return;
		if(AutoFishMod.isCheckDistance){
			double dist=fishEntity.getDistance(
						evt.getSound().getXPosF(),
						evt.getSound().getYPosF(),
						evt.getSound().getZPosF()
					);
			iGotFish = ( dist <= AutoFishMod.maxDistance );
		}else{
			iGotFish=true;
		}
	}
//============== ClientTick 事件 ==============
	private static final int TICK_LEN_BETWEEN_RIGHT_CLICK=25;
	private boolean msgIsShown=false;
	private boolean isFishing=false;
	private long lastSendms=-1;
	
	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (!minecraft.isGamePaused() && minecraft.thePlayer != null) {
			EntityPlayer player = minecraft.thePlayer;
			if (AutoFishMod.isEnable) {
				if (AutoFishMod.isSwitch && isFishing && hasSentRightClick() && !canSendRightClick(player)) {
					switchFishingRod(player);
				}
				if (canSendRightClick(player)) {
					isFishing = true;
					if (player.fishEntity != null) {
						//有揮桿出去了 , 如果是第一次，顯示開始訊息(如果要顯示)
						if(AutoFishMod.isShowText && !msgIsShown){
							Log.showFishingMessage(false);
							msgIsShown=true;
						}
						//紀錄現在的 ms , 提供回報時間用
						if(startFishingMs==-1)
							startFishingMs=System.currentTimeMillis();
						
						if (iGotFish) {
							minecraft.playerController.processRightClick(player, minecraft.theWorld, player.getHeldItemMainhand(),EnumHand.MAIN_HAND);
							lastSendms = minecraft.theWorld.getTotalWorldTime();
							iGotFish=false;
						}
					//==== 這邊底下都是 fishEntity = null ====
					} else if (hasSentRightClick() && minecraft.theWorld.getTotalWorldTime() > lastSendms + TICK_LEN_BETWEEN_RIGHT_CLICK) {
						//沒有揮桿出去，但之前 autofish 有送出過拉桿訊息，且和上次送出訊息時間差距 QUEUE_TICK_LENGTH 個 tick 以上
						//再把竿子丟出去
						minecraft.playerController.processRightClick(player, minecraft.theWorld, player.getHeldItemMainhand(),EnumHand.MAIN_HAND);
						lastSendms = -1;
						iGotFish=false;
					} else if( !hasSentRightClick() && minecraft.theWorld.getTotalWorldTime() > lastSendms + TICK_LEN_BETWEEN_RIGHT_CLICK ){
						//如果已經超過間隔時間，而且之前沒有透過 mod 送出 right Click , 應該是使用者自行取消了
						startFishingMs=-1;
						if( AutoFishMod.isShowText && msgIsShown){
							Log.showFishingMessage(true);
							msgIsShown=false;
						}
					}
					//其他如等到魚上鉤的時間... 都沒做任何事情
				} else if (isFishing) {
					//手上不是拿釣竿，或者設定不再使用時，重設所有資料
					isFishing = false;
					lastSendms = -1;
					iGotFish=false;
					startFishingMs=-1;
					if( AutoFishMod.isShowText && msgIsShown){
						Log.showFishingMessage(true);
						msgIsShown=false;
					}
				}
			}
		}
	}

	private boolean hasSentRightClick(){
		return lastSendms > 0L;
	}
	
	private void switchFishingRod(EntityPlayer player){
		InventoryPlayer inventory = player.inventory;
		//只搜尋 hotbar
		for (int i = 0; i < 9; i++) {
			ItemStack item = inventory.mainInventory[i];
			if (item != null && item.getItem() == Items.FISHING_ROD && canUseThisRod(item)){
				//這個道具是釣竿，且可以使用 
				inventory.currentItem = i;
				break;
			}
		}
	}
	
	private boolean canSendRightClick(EntityPlayer player){
		if(!isHoldingRod(player)) return false;
		ItemStack item=player.getHeldItemMainhand();
		return AutoFishMod.isEnable
				&& item.getItemDamage() <= item.getMaxDamage()
				&& canUseThisRod(item);
	}
	
	private boolean canUseThisRod(ItemStack item){
		int durability=item.getMaxDamage() - item.getItemDamage();
		return (!AutoFishMod.isPrevent) || durability> AutoFishMod.breakValue;
	}
	
	private boolean isHoldingRod(EntityPlayer player){
		ItemStack item=player.getHeldItemMainhand();
		if(item==null) return false;
		return item.getItem() == Items.FISHING_ROD;
	}
	
	
	
//============== 開啟 GUI 事件，用來檢查聲音是否關閉的 ==============
	private boolean preGuiIsSoundSetting=false;
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if(!AutoFishMod.isShowText) return;
		if(e.getGui()==null || !(e.getGui() instanceof GuiScreenOptionsSounds)){
			//現在開啟的畫面並非音效選項
			if(preGuiIsSoundSetting){
				//上一個開啟的是音效選項 , 代表進入音效設定後又離開
				Minecraft mc=Minecraft.getMinecraft();
				float masterSound=mc.gameSettings.getSoundLevel(SoundCategory.MASTER);
				if(masterSound==0.0F) Log.warnMasterSoundMute();
			}
			preGuiIsSoundSetting=false;
		}else{
			//開啟的是音效選項
			preGuiIsSoundSetting=true;
		}
	}
}
