package Framework;

/**
 *
 * @author Tom Pepels
 */
public class Game {

    private Space space;
    private int playerId;

    public Game() {
        space = new Space(new Level());
    }

    public int addPlayer(boolean human, SpaceShip ship) {
        if (human) {
            playerId = space.addShip(ship);
            return playerId;
        } else {
            return space.addShip(ship);
        }
    }

    public void increaseSpeed(double vx, double vy, int shipId) {
        space.increaseShipSpeed(vx, vy, shipId);        
    }

    public void fire(double xDir, double yDir, int shipId) {
        space.fire(xDir, yDir, shipId);
    }

    public SpaceShip getPlayerShip() {
        return space.getShips()[playerId];
    }

    /**
     * @return the space
     */
    public Space getSpace() {
        return space;
    }
}
