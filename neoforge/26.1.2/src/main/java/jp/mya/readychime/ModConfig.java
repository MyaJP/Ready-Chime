package jp.mya.readychime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * config/ready_chime.json に保存される設定。
 * ゲーム内の「設定 → 音楽とサウンド → Ready Chime」からも変えられる。
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig instance;

    /** ゲームを起動してタイトル画面が出たときに鳴らす */
    public boolean onTitleScreen = true;
    /** リソースの再読み込み（F3+T やリソースパックの切り替え）が終わったときに鳴らす */
    public boolean onResourceReload = true;
    /** 鳴らす音のID（/playsound で使えるものと同じ） */
    public String sound = "minecraft:entity.experience_orb.pickup";
    /** 音量（0.0～1.0） */
    public float volume = 1.0f;

    public static synchronized ModConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static Path path() {
        return FMLPaths.CONFIGDIR.get().resolve("ready_chime.json");
    }

    /** 今の設定をファイルに書く */
    public static synchronized void save() {
        try {
            Path path = path();
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(get(), w);
            }
        } catch (Exception e) {
            ReadyChime.LOGGER.error("Failed to save config", e);
        }
    }

    private static ModConfig load() {
        ModConfig cfg = new ModConfig();
        try {
            Path path = path();
            if (Files.exists(path)) {
                try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    ModConfig loaded = GSON.fromJson(r, ModConfig.class);
                    if (loaded != null) cfg = loaded;
                }
            }
            // 音が無い、または前の版にあって今は外した音なら、最初の音に戻す
            if (cfg.sound == null || cfg.sound.startsWith("minecraft:block.note_block.")
                    || cfg.sound.equals("minecraft:entity.arrow.hit_player")
                    || cfg.sound.equals("minecraft:block.bell.use")
                    || cfg.sound.equals("minecraft:ui.toast.challenge_complete")) cfg.sound = new ModConfig().sound;
            // 前の版のアメジストの音は小さくて聞こえにくかったので、新しい方に置き換える
            if (cfg.sound.equals("minecraft:block.amethyst_block.chime")) cfg.sound = "minecraft:block.amethyst_cluster.break";
            // 花火は「弾ける音」から「打ち上げる音」に変えた
            if (cfg.sound.equals("minecraft:entity.firework_rocket.blast")) cfg.sound = "minecraft:entity.firework_rocket.launch";
            cfg.volume = Math.max(0.0f, Math.min(1.0f, cfg.volume));
            // 新しい項目を追加したときにファイルへ反映させるため毎回書き直す
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            ReadyChime.LOGGER.error("Failed to load config, using defaults", e);
            cfg = new ModConfig();
        }
        return cfg;
    }
}
