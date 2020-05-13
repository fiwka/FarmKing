package ru.kdev.FarmKing.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
    public void onBreak(BlockBreakEvent e) {
        if(isFarmable(e.getBlock())) {
            e.setCancelled(true);
            if(e.getBlock().getLocation().getBlockY() == 43 && e.getBlock().getType() == Material.CACTUS || e.getBlock().getType() == Material.SUGAR_CANE_BLOCK)
                return;
            if(isSpecial(e.getBlock())) {
                if(e.getBlock().getType() != Material.LONG_GRASS) e.getBlock().setType(Material.AIR);
                Location location = e.getBlock().getLocation();
                location.setY(location.getY()-1.0);
                Block block = location.getBlock();
                if(block.getType() != Material.SAND && block.getType() != Material.GRASS) {
                    block.setType(Material.getMaterial(60));
                }
                return;
            }
            Crops crops = (Crops) e.getBlock().getState().getData();
            CropState cropState = crops.getState();
            if(cropState.equals(CropState.RIPE)) {
                e.getBlock().setData(CropState.SEEDED.getData());
            }
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
