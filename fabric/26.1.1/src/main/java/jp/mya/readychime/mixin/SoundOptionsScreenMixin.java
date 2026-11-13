package jp.mya.readychime.mixin;

import jp.mya.readychime.ChimeScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 「音楽とサウンド」の設定の一番下に「Ready Chime...」の項目を足す */
@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {
    protected SoundOptionsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void ready_chime$addEntry(CallbackInfo ci) {
        if (this.list == null) return;
        Screen self = this;
        Button button = Button.builder(Component.literal("Ready Chime..."),
                b -> ChimeScreen.open(new ChimeScreen(self))).bounds(0, 0, 150, 20).build();
        this.list.addSmall(button, null);
    }
}
