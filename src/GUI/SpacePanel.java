package GUI;

import Framework.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Tom Pepels
 */
public class SpacePanel extends JPanel {

    private int width, height;
    private Space space;
    private Graphics bufferGraphics;
    private Image offscreen;
    private BufferedImage img;
    private final int playerId = 0;
    private final int borderSize = 2;

    public SpacePanel() {
        super();
    }

    public void init(Space space) {
        this.space = space;
        this.width = space.getLevel().getWidth();
        this.height = space.getLevel().getHeight();
        this.setBounds(0, 0, width, height);
        offscreen = createImage(width, height);
        bufferGraphics = offscreen.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d;

        if (bufferGraphics != null) {
            g2d = (Graphics2D) bufferGraphics;
        } else {
            g2d = (Graphics2D) g;
        }

        // Clear everything that was drawn previously.
        g2d.clearRect(0, 0, width, height);
        g2d.drawImage(img, 0, 0, this);
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int shipSize = space.getShipSize();
        Font f = new Font("Dialog", Font.PLAIN, 10);
        g2d.setFont(f);

        for (int i = 0; i < space.getShips().length; i++) {
            SpaceShip ship = space.getShips()[i];
            if (!ship.isDead()) {
                double x = ship.getX(), y = ship.getY();
                g2d.setColor(ship.getColor());
                g2d.drawString(ship.getName(), (int) x + shipSize - 10, (int) y + shipSize);
                g2d.drawString("kills: " + ship.getScore(), (int) x - 35, (int) y - shipSize + 10);
                g2d.drawOval((int) Math.round(x + borderSize) - shipSize / 2,
                        (int) Math.round(y + borderSize) - shipSize / 2, shipSize, shipSize);
            }
        }

        for (int i = 0; i < space.getProjectiles().length; i++) {
            Projectile proj = space.getProjectiles()[i];
            g2d.setColor(space.getShips()[proj.getPlayerId()].getColor());
            g2d.fillOval((int) Math.round(proj.getX() - Projectile.width / 2 + borderSize),
                    (int) Math.round(proj.getY() - Projectile.height / 2 + borderSize),
                    Projectile.width, Projectile.height);
        }

        g2d.setColor(Color.BLACK);
        for (int i = 0; i <= borderSize; i++) {
            g2d.drawRect(i, i, width - 2 * i - 1, height - 2 * i - 1);
        }

        g.drawImage(offscreen, 0, 0, this);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        return getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getPreferredSize());
    }
}
