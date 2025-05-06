package lib.objects.spells;

import java.awt.Graphics2D;

import lib.render.CollisionManager;
public class WindSpell extends Spell {
    public WindSpell(int casterId, double x, double y, double width, double height) {
        super("WIND_SPELL", casterId, x, y, width, height);
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDataString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}