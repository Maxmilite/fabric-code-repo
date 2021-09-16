package com.maxmilite.fastmenu;

import net.fabricmc.api.ModInitializer;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FastMenu implements ModInitializer {
    @Override
    public void onInitialize() {

        KeyBinding bindKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fastmenu.bindkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (bindKey.wasPressed()) {
                client.player.sendChatMessage("/c");
            }

        });

        System.out.println("哇这里竟然有彩蛋");
    }
}