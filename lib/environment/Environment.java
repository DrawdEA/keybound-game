package lib;

import java.io.IOException;

import javax.imageio.ImageIO;

public class Environment extends GameObject {
    GameCanvas gameCanvas;
    Tile[] tiles;

    public Environment(GameCanvas gc) {
        gameCanvas = gc;
        tiles = new Tile[3];

    }

    public void getTileImages() {
        try {
            tiles[0] = new Tile();
            tiles[0] = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/FarmLand_Tile.png"));


        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(tiles[0].image, 0, 0, 16, 16, null);
    }
}
