package ru.kdev.FarmKing.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Level implements InventoryProvider {
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("myInventory")
            .provider(new Level())
            .size(1, 9)
            .title(ChatColor.GREEN + "Уровень")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE)));

        Main plugin = Main.getPlugin(Main.class);

        try {
            Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));

            ItemStack bottle = new ItemStack(Material.EXP_BOTTLE);
            ItemMeta meta = bottle.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Повысить уровень");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "След. уровень: " + ChatColor.GREEN + (farm.getLevel() + 1),
                    ChatColor.GRAY + "Цена: " + ChatColor.GREEN + Main.lvlPrice.get(farm.getLevel() + 1) + "$"));
            bottle.setItemMeta(meta);

            contents.set(0, 4, ClickableItem.of(bottle, e -> {
                try {
                    if(farm.getBalance() >= Main.lvlPrice.get(farm.getLevel() + 1)) {
                        farm.set("balance", farm.getBalance() - Main.lvlPrice.get(farm.getLevel() + 1));
                        farm.set("level", farm.getLevel() + 1);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Ваш уровень повышен!"));
                        player.closeInventory();
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(org.bukkit.ChatColor.RED + "У Вас недостаточно денег!"));
                    }
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
