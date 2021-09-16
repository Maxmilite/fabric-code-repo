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
    public Map<String, String> keyList = new HashMap<>() {
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
        CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();
            dispatcher.register(
                    literal("quicksender")
                            .then(literal("set")
                                    .then(argument("Number", integer())
                                            .then(argument("Command", string())
                                                    .executes(c -> {
                                                        int cur = getInteger(c, "Number");
                                                        if (cur > keyCount || cur <= 0) {
                                                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
                                                                    "\u00a7e[\u00a7bQuickSender\u00a7e]\u00a7r \u00a7b 选择的指令数超出现有的指令个数或不合法"));
                                                            return -1;
                                                        } else {
                                                            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                                                                while (List.get(cur - 1).wasPressed()) {
                                                                    client.player.sendChatMessage(getString(c, "Command").replaceAll("\"", ""));
                                                                }
                                                            });
                                                            keyList.put(String.valueOf(cur), getString(c, "Command").replaceAll("\"", ""));
                                                            try {
                                                                writeConfig();
                                                            } catch(IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
                                                                    "\u00a7e[\u00a7bQuickSender\u00a7e]\u00a7r \u00a7e 以下快捷指令已添加完毕: \n" +
                                                                            "\u00a7a第 " + String.valueOf(getInteger(c, "Number")) + " 条快捷指令 -> " + getString(c, "Command")
                                                            ));
                                                        }
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
//                            .then(CommandManager.literal("count")
//                                    .then(CommandManager.argument("Number", integer())
//                                            .executes(c -> {
//                                                int cnt = getInteger(c, "Number");
//                                                if (cnt >= keyCount) {
//                                                    for (int i = keyCount + 1; i <= cnt; ++i)
//                                                        List.add(KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                                                                "快捷按键 " + String.valueOf(i),
//                                                                InputUtil.Type.KEYSYM,
//                                                                GLFW.GLFW_KEY_UNKNOWN,
//                                                                "快捷指令"
//                                                        )));
//                                                } else {
//                                                    for (int i = keyCount; i > cnt; --i) {
//                                                        List.remove(i - 1);
//                                                    }
//                                                }
//                                                keyCount = cnt;
//                                                try {
//                                                    writeConfig();
//                                                } catch(IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
//                                                        "\u00a7e[\u00a7bQuickSender\u00a7e]\u00a7r \u00a7e 快捷指令条数已设置为: \u00a7a" +
//                                                                String.valueOf(cnt)));
//                                                return 1;
//                                            })
//                                    )
//                            )
                            .then(literal("help")
                                    .executes(c -> {
                                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
                                                        "\u00a7e[\u00a7bQuickSender\u00a7e]\u00a7r \u00a7e 使用帮助 \n" +
                                                                "\u00a7e1. /quicksender set <数字> \"<指令>\" \n \u00a7e设置某一快捷命令为某指令 \n" +
                                                                "\u00a7e例: /quicksender set 1 \"/home\" 设置第一条命令为 \u00a7a/home"
                                                )
                                        );
                                        return 1;
                                    })
                            )
                            .executes(c -> {
                                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(
                                                "\u00a7e[\u00a7bQuickSender\u00a7e]\u00a7r \u00a7e 使用帮助 \n" +
                                                        "\u00a7e1. /quicksender set <数字> \"<指令>\"\n\u00a7e设置某一快捷命令为某指令\n" +
                                                        "\u00a7e例: /quicksender set 1 \"/home\" 设置第一条命令为\u00a7a/home"
                                        )
                                );
                                return 1;
                            })
            );
        System.out.println("哇这里竟然有彩蛋");
    }
}
