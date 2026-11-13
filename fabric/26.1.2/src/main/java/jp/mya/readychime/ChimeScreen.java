package jp.mya.readychime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * ゲーム内の設定画面（設定 → 音楽とサウンド → Ready Chime）。
 * 言語ファイルを使わず、ゲームの言語が日本語なら日本語、それ以外は英語で表示する
 * （Fabric API が無くても文字が出るようにするため）。
 */
public class ChimeScreen extends Screen {
    /** 選べる音（ID, 英語名, 日本語名） */
    private static final String[][] SOUNDS = {
            {"minecraft:entity.experience_orb.pickup", "Experience orb", "経験値オーブ"},
            {"minecraft:entity.player.levelup", "Level up", "レベルアップ"},
            {"minecraft:block.amethyst_cluster.break", "Amethyst", "アメジスト"},
            {"minecraft:block.brewing_stand.brew", "Brewing stand", "醸造台"},
            {"minecraft:block.beacon.activate", "Beacon", "ビーコン"},
            {"minecraft:entity.item.pickup", "Item pickup", "アイテムを拾う音"},
            {"minecraft:entity.chicken.egg", "Chicken egg", "ニワトリの卵"},
            {"minecraft:entity.cat.ambient", "Cat", "ネコ"},
            {"minecraft:block.anvil.land", "Anvil", "金床"},
            {"minecraft:entity.firework_rocket.launch", "Firework", "花火"},
            {"minecraft:item.totem.use", "Totem of Undying", "不死のトーテム"},
            {"minecraft:entity.ender_dragon.growl", "Ender Dragon", "エンダードラゴン"},
    };

    private final Screen parent;

    public ChimeScreen(Screen parent) {
        super(Component.literal("Ready Chime"));
        this.parent = parent;
    }

    static boolean japanese() {
        try {
            return "ja_jp".equals(Minecraft.getInstance().getLanguageManager().getSelected());
        } catch (Throwable t) {
            return false;
        }
    }

    public static Component text(String en, String ja) {
        return Component.literal(japanese() ? ja : en);
    }

    private static Component onOff(String en, String ja, boolean on) {
        return japanese()
                ? Component.literal(ja + "：" + (on ? "オン" : "オフ"))
                : Component.literal(en + ": " + (on ? "ON" : "OFF"));
    }

    private static int soundIndex(String id) {
        for (int i = 0; i < SOUNDS.length; i++) {
            if (SOUNDS[i][0].equals(id)) return i;
        }
        return -1;
    }

    private static Component soundLabel() {
        String id = ModConfig.get().sound;
        int i = soundIndex(id);
        String name = i < 0 ? id : SOUNDS[i][japanese() ? 2 : 1]; // 設定ファイルに自分で書いた音はIDのまま出す
        return Component.literal((japanese() ? "音：" : "Sound: ") + name);
    }

    @Override
    protected void init() {
        ModConfig cfg = ModConfig.get();
        int w = 220;
        int x = this.width / 2 - w / 2;
        int y = Math.max(8, this.height / 2 - 66);

        addRenderableWidget(new StringWidget(x, y, w, 12, this.title, Minecraft.getInstance().font));
        y += 20;

        addRenderableWidget(Button.builder(onOff("On game start", "ゲーム起動時", cfg.onTitleScreen), b -> {
            cfg.onTitleScreen = !cfg.onTitleScreen;
            b.setMessage(onOff("On game start", "ゲーム起動時", cfg.onTitleScreen));
        }).bounds(x, y, w, 20).build());
        y += 24;

        addRenderableWidget(Button.builder(onOff("On resource reload", "リソースの再読み込み時", cfg.onResourceReload), b -> {
            cfg.onResourceReload = !cfg.onResourceReload;
            b.setMessage(onOff("On resource reload", "リソースの再読み込み時", cfg.onResourceReload));
        }).bounds(x, y, w, 20).build());
        y += 24;

        addRenderableWidget(Button.builder(soundLabel(), b -> {
            cfg.sound = SOUNDS[(soundIndex(cfg.sound) + 1) % SOUNDS.length][0];
            b.setMessage(soundLabel());
            ReadyChime.play(); // 選んだ音がすぐ分かるように鳴らす
        }).bounds(x, y, w, 20).build());
        y += 24;

        // 音量バーと、その右に「試しに鳴らす」ボタン（♪）
        addRenderableWidget(new VolumeSlider(x, y, w - 24, 20));
        addRenderableWidget(Button.builder(Component.literal("♪"), b -> ReadyChime.play())
                .bounds(x + w - 20, y, 20, 20)
                .tooltip(Tooltip.create(text("Play test sound", "試しに鳴らす")))
                .build());
        y += 32;

        addRenderableWidget(Button.builder(text("Done", "完了"), b -> onClose())
                .bounds(x, y, w, 20).build());
    }

    @Override
    public void onClose() {
        ModConfig.save();
        open(parent);
    }

    /** 画面を切り替える（26.2 から場所が変わった） */
    public static void open(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    private static class VolumeSlider extends AbstractSliderButton {
        VolumeSlider(int x, int y, int w, int h) {
            super(x, y, w, h, Component.empty(), ModConfig.get().volume);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal((japanese() ? "音量：" : "Volume: ") + Math.round(this.value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            ModConfig.get().volume = (float) this.value;
        }
    }
}
