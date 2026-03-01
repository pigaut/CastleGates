package io.github.pigaut.castlegates.util;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;

public class GateTool {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    private static final NamespacedKey GATE_TOOL_KEY = plugin.getNamespacedKey("gate_tool");
    private static final NamespacedKey GATE_NAME_KEY = plugin.getNamespacedKey("gate_name");
    private static final NamespacedKey GATE_ROTATION_KEY = plugin.getNamespacedKey("gate_rotation");
    private static final ItemStack ITEM_TEMPLATE;

    static {
        ITEM_TEMPLATE = IconBuilder.of(Material.TERRACOTTA)
                .enchanted(true)
                .name("&b&l{gate_tc} Gate ({gate_rotation_tc})")
                .addLine("&fright-click to place")
                .addLine("&fleft-click to break")
                .addLine("&fshift + left-click (air) to rotate")
                .buildIcon();
    }

    public static @NotNull ItemStack getItemTemplate() {
        return ITEM_TEMPLATE.clone();
    }

    public static @NotNull ItemStack createItem(@NotNull GateTemplate gate) {
        ItemStack gateItem = plugin.getSettings().getGateTool();
        gateItem.setType(gate.getItemType());

        ItemMeta meta = gateItem.getItemMeta();
        updateToolData(meta, gate, "NONE");
        gateItem.setItemMeta(meta);

        Placeholder rotationPlaceholder = Placeholder.of("{gate_tool_rotation}", "NONE");
        return ItemPlaceholders.parseAll(gateItem, gate, rotationPlaceholder);
    }

    public static boolean isValidItem(@NotNull ItemStack item) {
        return item.hasItemMeta() && PersistentData.hasTag(item.getItemMeta(), GATE_TOOL_KEY);
    }

    public static @Nullable GateTemplate getGateTemplate(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        String gateName = PersistentData.getString(item.getItemMeta(), GATE_NAME_KEY);
        return gateName != null ? plugin.getGateTemplate(gateName) : null;
    }

    public static @Nullable String getRotationData(@NotNull ItemStack item) {
        return item.hasItemMeta() ? PersistentData.getString(item.getItemMeta(), GATE_ROTATION_KEY) : null;
    }

    public static @Nullable Rotation getRotation(@NotNull ItemStack item) {
        String rotationName = getRotationData(item);
        if (rotationName == null) {
            return null;
        }
        if (rotationName.equalsIgnoreCase("RANDOM")) {
            return Rotation.random();
        }
        return ParseUtil.parseEnumOrNull(Rotation.class, rotationName);
    }

    public static void switchToolRotation(@NotNull ItemStack item) {
        Preconditions.checkArgument(isValidItem(item), "Item is not a gate tool");

        String currentRotationName = getRotationData(item);
        String newRotation = "NONE";
        if (currentRotationName != null) {
            switch (currentRotationName) {
                case "NONE" -> newRotation = "RIGHT";
                case "RIGHT" -> newRotation = "BACK";
                case "BACK" -> newRotation = "LEFT";
                case "LEFT" -> newRotation = "RANDOM";
                case "RANDOM" -> newRotation = "NONE";
            }
        }

        ItemMeta meta = plugin.getSettings().getGateTool().getItemMeta();
        GateTemplate gate = getGateTemplate(item);
        updateToolData(meta, gate, newRotation);
        item.setItemMeta(meta);

        Placeholder rotationPlaceholder = Placeholder.of("{gate_tool_rotation}", newRotation.toString());
        ItemPlaceholders.parseAll(item, gate, rotationPlaceholder);
    }

    private static void updateToolData(@NotNull ItemMeta meta, @NotNull GateTemplate gate, @NotNull String rotationData) {
        PersistentData.setTag(meta, GATE_TOOL_KEY);
        PersistentData.setString(meta, GATE_NAME_KEY, gate.getName());
        PersistentData.setString(meta, GATE_ROTATION_KEY, rotationData);
    }

}
