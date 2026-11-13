package jp.mya.readychime.mixin;

import jp.mya.readychime.ReadyChime;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/** リソースの再読み込み（F3+T やリソースパックの切り替え）が終わったら知らせる */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void ready_chime$onReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        CompletableFuture<Void> future = cir.getReturnValue();
        if (future == null) return;
        Minecraft mc = (Minecraft) (Object) this;
        // 失敗したときは鳴らさない。音はメインスレッドで鳴らす
        future.thenRun(() -> mc.execute(ReadyChime::onReloadFinished));
    }
}
