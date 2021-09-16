package com.maxmilite.bunnyhop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BunnyHop implements ModInitializer {
    public MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onInitialize() {
        KeyBinding keyForward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bunnyhop.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.movement"));
        KeyBinding keyLeft = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bunnyhop.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_A, "key.categories.movement"));
        KeyBinding keyBack = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bunnyhop.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_S, "key.categories.movement"));
        KeyBinding keyRight = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bunnyhop.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_D, "key.categories.movement"));
        KeyBinding keyJump = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bunnyhop.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, "key.categories.movement"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = mc.player;
//            if (t == 20) {
//                if (player != null) {
//                    System.out.println("x = " + player.getVelocity().x);
//                    System.out.println("y = " + player.getVelocity().y);
//                    System.out.println("z = " + player.getVelocity().z);
//                    System.out.println("yaw = " + player.getHeadYaw());
//                }
//                t = 0;
//            }
//            t++;
            while (player != null && keyJump.isPressed()) {
//                System.out.println("YESSSSS");
                float yaw = player.getHeadYaw();
                player.setSprinting(true);
                double x = 0f, y = player.getVelocity().y, z = 0f;
                if (keyForward.isPressed()) {
                    x += -Math.sin(yaw) * 0.2;
                    z += Math.cos(yaw) * 0.2;
                }
                if (keyBack.isPressed()) {
                    x += Math.sin(yaw) * 0.2;
                    z += -Math.cos(yaw) * 0.2;
                }
                if (keyLeft.isPressed()) {
                    x += Math.cos(yaw) * 0.2;
                    y += Math.sin(yaw) * 0.2;
                }
                if (keyRight.isPressed()) {
                    x += -Math.cos(yaw) * 0.2;
                    y += -Math.sin(yaw) * 0.2;
                }
                player.setVelocity(x, y, z);
            }

        });
    }
}
