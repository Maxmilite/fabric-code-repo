package com.maxmilite.quicksender.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import com.maxmilite.quicksender.QuickSender;

import java.util.function.Function;

import static com.maxmilite.quicksender.QuickSender.*;

public class AutofishScreenBuilder {

    private static final Function<Boolean, Text> yesNoTextSupplier = bool -> {
        if (bool) return Text.of("\u00A7aOn");
        else return Text.of("\u00A7eOff");
    };

    public static Screen buildScreen(MinecraftClient client) {

        Map<String, String> keyList = QuickSender.keyList;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(Text.of(text.qs.configscreen))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    QuickSender.writeConfig();
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(Text.of("autofishconfig"));

        //ClearLag Regex
        AbstractConfigListEntry clearLagRegexField = entryBuilder.startTextField(Text.of("ClearLag Chat Pattern"), config.getClearLagRegex())
                .setDefaultValue(defaults.getClearLagRegex())
                .setTooltip(
                        Text.of("Recast the fishing rod when"),
                        Text.of("this pattern is matched in chat."),
                        Text.of("\u00A76This pattern is a \u00A7aRegular Expression\u00A76.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setClearLagRegex(newValue);
                })
                .build();


        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(Text.of("Basic Options"));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.add(toggleBreakProtection);
        subCatBuilderBasic.add((togglePersistentMode));
        subCatBuilderBasic.setExpanded(true);

        SubCategoryBuilder subCatBuilderAdvanced = entryBuilder.startSubCategory(Text.of("Advanced Options"));
        subCatBuilderAdvanced.add(toggleSoundDetection);
        subCatBuilderAdvanced.add(toggleForceMPDetection);
        subCatBuilderAdvanced.add(recastDelaySlider);
        subCatBuilderAdvanced.add(clearLagRegexField);
        subCatBuilderAdvanced.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());
        configCat.addEntry(subCatBuilderAdvanced.build());

        return builder.build();

    }
}