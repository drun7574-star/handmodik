package com.example.handmod;

import net.fabricmc.api.ModInitializer;

public class HandMod implements ModInitializer {
    public static final String MOD_ID = "handmod";

    @Override
    public void onInitialize() {
        ModItems.init();
        ArmEvents.register();
    }
}
