package src;
import java.util.ArrayList;
import java.awt.*;

public class Team {
    private ArrayList<Player> players;
    private Player selectedPlayer;
    private Color teamColor;

    public Team() {
        players = new ArrayList<>();
    }

    public Team(Color teamColor) {
        this.players = new ArrayList<>();
        this.teamColor = teamColor;
    }

    public void addPlayer(Player player) {
        players.add(player);
        if (selectedPlayer == null) {
            selectPlayer(player);
        }
    }

    public Player getSelectedPlayer() {
        return selectedPlayer;
    }

    public void selectPlayer(Player player) {
        if (selectedPlayer != null) {
            selectedPlayer.setSelected(false);
        }
        selectedPlayer = player;
        player.setSelected(true);
    }

    public void selectNextPlayer() {
        int currentIndex = players.indexOf(selectedPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        selectPlayer(players.get(nextIndex));
    }

    public void moveSelectedPlayer(double dx, double dy) {
        if (selectedPlayer != null) {
            selectedPlayer.move(dx, dy);
        }
    }

    public Player getNearestPlayerTo(Player source) {
        Player nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Player player : players) {
            if (player != source) {
                double dx = player.getX() - source.getX();
                double dy = player.getY() - source.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = player;
                }
            }
        }

        return nearest;
    }

    public void draw(Graphics g) {
        for (Player player : players) {
            player.draw(g);
        }
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }
}
