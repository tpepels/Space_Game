package Bots;

import Framework.*;

/**
 *
 * @author Tom Pepels
 */
public class TomBot implements Bot {

    private final int dodgeDistance = 200;
    private final double maxRandomDodge = 0.2;
    private int shipId;
    private Space space;

    public Move determineMove(Space space, int shipId) {
        this.space = space;
        this.shipId = shipId;
        Move move = new Move();
        SpaceShip s = space.getShips()[shipId];

        for (int i = 0; i < space.getProjectiles().length; i++) {
            Projectile p = space.getProjectiles()[i];

            if (p.getPlayerId() == shipId) {
                continue;
            }

            double closest = 10000;
            if (detectDynamicDynamic(p, s)) { // Bullet will hit man!!
                double x = p.getX() - s.getX();
                double y = p.getY() - s.getY();
                double length = Math.sqrt(x * x + y * y);
                if (length < closest) {
                    closest = length;
                    double dirx = -1.0 * (y / length);
                    double diry = (x / length);
                    double randXDir = dirx * Math.random() * maxRandomDodge;
                    double randYDir = diry * Math.random() * maxRandomDodge;
                    length = Math.sqrt(randXDir * randXDir + randYDir * randYDir);
                    move.move = true;
                    move.moveX = randXDir / length;
                    move.moveY = randYDir / length;
                    //System.out.println("moving: xdir " + xDir + " ydir " + yDir);
                }
            }
        }

        if (s.canFire()) {
            double nearestL = 1000009; // distance to the nearest enemy
            double nearestX = 0, nearestY = 0;
            for (int i = 0; i < space.getShips().length; i++) {
                if (i == shipId) {
                    continue;
                }

                SpaceShip enemy = space.getShips()[i];
                if (enemy.isDead()) {
                    continue;
                }
                double dx = enemy.getX() - s.getX();
                double dy = enemy.getY() - s.getY();
                double D = dx * dx + dy * dy;
                if (D < nearestL) {
                    nearestL = D;
                    nearestX = dx;
                    nearestY = dy;
                }
            }

            if (nearestL != 1000009) {
                double lengthV = Math.sqrt(nearestL);
                move.fire = true;
                move.fireX = nearestX / lengthV;
                move.fireY = nearestY / lengthV;
                // System.out.println("firing : xdir " + move.fireX + " ydir " + move.fireY);
            }
        }

        return move;
    }

    public boolean detectDynamicDynamic(Projectile p, SpaceShip ship) {
        // Source: http://www.gamasutra.com/view/feature/3015/pool_hall_lessons_fast_accurate_.php?page=1

        // Calculate the length of the trajectory vectors (VX, VY) for both balls.
        double lengthI = Math.sqrt(Math.pow(ship.getVx(), 2) + Math.pow(ship.getVy(), 2));
        double lengthJ = Math.sqrt(Math.pow(p.getVx(), 2) + Math.pow(p.getVy(), 2));

        // Vector C from the center of the first to the second ball.
        double[] C = {ship.getX() - p.getX(), ship.getY() - p.getY()};
        // The length of vector C is the distance between the centre of ball i and ball j
        double lengthC = Math.sqrt(Math.pow(C[0], 2) + Math.pow(C[1], 2));

        // Substract one vector from the other to get the relative movementvector.
        double[] movementV = {(p.getVx() - ship.getVx()), (p.getVy() - ship.getVy())};

        // The length of the relative movement vector.
        double lengthV = Math.sqrt(Math.pow(movementV[0], 2) + Math.pow(movementV[1], 2));
        //double lengthV = Math.abs(lengthI - lengthJ);

        // Early Escape test: if the length of the movevec is less
        // than distance between the centers of these circles minus
        // their radii, there's no way they can hit.
        double sumRadii = space.getShipSize() * 2 + Projectile.width;

        if (lengthV * dodgeDistance < lengthC - sumRadii) {
            return false;
        }

        double[] NI = new double[2];
        // Calculate the normalized trajectory vector. (length 1)
        NI[0] = movementV[0] / lengthV;
        NI[1] = movementV[1] / lengthV;

        // D, the distance between the center of ball[i] and the closest point on the trajectory to ball[j]
        double D = (C[0] * NI[0]) + (C[1] * NI[1]);

        // Another early escape: Make sure that A is moving
        // towards B! If the dot product between the movevec and
        // B.center - A.center is less that or equal to 0,
        // A isn't isn't moving towards B
        if (D <= 0) {
            return false;
        }

        double F = Math.pow(lengthC, 2) - Math.pow(D, 2);
        double radius = Math.pow(sumRadii, 2);

        if (F >= radius) {
            return false;
        }


        // Balls may collide.
        double T = radius - F; // the location where the centre of ball[i] will hit ball[j]

        // If there is no such right triangle with sides length of
        // sumRadii and sqrt(f), T will probably be less than 0.
        // Better to check now than perform a square root of a
        // negative number.
        if (T < 0) {
            return false;
        }

        double maxD = D - Math.sqrt(T); // The distance a can travel before contacting B.

        if (lengthV * dodgeDistance < maxD) {
            return false;
        }

        return true;
    }
}
