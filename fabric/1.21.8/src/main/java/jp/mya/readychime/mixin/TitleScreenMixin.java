package jp.mya.readychime.mixin;

import jp.mya.readychime.ReadyChime;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** タイトル画面が出た＝ゲームの起動が終わった */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void ready_chime$onInit(CallbackInfo ci) {
        ReadyChime.onTitleScreen();
    }
}
