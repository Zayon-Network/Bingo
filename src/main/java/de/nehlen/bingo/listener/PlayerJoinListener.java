package de.nehlen.bingo.listener;

import de.nehlen.bingo.Bingo;
import de.nehlen.bingo.countdowns.LobbyCountdown;
import de.nehlen.bingo.data.GameData;
import de.nehlen.bingo.data.GameState;
import de.nehlen.bingo.data.StringData;
import de.nehlen.bingo.util.Items;
import io.sentry.Sentry;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;

public class PlayerJoinListener implements Listener {

    private final Bingo bingo;

    public PlayerJoinListener(Bingo bingo) {
        this.bingo = bingo;
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();

            player.getInventory().clear();
            player.setHealthScale(20D);
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            event.setJoinMessage("");

            this.bingo.getUserFactory().createUser(player);
            Bukkit.getOnlinePlayers().forEach(this.bingo.getScoreboardManager()::setUserScoreboard);

            if (GameState.state == GameState.LOBBY) {

//            player.teleport(GameData.getLobbyLocation());
                Bukkit.broadcastMessage(StringData.getPrefix() + StringData.getHighlightColor() + player.getDisplayName() + " §7hat das Spiel betreten.");
                player.getInventory().setItem(8, Items.createItem(Material.HEART_OF_THE_SEA, "§7Zurück zur Lobby", 1));
                player.getInventory().setItem(0, Items.createItem(Material.TOTEM_OF_UNDYING, "§7Teamauswahl", 1));
                player.updateInventory();

                ArrayList<Player> playerList = GameData.getIngame();
                playerList.add(player);
                GameData.setIngame(playerList);

                if (Bukkit.getOnlinePlayers().size() == 3) {
                    LobbyCountdown.startLobbyCountdown(false);
                }
                if (Bukkit.getOnlinePlayers().size() == (GameData.getTeamAmount() * GameData.getTeamSize())/2) {
                    if(LobbyCountdown.counter >= 60) {
                        LobbyCountdown.counter = 60;
                    }
                }
                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.showPlayer(Bingo.getBingo(), player);
                }
            } else if (GameState.state == GameState.INGAME) {
                player.setGameMode(GameMode.SURVIVAL);
                player.setFlying(true);
                player.setAllowFlight(true);
                player.getInventory().setItem(8, Items.createItem(Material.HEART_OF_THE_SEA, "§7Zurück zur Lobby", 1));
                player.getInventory().setItem(0, Items.createItem(Material.COMPASS, "§7Spieler", 1));

                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.hidePlayer(Bingo.getBingo(), player);
                }
                player.sendMessage("");
                player.sendMessage("§7Diese Runde kannst du nur noch Zuschauen!");
                player.sendMessage("§7Nutze um zu den Kompass um dich zu einem Spieler zu teleportieren.");
            }
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }

    @EventHandler
    public void handleSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        Location location = GameData.getLobbyLocation();
        event.setSpawnLocation(location);
    }
}
