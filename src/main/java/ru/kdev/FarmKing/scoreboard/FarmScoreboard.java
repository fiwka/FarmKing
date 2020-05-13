package ru.kdev.FarmKing.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FarmScoreboard {
    private Player player;
    private Scoreboard board;
    private static Map<Player, ArrayList<Team>> rows = new HashMap<>();
    private Main plugin;

    public FarmScoreboard(Main plugin,Player player) {
        this.player = player;
        this.plugin = plugin;
    }

    public void createScoreboard() throws UnsupportedEncodingException {
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective;
        if(this.board.getObjective(player.getName()) != null) {
            objective = this.board.getObjective(player.getName());
        } else {
            objective = this.board.registerNewObjective(player.getName(), "dummy");
        }
        Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        objective.setDisplayName(ChatColor.GREEN + "   Farm King   ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        try {
            ArrayList<Team> arrayList = new ArrayList<>();
            arrayList.add(addRow("balance", "  §fБаланс: §b" + farm.getBalance() + "$", ChatColor.BLACK + ""));
            arrayList.add(addRow("level", "  §fУровень: §b" + farm.getLevel(), ChatColor.BLUE + ""));
            arrayList.add(addRow("collected", "  §fСобрано: §b" + farm.getCollected(), ChatColor.YELLOW + ""));
            arrayList.add(addRow("online", "§fОнлайн: §b" + Bukkit.getOnlinePlayers().size(), ChatColor.DARK_AQUA + ""));
            rows.put(player, arrayList);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        objective.getScore("").setScore(20);
        objective.getScore(" §bПрофиль").setScore(19);
        objective.getScore(ChatColor.BLACK + "").setScore(18);
        objective.getScore(ChatColor.BLUE + "").setScore(17);
        objective.getScore(ChatColor.YELLOW + "").setScore(16);
        objective.getScore(" ").setScore(15);
        objective.getScore(ChatColor.DARK_AQUA + "").setScore(14);
        objective.getScore("  ").setScore(13);
        objective.getScore("   www.cristalix.ru   ").setScore(12);
        player.setScoreboard(this.board);
    }

    public void updateScoreboard() throws UnsupportedEncodingException {
        Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            rows.forEach((p, v) -> {
                if(Bukkit.getOnlinePlayers().contains(p)) {
                    try {
                        v.get(0).setPrefix("  §fБаланс: §b" + farm.getBalance() + "$");
                        v.get(1).setPrefix("  §fУровень: §b" + farm.getLevel());
                        v.get(2).setPrefix("  §fСобрано: §b" + farm.getCollected());
                        v.get(3).setPrefix("§fОнлайн: §b" + Bukkit.getOnlinePlayers().size());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }, 0L, 20L);
    }

    private Team addRow(String name, String text, String entry) {
        Team team;
        if(this.board.getTeam(name) != null) {
            team = this.board.getTeam(name);
        } else {
            team = this.board.registerNewTeam(name);
        }
        team.setPrefix(text);
        team.addEntry(entry);
        return team;
    }
}
