package com.bards.fabric.client;

import com.bards.client.BardClient;
import net.fabricmc.api.ClientModInitializer;

public final class FabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BardClient.init();
    }
}
