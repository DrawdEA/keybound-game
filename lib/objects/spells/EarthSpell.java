package lib.objects.spells;

import java.awt.Graphics2D;
public class EarthSpell extends Spell {
    public EarthSpell(int casterId, double x, double y, double width, double height) {
        super("EARTH_SPELL", casterId, x, y, width, height);
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
    public void onHit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}