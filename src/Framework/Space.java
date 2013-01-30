package Framework;

import Bots.Move;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author Tom Pepels
 */
public class Space implements ActionListener {

    private final int shipSize = 14;
    private Level level;
    private ArrayList<SpaceShip> ships;
    private ArrayList<Projectile> projectiles;
    private Timer updateTimer;
    private final int updateInterval = 10;
    private final double resistance = 0.023;
    private final double bounceFactor = 1.3;
    private final double speedIncrease = 0.27;
    private final int fireInterval = 50;
    private final int respawnInterval = 200;
    private final int spawnDistance = 40;

    public Space(Level level) {
        this.level = level;
        this.ships = new ArrayList<SpaceShip>();
        this.projectiles = new ArrayList<Projectile>();
        updateTimer = new Timer(updateInterval, this);
        updateTimer.start();
    }

    public int addShip(SpaceShip ship) {
        ships.add(ship);
        int shipId = ships.indexOf(ship);
        spawnShip(shipId);
        return shipId;
    }

    public void spawnShip(int shipId) {
        double x = Math.random() * level.getWidth();
        if (x < shipSize) {
            x += 2 * shipSize;
        } else if (x - level.getWidth() < shipSize) {
            x -= 2 * shipSize;
        }
        double y = Math.random() * level.getHeight();
        if (y < shipSize) {
            y += 2 * shipSize;
        } else if (y - level.getHeight() < shipSize) {
            y -= 2 * shipSize;
        }

        for(int i = 0; i < ships.size(); i++) {
            SpaceShip s = ships.get(i);
            double dx = x - s.getX(), dy = y - s.getY();

            if (Math.sqrt(dx*dx + dy*dy) < spawnDistance) {
                if (level.getHeight() - y < spawnDistance) {
                    y += spawnDistance;
                } else {
                    y -= spawnDistance;
                }
            }
        }

        ships.get(shipId).setSpeed(0, 0);
        ships.get(shipId).setCoordinates(x, y);
        ships.get(shipId).setDead(false, 0);
    }

    public void increaseShipSpeed(double vx, double vy, int shipId) {
        if (Math.abs(vy) <= 1 || vy == 0 || Math.abs(vx) <= 1 || vx == 0) {
            ships.get(shipId).increaseSpeed(vx * speedIncrease, vy * speedIncrease);
            // System.out.println("Increased speed of ship " + shipId + " vx: " + vx + " vy: " + vy);
        }
    }

    public void fire(double xDir, double yDir, int shipId) {
        if (Math.sqrt(xDir * xDir + yDir * yDir) <= 1) { // Length of fire-vector cannot be larger than 1.
            //System.out.println("New projectile xDir: " + xDir + " yDir: " + yDir);
            if (ships.get(shipId).canFire()) {
                ships.get(shipId).setFireTimer(fireInterval);
                projectiles.add(new Projectile(shipId, xDir, yDir, ships.get(shipId).getX(), ships.get(shipId).getY()));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < ships.size(); i++) {
            if (!ships.get(i).isDead()) {

                if (ships.get(i).getFireTimer() > 0) {
                    ships.get(i).decreaseFireTimer();
                }

                wallCollision(ships.get(i));
                double dx = ships.get(i).getVx(), dy = ships.get(i).getVy();
                ships.get(i).increaseCoordinates(dx, dy);
                ships.get(i).setSpeed(dx * (1 - resistance), dy * (1 - resistance));
                //System.out.println("Moved ship " + i + " x: " + dx + " y: " + dy);

                for (int j = 0; j < projectiles.size(); j++) {
                    Projectile p = projectiles.get(j);
                    if (p.getPlayerId() != i) {
                        if (projectileCollision(p, ships.get(i))) {
                            ships.get(i).setDead(true, respawnInterval);
                            ships.get(p.getPlayerId()).increaseScore();
                            projectiles.remove(j);
                        }
                    }
                    double x = p.getX(), y = p.getY();
                    if (x < 0 || x > level.getWidth() || y < 0 || y > level.getHeight()) {
                        projectiles.remove(j);
                        //System.out.println("removed projectile.");
                    }
                }
            } else {
                if (ships.get(i).getRespawnTimer() > 0) {
                    ships.get(i).decreaseRespawnTimer();
                } else {
                    spawnShip(i);
                }
            }
        }

        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).increaseCoordinates();
        }

        for (int i = 0; i < ships.size(); i++) {
            if (!ships.get(i).isDead() && ships.get(i).getBot() != null) {
                Move move = ships.get(i).getBot().determineMove(this, i);
                if (move.move) {
                    increaseShipSpeed(move.moveX, move.moveY, i);
                }
                if (move.fire) {
                    fire(move.fireX, move.fireY, i);
                }
            }
        }
    }

    public boolean projectileCollision(Projectile p, SpaceShip ship) {
        double sx = ship.getX(), sy = ship.getY();
        double px = p.getX(), py = p.getY();
        double dx = sx - px, dy = sy - py;
        double D = Math.sqrt(dx * dx + dy * dy);

        if (D <= shipSize) {
            return true;
        }
        return false;
    }

    public void wallCollision(SpaceShip ship) {
        double x = ship.getX(), y = ship.getY();
        double vx = ship.getVx(), vy = ship.getVy();
        double h = level.getHeight(), w = level.getWidth();

        if (x <= shipSize / 2 || w - x <= shipSize/2) {
            if (x < shipSize/2) {
                ship.setCoordinates(shipSize/2, y);
            } else if (x > level.getWidth() - shipSize/2) {
                ship.setCoordinates(level.getWidth() - shipSize/2, y);
            }
            ship.setSpeed(-1.0 * bounceFactor * vx, vy);
        } else if (y <= shipSize / 2 || h - y <= shipSize/2) {
            if (y < shipSize /2) {
                ship.setCoordinates(x, shipSize/2);
            } else if (y > level.getHeight() - shipSize/2) {
                ship.setCoordinates(x, level.getHeight() - shipSize/2);               
            }
            ship.setSpeed(vx, -1.0 * bounceFactor * vy);
        }
    }

    /**
     * @return the ship
     */
    public SpaceShip[] getShips() {
        return ships.toArray(new SpaceShip[0]);
    }

    /**
     * @return the shipSize
     */
    public int getShipSize() {
        return shipSize;
    }

    /**
     * @return the projectiles
     */
    public Projectile[] getProjectiles() {
        if (projectiles.size() > 0) {
            return projectiles.toArray(new Projectile[0]);
        } else {
            return new Projectile[0];
        }
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(Level level) {
        this.level = level;
    }
}
