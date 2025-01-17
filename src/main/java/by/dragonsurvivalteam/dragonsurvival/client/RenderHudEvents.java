package by.dragonsurvivalteam.dragonsurvival.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientGrowthHudHandler;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderHudEvents {
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "hud"}, key = "vanillaFoodLevel", comment = "Re-enable the vanilla hud for the food level")
    public static Boolean vanillaFoodLevel = false;

    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "hud"}, key = "vanillaExperienceBar", comment = "Re-enable the vanilla hud for the experience bar")
    public static Boolean vanillaExperienceBar = false;

    public static ForgeGui getForgeGUI() {
        return (ForgeGui) Minecraft.getInstance().gui;
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderOverlay(final RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (event.isCanceled() ||
            minecraft.options.hideGui){
            return;
        }

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();
        ResourceLocation id = event.getOverlay().id();

        if (!vanillaFoodLevel && id == VanillaGuiOverlay.FOOD_LEVEL.id()) {
            event.setCanceled(true);
            DragonFoodHandler.onRenderFoodBar(getForgeGUI(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
        } else if (!vanillaExperienceBar && id == VanillaGuiOverlay.EXPERIENCE_BAR.id()) {
            event.setCanceled(true);
            ClientMagicHUDHandler.cancelExpBar(getForgeGUI(), event.getPoseStack(), event.getPartialTick(),screenWidth, screenHeight);
        } else if (id == VanillaGuiOverlay.AIR_LEVEL.id()) {
            // Render dragon specific hud elements (e.g. time in rain for cave dragons or time without water for sea dragons)
            ClientEvents.onRenderOverlayPreTick(getForgeGUI(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
            // Renders the abilities
            ClientMagicHUDHandler.renderAbilityHud(getForgeGUI(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
            // Renders the growth icon above the experience bar when an item is selected which grants growth
            ClientGrowthHudHandler.renderGrowth(getForgeGUI(), event.getPoseStack(), event.getPartialTick(), screenWidth, screenHeight);
        }
    }
}
