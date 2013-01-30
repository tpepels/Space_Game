package Framework;

/**
 *
 * @author Tom Pepels
 */
public class Projectile {

    public static double speed = 2.;
    public static int width = 4, height = 4;
    private int playerId;
    private double vx, vy;
    private double x, y;

    public Projectile(int playerId, double vx, double vy, double x, double y) {
        this.x = x;
        this.y = y;        
        this.vx = vx * speed;
        this.vy = vy * speed;
        this.playerId = playerId;
    }

    public void increaseCoordinates() {
        this.x += speed * vx;
        this.y += speed * vy;
    }

    /**
     * @return the playerId
     */
    public int getPlayerId() {
        return playerId;
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
}
