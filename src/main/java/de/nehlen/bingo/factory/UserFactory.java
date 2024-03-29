package de.nehlen.bingo.factory;

import de.nehlen.bingo.Bingo;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserFactory {
    private final Bingo bingo;

    public UserFactory(Bingo bingo) {
        this.bingo = bingo;
    }

    public void createTable() {
        StringBuilder table = new StringBuilder();
        table.append("id INT(11) NOT NULL AUTO_INCREMENT, ");
        table.append("`uuid` VARCHAR(64) NOT NULL UNIQUE, ");
        table.append("`kills` INT(11) NOT NULL, ");
        table.append("`deaths` INT(11) NOT NULL, ");
        table.append("`games` INT(11) NOT NULL, ");
        table.append("`wins` INT(11) NOT NULL, ");
        table.append("`brownSheeps` INT(11) NOT NULL, ");
        table.append("PRIMARY KEY (`id`)");
        this.bingo.getDatabaseLib().executeUpdateAsync("CREATE TABLE IF NOT EXISTS bingo_stats (" + table.toString() + ")", resultSet -> {});
    }

    public CompletableFuture<Boolean> userExists(Player player) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        this.bingo.getDatabaseLib().executeQueryAsync("SELECT id FROM bingo_stats WHERE uuid = ?", resultSet -> {
            try {
                completableFuture.complete(Boolean.valueOf(resultSet.next()));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, player.getUniqueId().toString());
        return completableFuture;
    }

    public void createUser(Player player) {
        userExists(player).whenCompleteAsync((exist, throwable) -> {
            if (throwable == null && !exist.booleanValue())
                this.bingo.getDatabaseLib().executeUpdateAsync("INSERT INTO bingo_stats (uuid, kills, deaths, games, wins, brownSheeps) VALUES (?, ?, ?, ?, ?, ?)", resultSet -> {}, player.getUniqueId().toString(), 0, 0, 0, 0, 0);
        });
    }

    public int getKills(Player player) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT kills FROM bingo_stats WHERE uuid = ?", player.getUniqueId().toString(), "kills");
    }

    public int getDeaths(Player player) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT deaths FROM bingo_stats WHERE uuid = ?", player.getUniqueId().toString(), "deaths");
    }

    public int getWins(Player player) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT wins FROM bingo_stats WHERE uuid = ?", player.getUniqueId().toString(), "wins");
    }
    public int getWins(UUID uuid) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT wins FROM bingo_stats WHERE uuid = ?", uuid.toString(), "wins");
    }


    public int getGames(Player player) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT games FROM bingo_stats WHERE uuid = ?", player.getUniqueId().toString(), "games");
    }

    public int getGames(UUID uuid) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT games FROM bingo_stats WHERE uuid = ?", uuid.toString(), "games");
    }

    public int getBrownSheeps(Player player) {
        return (Integer) this.bingo.getDatabaseLib().get("SELECT brownSheeps FROM bingo_stats WHERE uuid = ?", player.getUniqueId().toString(), "brownSheeps");
    }

    public void updateKills(Player player, UpdateType updateType, int kills) {
        int newKills = 0;
        if (updateType == UpdateType.ADD) {
            newKills = getKills(player) + kills;
        } else if (updateType == UpdateType.REMOVE) {
            newKills = getKills(player) - kills;
        }
        this.bingo.getDatabaseLib().executeUpdateAsync("UPDATE bingo_stats SET kills = ? WHERE uuid = ?", resultSet -> {},newKills, player.getUniqueId().toString() );
    }

    public void updateDeaths(Player player, UpdateType updateType, int deaths) {
        int newDeaths = 0;
        if (updateType == UpdateType.ADD) {
            newDeaths = getDeaths(player) + deaths;
        } else if (updateType == UpdateType.REMOVE) {
            newDeaths = getDeaths(player) - deaths;
        }
        this.bingo.getDatabaseLib().executeUpdateAsync("UPDATE bingo_stats SET deaths = ? WHERE uuid = ?", resultSet -> {}, newDeaths, player.getUniqueId().toString());
    }

    public void updateWins(Player player, UpdateType updateType, int wins) {
        int newWins = 0;
        if (updateType == UpdateType.ADD) {
            newWins = getWins(player) + wins;
        } else if (updateType == UpdateType.REMOVE) {
            newWins = getWins(player) - wins;
        }
        this.bingo.getDatabaseLib().executeUpdateAsync("UPDATE bingo_stats SET wins = ? WHERE uuid = ?", resultSet -> {},newWins, player.getUniqueId().toString() );
    }

    public void updateGames(Player player, UpdateType updateType, int games) {
        int newGames = 0;
        if (updateType == UpdateType.ADD) {
            newGames = getGames(player) + games;
        } else if (updateType == UpdateType.REMOVE) {
            newGames = getGames(player) - games;
        }
        this.bingo.getDatabaseLib().executeUpdateAsync("UPDATE bingo_stats SET games = ? WHERE uuid = ?", resultSet -> {},newGames, player.getUniqueId().toString() );
    }

    public void updateBrownSheeps(Player player, UpdateType updateType, int sheeps) {
        int newSheeps = 0;
        if (updateType == UpdateType.ADD) {
            newSheeps = getBrownSheeps(player) + sheeps;
        } else if (updateType == UpdateType.REMOVE) {
            newSheeps = getBrownSheeps(player) - sheeps;
        }
        this.bingo.getDatabaseLib().executeUpdateAsync("UPDATE bingo_stats SET brownSheeps = ? WHERE uuid = ?", resultSet -> {},newSheeps, player.getUniqueId().toString() );
    }

    public String getKDRatio(Player player) {
        double kd = (double) getKills(player) / (double) getDeaths(player);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(kd);
    }


    public enum UpdateType {
        ADD, REMOVE;
    }
}
