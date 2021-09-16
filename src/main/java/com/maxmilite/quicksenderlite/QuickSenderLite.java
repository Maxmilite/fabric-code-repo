package com.maxmilite.quicksenderlite;

import net.fabricmc.api.ModInitializer;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class QuickSenderLite implements ModInitializer{
    @Override
    public void onInitialize() {

        KeyBinding homeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qslite.homekey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.qslite"

        ));
        KeyBinding spawnKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qslite.spawnkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.qslite"

        ));
        KeyBinding backKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qslite.backkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.qslite"

        ));
        KeyBinding dbackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qslite.dbackkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.qslite"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (dbackKey.wasPressed()) {
                client.player.sendChatMessage("/dback");
            }
            while (backKey.wasPressed()) {
                client.player.sendChatMessage("/back");
            }
            while (homeKey.wasPressed()) {
                client.player.sendChatMessage("/home");
            }
            while (spawnKey.wasPressed()) {
                client.player.sendChatMessage("/spawn");
            }
        });

        System.out.println("哇这里竟然有彩蛋");
    }
}
