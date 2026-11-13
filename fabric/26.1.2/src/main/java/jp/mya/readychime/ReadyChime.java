package jp.mya.readychime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 読み込みが終わったら音で知らせる。
 * ・ゲームを起動してタイトル画面が出たとき（最初の1回だけ）
 * ・リソースの再読み込み（F3+T やリソースパックの切り替え）が終わったとき
 */
public final class ReadyChime {
    public static final String MOD_ID = "ready_chime";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String DEFAULT_SOUND = "minecraft:entity.experience_orb.pickup";

    private static boolean titleShown;
    /** 最後に鳴らした音 */
    private static SoundInstance playing;

    private ReadyChime() {}

    /** タイトル画面が出たとき（mixin から呼ばれる） */
    public static void onTitleScreen() {
        if (titleShown) return; // 2回目以降（ワールドから戻ったときなど）は鳴らさない
        titleShown = true;
        if (ModConfig.get().onTitleScreen) play();
    }

    /** リソースの再読み込みが終わったとき（mixin から呼ばれる） */
    public static void onReloadFinished() {
        if (ModConfig.get().onResourceReload) play();
    }

    /** 設定されている音を鳴らす（設定画面の「試しに鳴らす」でも使う） */
    public static void play() {
        try {
            ModConfig cfg = ModConfig.get();
            Identifier id = Identifier.tryParse(cfg.sound);
            if (id == null) id = Identifier.tryParse(DEFAULT_SOUND);
            SoundEvent sound = SoundEvent.createVariableRangeEvent(id);
            SoundManager sounds = Minecraft.getInstance().getSoundManager();
            if (playing != null) sounds.stop(playing); // 前の音が鳴りっぱなしにならないように止める
            playing = SimpleSoundInstance.forUI(sound, 1.0f, cfg.volume);
            sounds.play(playing);
        } catch (Throwable t) {
            LOGGER.warn("Failed to play the chime", t);
        }
    }
}
