package com.woalk.mods.woalklib.mixins.client;

import com.woalk.mods.woalklib.registry.MarkerRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

/**
 * Mixin to the rendering code for marker items (such as barrier blocks and light blocks)
 * to inject our own marker items.
 */
@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Redirect(method = "getMarkerParticleTarget", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean getBlockParticle_BlockMarker_contains(Set<Item> instance, Object o) {
        //noinspection SuspiciousMethodCalls
        return instance.contains(o) || MarkerRegistry.INSTANCE.getMarkerItems().contains(o);
    }
}
