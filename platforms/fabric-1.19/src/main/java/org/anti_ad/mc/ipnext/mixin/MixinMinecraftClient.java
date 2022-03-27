package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinMinecraftClient
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Final public GameOptions options;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTickPre();
    }

    @Inject(at = @At("RETURN"), method = "tick()V")
    public void tick2(CallbackInfo info) {
        ClientEventHandler.INSTANCE.onTick();
    }

    @Inject(at = @At("RETURN"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    public void joinWorld(ClientWorld clientWorld, CallbackInfo info) {
        ClientEventHandler.INSTANCE.onJoinWorld();
    }

    @Inject(at = @At("HEAD"),
            method = "handleInputEvents()V")
    public void handleInputEvents(CallbackInfo info) {
        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isValidHotbarIndex(this.player.getInventory().selectedSlot)
                && (MinecraftClient.getInstance().options.dropKey.isPressed() || MinecraftClient.getInstance().options.dropKey.wasPressed())) {

            if (!LockSlotsHandler.INSTANCE.isHotbarQMoveActionAllowed(this.player.getInventory().selectedSlot + 36, true)) {
                IMixinKeyBinding drop = (IMixinKeyBinding) MinecraftClient.getInstance().options.dropKey;
                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
