package com.lavalive.common;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LavaConfigScreen extends Screen {
    private final Screen lastScreen;

    private int activeTab = 0; // 0 = Visuals, 1 = Audio, 2 = Gameplay

    private EditBox frequencyField;
    private EditBox intensityField;
    private EditBox soundVolumeField;
    private EditBox overworldRateField;
    private EditBox damageCooldownField;
    private EditBox particleStayTimeField;

    public LavaConfigScreen(Screen lastScreen) {
        super(Component.literal("Lava Particles | More Configuration"));
        this.lastScreen = lastScreen;
    }

    private void parseActiveTabFields() {
        if (activeTab == 0) {
            try {
                if (frequencyField != null) {
                    double f = Double.parseDouble(frequencyField.getValue());
                    if (f >= 0.0 && f <= 20.0) LavaConfig.bubbleFrequency = f;
                }
            } catch (NumberFormatException ignored) {}
            try {
                if (intensityField != null) {
                    double iVal = Double.parseDouble(intensityField.getValue());
                    if (iVal >= 0.0 && iVal <= 20.0) LavaConfig.particleIntensity = iVal;
                }
            } catch (NumberFormatException ignored) {}
            try {
                if (overworldRateField != null) {
                    double o = Double.parseDouble(overworldRateField.getValue());
                    if (o >= 0.0 && o <= 20.0) LavaConfig.overworldMultiplier = o;
                }
            } catch (NumberFormatException ignored) {}
            try {
                if (particleStayTimeField != null) {
                    double tVal = Double.parseDouble(particleStayTimeField.getValue());
                    if (tVal >= 0.0 && tVal <= 30.0) LavaConfig.magmaFragmentStayTime = tVal;
                }
            } catch (NumberFormatException ignored) {}
        } else if (activeTab == 1) {
            try {
                if (soundVolumeField != null) {
                    double s = Double.parseDouble(soundVolumeField.getValue());
                    if (s >= 0.0 && s <= 20.0) LavaConfig.soundVolumeMultiplier = s;
                }
            } catch (NumberFormatException ignored) {}
        } else if (activeTab == 2) {
            try {
                if (damageCooldownField != null) {
                    double c = Double.parseDouble(damageCooldownField.getValue());
                    if (c >= 0.0 && c <= 10.0) LavaConfig.damageCooldown = c;
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void resetToDefaults() {
        LavaConfig.bubbleFrequency = 1.0;
        LavaConfig.particleIntensity = 1.0;
        LavaConfig.overworldMultiplier = 1.0;
        LavaConfig.magmaFragmentStayTime = 8.0;
        LavaConfig.enableEruptions = true;
        LavaConfig.enableLargeBubbles = true;
        LavaConfig.performanceMode = false;
        LavaConfig.testMode = false;
        LavaConfig.soundVolumeMultiplier = 1.0;
        LavaConfig.enablePopSounds = true;
        LavaConfig.enableSmallBubblePopSounds = true;
        LavaConfig.enableRumbleSounds = true;
        LavaConfig.particlesDealDamage = false;
        LavaConfig.damageCooldown = 3.0;
        LavaConfig.enablePlayerLavaInteraction = true;
        LavaConfig.enableRegularBubbles = true;

        this.init(this.minecraft, this.width, this.height);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int leftColX = centerX - 155;
        int rightColX = centerX + 5;
        int btnWidth = 150;

        // --- Tabs Navigation Buttons ---
        this.addRenderableWidget(Button.builder(Component.literal(activeTab == 0 ? "§6§l[ Visuals ]" : "§7Visuals"), (btn) -> {
            parseActiveTabFields();
            this.activeTab = 0;
            this.init(this.minecraft, this.width, this.height);
        }).bounds(centerX - 130, 45, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(activeTab == 1 ? "§6§l[ Audio ]" : "§7Audio"), (btn) -> {
            parseActiveTabFields();
            this.activeTab = 1;
            this.init(this.minecraft, this.width, this.height);
        }).bounds(centerX - 40, 45, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(activeTab == 2 ? "§6§l[ Gameplay ]" : "§7Gameplay"), (btn) -> {
            parseActiveTabFields();
            this.activeTab = 2;
            this.init(this.minecraft, this.width, this.height);
        }).bounds(centerX + 50, 45, 80, 20).build());

        // Nullify fields so we don't hold dangling references
        this.frequencyField = null;
        this.intensityField = null;
        this.soundVolumeField = null;
        this.overworldRateField = null;
        this.damageCooldownField = null;
        this.particleStayTimeField = null;

        // --- Render Tab Content ---
        if (activeTab == 0) {
            // Frequency Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eFrequency (Reset)"), (btn) -> {
                if (this.frequencyField != null) this.frequencyField.setValue("1.0");
            }).bounds(leftColX, 75, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (1.0). Lava block check rate. Custom decimal (0.0 to 20.0).")))
             .build());

            this.frequencyField = new EditBox(this.font, leftColX + 115, 75, 40, 20, Component.literal("Frequency"));
            this.frequencyField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.bubbleFrequency));
            this.addRenderableWidget(this.frequencyField);

            // Particle Intensity Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eIntensity (Reset)"), (btn) -> {
                if (this.intensityField != null) this.intensityField.setValue("1.0");
            }).bounds(rightColX, 75, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (1.0). Multiplies spawned particles on pop (0.0 to 20.0).")))
             .build());

            this.intensityField = new EditBox(this.font, rightColX + 115, 75, 40, 20, Component.literal("Intensity"));
            this.intensityField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.particleIntensity));
            this.addRenderableWidget(this.intensityField);

            // Overworld Rate Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eOverworld (Reset)"), (btn) -> {
                if (this.overworldRateField != null) this.overworldRateField.setValue("1.0");
            }).bounds(leftColX, 105, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (1.0). Lava activity modifier in Overworld (0.0 to 20.0).")))
             .build());

            this.overworldRateField = new EditBox(this.font, leftColX + 115, 105, 40, 20, Component.literal("Overworld Rate"));
            this.overworldRateField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.overworldMultiplier));
            this.addRenderableWidget(this.overworldRateField);

            // Particle Stay Time Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eStay Time (Reset)"), (btn) -> {
                if (this.particleStayTimeField != null) this.particleStayTimeField.setValue("8.0");
            }).bounds(rightColX, 105, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (8.0). Seconds landing magma remains on floor (0.0 to 30.0).")))
             .build());

            this.particleStayTimeField = new EditBox(this.font, rightColX + 115, 105, 40, 20, Component.literal("Stay Time"));
            this.particleStayTimeField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.magmaFragmentStayTime));
            this.addRenderableWidget(this.particleStayTimeField);

            // Boiling Eruptions Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eEruptions: " + (LavaConfig.enableEruptions ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enableEruptions = !LavaConfig.enableEruptions;
                    btn.setMessage(Component.literal("§eEruptions: " + (LavaConfig.enableEruptions ? "§aON" : "§cOFF")));
                }
            ).bounds(leftColX, 135, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles violent boiling surges and rare lava geysers in large lava lakes.")))
             .build());

            // Large Magma Bubbles Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eLarge Bubbles: " + (LavaConfig.enableLargeBubbles ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enableLargeBubbles = !LavaConfig.enableLargeBubbles;
                    btn.setMessage(Component.literal("§eLarge Bubbles: " + (LavaConfig.enableLargeBubbles ? "§aON" : "§cOFF")));
                }
            ).bounds(rightColX, 135, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles rare large magma bubbles rising upwards through deep lava lakes.")))
             .build());

            // Performance Mode Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§ePerformance: " + (LavaConfig.performanceMode ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.performanceMode = !LavaConfig.performanceMode;
                    btn.setMessage(Component.literal("§ePerformance: " + (LavaConfig.performanceMode ? "§aON" : "§cOFF")));
                }
            ).bounds(leftColX, 160, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Reduces background block checks and interval rate to conserve CPU.")))
             .build());

            // Regular Ambient Bubbles Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eBubbles: " + (LavaConfig.enableRegularBubbles ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enableRegularBubbles = !LavaConfig.enableRegularBubbles;
                    btn.setMessage(Component.literal("§eBubbles: " + (LavaConfig.enableRegularBubbles ? "§aON" : "§cOFF")));
                }
            ).bounds(rightColX, 160, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles standard ambient lava bubbles rising and popping.")))
             .build());

            // Testing Mode Toggle (Centered below)
            this.addRenderableWidget(Button.builder(
                Component.literal("§d§lTesting Mode: " + (LavaConfig.testMode ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.testMode = !LavaConfig.testMode;
                    btn.setMessage(Component.literal("§d§lTesting Mode: " + (LavaConfig.testMode ? "§aON" : "§cOFF")));
                }
            ).bounds(centerX - 75, 185, 150, 20)
             .tooltip(Tooltip.create(Component.literal("Forces immediate, extremely intensive lava bubbles/flares for visual testing.")))
             .build());

        } else if (activeTab == 1) {
            // Sound Volume Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eSound Vol (Reset)"), (btn) -> {
                if (this.soundVolumeField != null) this.soundVolumeField.setValue("1.0");
            }).bounds(leftColX, 75, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (1.0). Volume multiplier for pop/rumble (0.0 to 20.0).")))
             .build());

            this.soundVolumeField = new EditBox(this.font, leftColX + 115, 75, 40, 20, Component.literal("Sound Volume"));
            this.soundVolumeField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.soundVolumeMultiplier));
            this.addRenderableWidget(this.soundVolumeField);

            // Pop Sounds Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§ePop Sounds: " + (LavaConfig.enablePopSounds ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enablePopSounds = !LavaConfig.enablePopSounds;
                    btn.setMessage(Component.literal("§ePop Sounds: " + (LavaConfig.enablePopSounds ? "§aON" : "§cOFF")));
                }
            ).bounds(rightColX, 75, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles sound effects for popping magma bubbles.")))
             .build());

            // Rumble Sounds Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eRumble Sounds: " + (LavaConfig.enableRumbleSounds ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enableRumbleSounds = !LavaConfig.enableRumbleSounds;
                    btn.setMessage(Component.literal("§eRumble Sounds: " + (LavaConfig.enableRumbleSounds ? "§aON" : "§cOFF")));
                }
            ).bounds(leftColX, 105, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles deep rumble sounds during boiling eruptions and volcanic geysers.")))
             .build());

            // Small Bubble Pop Sounds Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eSmall Bubble Pop: " + (LavaConfig.enableSmallBubblePopSounds ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enableSmallBubblePopSounds = !LavaConfig.enableSmallBubblePopSounds;
                    btn.setMessage(Component.literal("§eSmall Bubble Pop: " + (LavaConfig.enableSmallBubblePopSounds ? "§aON" : "§cOFF")));
                }
            ).bounds(rightColX, 105, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Toggles light popping sounds for small ambient lava bubbles.")))
             .build());

        } else if (activeTab == 2) {
            // Splash Damage Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eSplash Damage: " + (LavaConfig.particlesDealDamage ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.particlesDealDamage = !LavaConfig.particlesDealDamage;
                    btn.setMessage(Component.literal("§eSplash Damage: " + (LavaConfig.particlesDealDamage ? "§aON" : "§cOFF")));
                }
            ).bounds(leftColX, 75, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Causes landing hot magma droplets to deal fire damage to players nearby.")))
             .build());

            // Damage Cooldown Row: Reset Button on Left, EditBox on Right
            this.addRenderableWidget(Button.builder(Component.literal("§eDmg CD (Reset)"), (btn) -> {
                if (this.damageCooldownField != null) this.damageCooldownField.setValue("3.0");
            }).bounds(rightColX, 75, 110, 20)
             .tooltip(Tooltip.create(Component.literal("Click to reset to default (3.0). Cooldown on fire damage in seconds (0.0 to 10.0).")))
             .build());

            this.damageCooldownField = new EditBox(this.font, rightColX + 115, 75, 40, 20, Component.literal("Damage Cooldown"));
            this.damageCooldownField.setValue(String.format(java.util.Locale.US, "%.1f", LavaConfig.damageCooldown));
            this.addRenderableWidget(this.damageCooldownField);

            // Lava Sizzling Toggle
            this.addRenderableWidget(Button.builder(
                Component.literal("§eLava Sizzling: " + (LavaConfig.enablePlayerLavaInteraction ? "§aON" : "§cOFF")),
                (btn) -> {
                    LavaConfig.enablePlayerLavaInteraction = !LavaConfig.enablePlayerLavaInteraction;
                    btn.setMessage(Component.literal("§eLava Sizzling: " + (LavaConfig.enablePlayerLavaInteraction ? "§aON" : "§cOFF")));
                }
            ).bounds(leftColX, 105, btnWidth, 20)
             .tooltip(Tooltip.create(Component.literal("Spawns dense steam, smoke, and sizzling pops when players are submerged in lava.")))
             .build());
        }

        // Reset to Defaults Button (Global optimal reset)
        this.addRenderableWidget(Button.builder(Component.literal("§c§lReset Defaults"), (btn) -> {
            this.resetToDefaults();
        }).bounds(centerX - 125, 215, 120, 20)
          .tooltip(Tooltip.create(Component.literal("Reset all settings to their factory/optimal default values.")))
          .build());

        // Save & Close Button
        this.addRenderableWidget(Button.builder(Component.literal("§a§lSave & Close"), (btn) -> {
            parseActiveTabFields();
            LavaPlatform.saveConfig(
                LavaConfig.particleIntensity,
                LavaConfig.bubbleFrequency,
                LavaConfig.soundVolumeMultiplier,
                LavaConfig.enableLargeBubbles,
                LavaConfig.performanceMode,
                LavaConfig.overworldMultiplier,
                LavaConfig.enableEruptions,
                LavaConfig.testMode,
                LavaConfig.particlesDealDamage,
                LavaConfig.damageCooldown,
                LavaConfig.magmaFragmentStayTime,
                LavaConfig.enablePopSounds,
                LavaConfig.enableSmallBubblePopSounds,
                LavaConfig.enableRumbleSounds,
                LavaConfig.enablePlayerLavaInteraction,
                LavaConfig.enableRegularBubbles
            );
            this.onClose();
        }).bounds(centerX + 5, 215, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xD5100B0B);
        guiGraphics.drawCenteredString(this.font, "§c§l" + this.title.getString(), this.width / 2, 18, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
