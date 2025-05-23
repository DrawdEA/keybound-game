/**
 * The Environment class is responsible for generating the environment that the player is walking in.
 * This includes generating the map through a .txt file, and loading in the tiles and setting their collisions booleans.
 * 
 * @author Edward Joshua M. Diesta (241571), Charles Joshua T. Uy (244644)
 * @version May 20, 2025
 * 
 * We have not discussed the Java language code in our program 
 * with anyone other than our instructor or the teaching assistants 
 * assigned to this course.
 * 
 * We have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in our program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of our program.
 */
package lib.objects;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import lib.*;
import lib.render.*;

public class Environment extends GameObject {
    private GameCanvas gameCanvas;
    private Tile[] tiles;
    private int[][] mapNumbers;

    /**
     * Generate a map of the game.
     * 
     * @param x x position of the map
     * @param y y position of the map
     * @param gc the GameCanvas of the client
     */
    public Environment(int x, int y, GameCanvas gc) {
        super("ENVIRONMENT", x, y, 0, 0);
        gameCanvas = gc;
        tiles = new Tile[100];
        mapNumbers = new int[GameConfig.MAX_WORLD_COLUMNS][GameConfig.MAX_WORLD_ROWS];

        // Load the map.
        try {
            InputStream is = getClass().getResourceAsStream("/resources/maps/forgottenIslands.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            System.out.println("SETTING UP MAP");

            for (int j = 0; j < GameConfig.MAX_WORLD_ROWS; j++) {
                String line = br.readLine();
                
                String numbers[] = line.split(" ");

                for (int i = 0; i < GameConfig.MAX_WORLD_COLUMNS; i++) {
                    mapNumbers[i][j] = Integer.parseInt(numbers[i]);
                }
            }

            System.out.println("SETUP DONE");
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        // Generate the tiles.
        try {
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Grass_Middle.png"));
            tiles[0].canCollide = false;

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Water_Middle.png"));
            tiles[1].canCollide = false;

            BufferedImage pathTiles = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Path_Tile.png"));

            // Inner Grass-Path tiles
            tiles[2] = new Tile();
            tiles[2].image = pathTiles.getSubimage(0, 0, 16, 16);
            tiles[2].canCollide = false;

            tiles[3] = new Tile();
            tiles[3].image = pathTiles.getSubimage(16, 0, 16, 16);
            tiles[3].canCollide = false;

            tiles[4] = new Tile();
            tiles[4].image = pathTiles.getSubimage(32, 0, 16, 16);
            tiles[4].canCollide = false;

            tiles[5] = new Tile();
            tiles[5].image = pathTiles.getSubimage(0, 16, 16, 16);
            tiles[5].canCollide = false;

            tiles[6] = new Tile();
            tiles[6].image = pathTiles.getSubimage(16, 16, 16, 16);
            tiles[6].canCollide = false;

            tiles[7] = new Tile();
            tiles[7].image = pathTiles.getSubimage(32, 16, 16, 16);
            tiles[7].canCollide = false;

            tiles[8] = new Tile();
            tiles[8].image = pathTiles.getSubimage(0, 32, 16, 16);
            tiles[8].canCollide = false;

            tiles[9] = new Tile();
            tiles[9].image = pathTiles.getSubimage(16, 32, 16, 16);
            tiles[9].canCollide = false;

            tiles[10] = new Tile();
            tiles[10].image = pathTiles.getSubimage(32, 32, 16, 16);
            tiles[10].canCollide = false;

            // Outer Grass-Path tiles
            tiles[11] = new Tile();
            tiles[11].image = pathTiles.getSubimage(0, 48, 16, 16);
            tiles[11].canCollide = false;

            tiles[12] = new Tile();
            tiles[12].image = pathTiles.getSubimage(16, 48, 16, 16);
            tiles[12].canCollide = false;

            tiles[13] = new Tile();
            tiles[13].image = pathTiles.getSubimage(0, 64, 16, 16);
            tiles[13].canCollide = false;

            tiles[14] = new Tile();
            tiles[14].image = pathTiles.getSubimage(16, 64, 16, 16);
            tiles[14].canCollide = false;

            // Path footprints
            tiles[15] = new Tile();
            tiles[15].image = pathTiles.getSubimage(0, 80, 16, 16);
            tiles[15].canCollide = false;

            tiles[16] = new Tile();
            tiles[16].image = pathTiles.getSubimage(16, 80, 16, 16);
            tiles[16].canCollide = false;

            tiles[17] = new Tile();
            tiles[17].image = pathTiles.getSubimage(16, 80, 16, 16);
            tiles[17].canCollide = false;

            BufferedImage cliffTiles = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Cliff_Tile.png"));

            // Inner Cliff tiles
            tiles[18] = new Tile();
            tiles[18].image = cliffTiles.getSubimage(0, 0, 16, 16);

            tiles[19] = new Tile();
            tiles[19].image = cliffTiles.getSubimage(16, 0, 16, 16);

            tiles[20] = new Tile();
            tiles[20].image = cliffTiles.getSubimage(32, 0, 16, 16);

            tiles[21] = new Tile();
            tiles[21].image = cliffTiles.getSubimage(0, 16, 16, 16);

            tiles[22] = new Tile();
            tiles[22].image = cliffTiles.getSubimage(32, 16, 16, 16);

            tiles[23] = new Tile();
            tiles[23].image = cliffTiles.getSubimage(0, 32, 16, 16);

            tiles[24] = new Tile();
            tiles[24].image = cliffTiles.getSubimage(16, 32, 16, 16);

            tiles[25] = new Tile();
            tiles[25].image = cliffTiles.getSubimage(32, 32, 16, 16);

            // Outer Cliff tiles
            tiles[26] = new Tile();
            tiles[26].image = cliffTiles.getSubimage(0, 48, 16, 16);

            tiles[27] = new Tile();
            tiles[27].image = cliffTiles.getSubimage(16, 48, 16, 16);

            tiles[28] = new Tile();
            tiles[28].image = cliffTiles.getSubimage(0, 64, 16, 16);

            tiles[29] = new Tile();
            tiles[29].image = cliffTiles.getSubimage(16, 64, 16, 16);

            // Grass footprints
            tiles[30] = new Tile();
            tiles[30].image = cliffTiles.getSubimage(0, 80, 16, 16);
            tiles[30].canCollide = false;

            tiles[31] = new Tile();
            tiles[31].image = cliffTiles.getSubimage(16, 80, 16, 16);
            tiles[31].canCollide = false;

            tiles[32] = new Tile();
            tiles[32].image = cliffTiles.getSubimage(16, 80, 16, 16);
            tiles[32].canCollide = false;

            BufferedImage waterTiles = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Water_Tile.png"));

            // Outer water tiles
            tiles[33] = new Tile();
            tiles[33].image = waterTiles.getSubimage(0, 0, 16, 16);

            tiles[34] = new Tile();
            tiles[34].image = waterTiles.getSubimage(16, 0, 16, 16);

            tiles[35] = new Tile();
            tiles[35].image = waterTiles.getSubimage(32, 0, 16, 16);

            tiles[36] = new Tile();
            tiles[36].image = waterTiles.getSubimage(0, 16, 16, 16);

            tiles[37] = new Tile();
            tiles[37].image = waterTiles.getSubimage(32, 16, 16, 16);

            tiles[38] = new Tile();
            tiles[38].image = waterTiles.getSubimage(0, 32, 16, 16);

            tiles[39] = new Tile();
            tiles[39].image = waterTiles.getSubimage(16, 32, 16, 16);

            tiles[40] = new Tile();
            tiles[40].image = waterTiles.getSubimage(32, 32, 16, 16);

            // Inner water tiles
            tiles[41] = new Tile();
            tiles[41].image = waterTiles.getSubimage(0, 48, 16, 16);

            tiles[42] = new Tile();
            tiles[42].image = waterTiles.getSubimage(16, 48, 16, 16);

            tiles[43] = new Tile();
            tiles[43].image = waterTiles.getSubimage(0, 64, 16, 16);

            tiles[44] = new Tile();
            tiles[44].image = waterTiles.getSubimage(16, 64, 16, 16);

            // Water details
            tiles[45] = new Tile();
            tiles[45].image = waterTiles.getSubimage(0, 80, 16, 16);

            tiles[46] = new Tile();
            tiles[46].image = waterTiles.getSubimage(16, 80, 16, 16);

            tiles[47] = new Tile();
            tiles[47].image = waterTiles.getSubimage(16, 80, 16, 16);

            BufferedImage beachTiles = ImageIO.read(getClass().getResourceAsStream("/resources/tiles/Beach_Tile.png"));

            // Inner beach tiles
            tiles[48] = new Tile();
            tiles[48].image = beachTiles.getSubimage(0, 0, 16, 16);

            tiles[49] = new Tile();
            tiles[49].image = beachTiles.getSubimage(16, 0, 16, 16);

            tiles[50] = new Tile();
            tiles[50].image = beachTiles.getSubimage(32, 0, 16, 16);

            tiles[51] = new Tile();
            tiles[51].image = beachTiles.getSubimage(0, 16, 16, 16);

            tiles[52] = new Tile();
            tiles[52].image = beachTiles.getSubimage(32, 16, 16, 16);

            tiles[53] = new Tile();
            tiles[53].image = beachTiles.getSubimage(0, 32, 16, 16);

            tiles[54] = new Tile();
            tiles[54].image = beachTiles.getSubimage(16, 32, 16, 16);

            tiles[55] = new Tile();
            tiles[55].image = beachTiles.getSubimage(32, 32, 16, 16);

            // Outer beach tiles
            tiles[56] = new Tile();
            tiles[56].image = beachTiles.getSubimage(48, 0, 16, 16);

            tiles[57] = new Tile();
            tiles[57].image = beachTiles.getSubimage(64, 0, 16, 16);

            tiles[58] = new Tile();
            tiles[58].image = beachTiles.getSubimage(48, 16, 16, 16);

            tiles[59] = new Tile();
            tiles[59].image = beachTiles.getSubimage(64, 16, 16, 16);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Tile class holds the data for individual tile.
     * These include their sprites, and whether or not they can collide with the player.
     */
    public class Tile {
        public BufferedImage image;
        public boolean canCollide = true;
    }

    /**
     * Returns a 2D array of the numbers of the map containing the tile sprite.
     * 
     * @return the 2D array of the numbers of the map
     */
    public int[][] getMapNumbers() {
        return mapNumbers;
    }

    /**
     * Returns an array of the tiles. 
     * 
     * @return an array of the tiles
     */
    public Tile[] getTiles() {
        return tiles;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        for (int i = 0; i < GameConfig.MAX_WORLD_COLUMNS; i++) {
            for (int j = 0; j < GameConfig.MAX_WORLD_ROWS; j++) {

                // Generate the camera-centric position.
                double worldX = i * GameConfig.TILE_SIZE;
                double worldY = j * GameConfig.TILE_SIZE;
                PlayerObject player = gameCanvas.getOwnPlayer();
                double screenX = worldX - player.getX() + player.getScreenX();
                double screenY = worldY - player.getY() + player.getScreenY();

                // Limit the render of the player for performance.
                if (worldX + GameConfig.TILE_SIZE * 4 > player.getX() - player.getScreenX() &&
                    worldX - GameConfig.TILE_SIZE * 4 < player.getX() + player.getScreenX() &&
                    worldY + GameConfig.TILE_SIZE * 2 > player.getY() - player.getScreenY() &&
                    worldY - GameConfig.TILE_SIZE * 2 < player.getY() + player.getScreenY()) {
                    g2d.drawImage(tiles[mapNumbers[i][j]].image, (int) screenX, (int) screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
                }
            }
        }
    }
}
