package com.maxmilite.quicksender;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.mojang.brigadier.arguments.IntegerArgumentType.*;

import net.minecraft.text.*;

public class QuickSender implements ClientModInitializer {
    public int keyCount = 10;
    public static Map<String, String> keyList = new Map<>() {
        {
            put("1", "Hello World!");
            put("2", "Hello World!");
            put("3", "Hello World!");
            put("4", "Hello World!");
            put("5", "Hello World!");
            put("6", "Hello World!");
            put("7", "Hello World!");
            put("8", "Hello World!");
            put("9", "Hello World!");
            put("10", "Hello World!");
        }
    };
    public MinecraftClient mc = MinecraftClient.getInstance();
    public File configFile = new File(mc.runDirectory + "/config/QuickSender.json");
    public ArrayList<KeyBinding> List = new ArrayList<>();

    // Code from CSDN
    public static String readJsonFile(String fileName) {
        String jsonStr;
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void readConfig() {
        String s = readJsonFile(configFile.toString());
        JsonParser parser = new JsonParser();
        JsonElement jsonNode = parser.parse(s);
        if (jsonNode.isJsonObject()) {
            JsonObject object = jsonNode.getAsJsonObject();
            keyCount = object.get("keyCount").getAsInt();
            Iterator<Map.Entry<String, JsonElement>> it = object.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().toString() == "keyCount")
                    continue;
                else
                    keyList.put(entry.getKey().toString(), entry.getValue().toString());
            }
            System.out.println(keyCount);
            for (String key : keyList.keySet()) {
                System.out.println(key + " " + keyList.get(key));
            }
        }
        for (int i = 1; i <= keyCount; ++i) {
            List.add(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "快捷按键 " + String.valueOf(i),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "快捷指令"
            )));
            int finalI = i;
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (List.get(finalI - 1).wasPressed()) {
                    client.player.sendChatMessage(keyList.get(String.valueOf(finalI)).replaceAll("\"", ""));
                }
            });
        }
    }

    public void writeConfig() throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("keyCount", keyCount);
        for (String key : keyList.keySet()) {
            object.addProperty(key, keyList.get(key).replaceAll("\"", ""));
            System.out.println(key + keyList.get(key));
        }
        FileWriter cout = new FileWriter(configFile.toString());
        cout.write(object.toString());
        cout.close();
    }

    @Override
    public void onInitializeClient() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                writeConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        readConfig();
        
        System.out.println("哇这里竟然有彩蛋");
    }
}
