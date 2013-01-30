package Framework;

import Bots.Bot;
import java.awt.Color;

/**
 *
 * @author Tom Pepels
 */
public class SpaceShip {

    private double vx, vy;
    private double x, y;
    private Color color;
    private String name;
    private boolean dead;
    private int score;
    private int respawnTimer;
    private int fireTimer;
    private Bot bot;

    public SpaceShip(Color color, String name) {
        this.name = name;
        this.color = color;
        this.dead = true;
        this.score = 0;
    }

    public void increaseCoordinates(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void increaseSpeed(double vx, double vy) {
        this.vx += vx;
        this.vy += vy;
    }

    public void setSpeed(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the vx
     */
    public double getVx() {
        return vx;
    }

    /**
     * @return the vy
     */
    public double getVy() {
        return vy;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @param dead the dead to set
     */
    public void setDead(boolean dead, int respawnTimer) {
        this.dead = dead;
        this.respawnTimer = respawnTimer;        
        if (dead) {
            fireTimer = 0;
            vy = 0;
            vx = 0;
        }
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void increaseScore() {
        this.score++;
    }

    public void decreaseRespawnTimer() {
        respawnTimer--;
    }

    public void decreaseFireTimer() {
        fireTimer--;
    }

    /**
     * @return the respawnTimer
     */
    public int getRespawnTimer() {
        return respawnTimer;
    }

    /**
     * @return the fireTimer
     */
    public int getFireTimer() {
        return fireTimer;
    }

    public boolean canFire() {
        return fireTimer == 0 && !dead;
    }

    /**
     * @param fireTimer the fireTimer to set
     */
    public void setFireTimer(int fireTimer) {
        this.fireTimer = fireTimer;
    }

    /**
     * @return the bot
     */
    public Bot getBot() {
        if (bot != null) {
            return bot;
        } else {
            return null;
        }
    }

    /**
     * @param bot the bot to set
     */
    public void setBot(Bot bot) {
        this.bot = bot;
    }
}
