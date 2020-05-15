package ru.kdev.FarmKing.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.Crops;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;
import ru.kdev.FarmKing.scoreboard.FarmScoreboard;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class FarmListener implements Listener {
    private Main plugin;

    public FarmListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e ) throws UnsupportedEncodingException {
        Player player = e.getPlayer();
        Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        FarmScoreboard board = new FarmScoreboard(plugin, player);
        if(farm.exists()) {
            board.createScoreboard();
            board.updateScoreboard();
        } else {
            farm.generate();
            player.sendMessage(ChatColor.GREEN + "Создаем Вам ферму...");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                farm.teleport(player);
                try {
                    farm.saveFirst();
                    board.createScoreboard();
                    board.updateScoreboard();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                player.sendMessage(ChatColor.GREEN + "Ферма создана!");
            }, 120L);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) throws UnsupportedEncodingException {
        Player player = e.getPlayer();
        Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        if(isFarmable(e.getBlock())) {
            e.setCancelled(true);
            if(!farm.isInOwnFarm()) return;
            Integer level = Main.levels.get(e.getBlock().getType());
            if(farm.getLevel() < level) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Для этого действия нужен " + level + " уровень!"));
                return;
            }
            if(e.getBlock().getLocation().getBlockY() == 43 && (e.getBlock().getType() == Material.CACTUS || e.getBlock().getType() == Material.SUGAR_CANE_BLOCK))
                return;
            if(isSpecial(e.getBlock())) {
                Block block = e.getBlock();
                farm.breakBlock(block);
                if(e.getBlock().getType() != Material.LONG_GRASS) e.getBlock().setType(Material.AIR);
                Location location = e.getBlock().getLocation();
                location.setY(location.getY()-1.0);
                block = location.getBlock();
                if(block.getType() != Material.SAND && block.getType() != Material.GRASS && block.getType() != Material.CACTUS && block.getType() != Material.SUGAR_CANE_BLOCK) {
                    block.setType(Material.getMaterial(60));
                }
                return;
            }
            Crops crops = (Crops) e.getBlock().getState().getData();
            CropState cropState = crops.getState();
            if(cropState.equals(CropState.RIPE)) {
                e.getBlock().setData(CropState.SEEDED.getData());
                farm.breakBlock(e.getBlock());
            }
        } else {
            if(!e.getPlayer().hasPermission("farmking.build")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws UnsupportedEncodingException {
        Player player = event.getPlayer();
        Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            // If the block is farmland (soil)
            if (block.getType() == Material.SOIL) {
                // Deny event and set the block
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                event.setCancelled(true);
                block.setTypeIdAndData(block.getType().getId(), block.getData(), true);
            }
        }
        else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                if(sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Кликер")) {
                    if(farm.getLevel() < 8) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Для этого действия нужен 8 уровень!"));
                        return;
                    }
                    farm.set("balance", farm.getBalance() + 5000);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "+5000.0$"));
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(!e.getPlayer().hasPermission("farmking.build")) {
            e.setCancelled(true);
        }
    }

    private boolean isSpecial(Block block) {
        List<Material> materials = Arrays.asList(Material.MELON_BLOCK, Material.PUMPKIN, Material.SUGAR_CANE_BLOCK, Material.LONG_GRASS, Material.CACTUS);
        return materials.contains(block.getType());
    }

    private boolean isFarmable(Block block) {
        List<Material> materials = Arrays.asList(Material.CROPS, Material.POTATO, Material.CARROT, Material.MELON_BLOCK, Material.PUMPKIN, Material.BEETROOT_BLOCK, Material.SUGAR_CANE_BLOCK, Material.LONG_GRASS, Material.CACTUS);
        return materials.contains(block.getType());
    }
}
