package incognito.cog.util;

/* UNCOMMENT THIS
import com.acmerobotics.roadrunner.geometry.Vector2d;
*/

import static incognito.teamcode.robot.TileMovementPretty.MoveDirection.J_UP_LEFT;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import incognito.teamcode.robot.TileMovementPretty;

//import incognito.teamcode.robot.TileMovementPretty;


public class AsciiTrajectory {
    HashMap<String, String> chars = new HashMap<>();
    int GRID_SIZE = 13;
    int RATIO_MODIFIER = 3;
    int[] position;
    TileMovementPretty.MoveDirection lastDirection;
    List<List<String>> tiles = new ArrayList<>();

    public AsciiTrajectory() {
        initializeChars();
        resetTiles();
    }

    public void initializeChars() {
        setPad(" ");

        setChar("b_horizontal", "══", false);
        setChar("b_right_wall", "║", false);
        setChar("b_left_wall", "║", true);
        setChar("b_top_left", "╔═", false);
        setChar("b_top_right", "╗", false);
        setChar("b_bottom_left", "╚═", false);
        setChar("b_bottom_right", "╝", false);

        setChar("blank", " ");
        setChar("undo", "⦾");

        setChar("robot", "X");

        setChar("j_H", "H" );
        setChar("j_M", "M" );
        setChar("j_L", "L" );
        setChar("j_G", "G" );

        setChar("j_top_left", "⬉");
        setChar("j_top_right", "⬈");
        setChar("j_bottom_left", "⬋");
        setChar("j_bottom_right", "⬊");

        setChar("m_up_start", "╵");
        setChar("m_up_end", "↑");

        setChar("m_down_start", "╷");
        setChar("m_down_end", "↓");

        setChar("m_right_right", "─");
        setChar("m_left_left", "─");
        setChar("m_up_up", "│");
        setChar("m_down_down", "│");

        setChar("m_left_start", "╴");
        setChar("m_left_end", "←");

        setChar("m_right_start", "╶");
        setChar("m_right_end", "→");

        setChar("m_up_left", "╮");
        setChar("m_up_right", "╭");
        setChar("m_down_left", "╯");
        setChar("m_down_right", "╰");
        setChar("m_right_up", "╰");
        setChar("m_right_down", "╭");
        setChar("m_left_up", "╯");
        setChar("m_left_down", "╮");
    }

    public void setChar(String key, String value) {
        setChar(key, value, true);
    }

    public void setChar(String key, String value, boolean pad) {
        if (pad) {
            //value = getPad() + value + getPad();
            value = value + getPad();
        }
        chars.put(key, value);
    }

    public String getChar(String key) {
        return chars.get(key);
    }

    public void setPad(String pad) {
        chars.put("PAD", pad);
    }

    public String  getPad() {
        return chars.get("PAD");
    }

    /* EXAMPLE
@-----------------------@
|                       |
|   G   L   G   L   G   |
|                       |
|   L   M   H   M   L   |
|                       |
|   G   H   G   H   G   |
|             ←───╮     |
|   L   M   H   M │ L   |
|                 x     |
|   G   L   G   L   G   |
|                       |
@-----------------------@
     */

    public void resetTiles() {
        tiles.clear();
        List<String> firstRow = new ArrayList<>();
        firstRow.add(getChar("b_top_left"));
        for (int i = 0; i < GRID_SIZE - 2; i++) {
            firstRow.add(getChar("b_horizontal"));
        }
        firstRow.add(getChar("b_top_right"));
        tiles.add(firstRow);
        tiles.add(makeBlankRow());
        tiles.add(makeJunctionRow("GLGLG"));
        tiles.add(makeBlankRow());
        tiles.add(makeJunctionRow("LMHML"));
        tiles.add(makeBlankRow());
        tiles.add(makeJunctionRow("GHGHG"));
        tiles.add(makeBlankRow());
        tiles.add(makeJunctionRow("LMHML"));
        tiles.add(makeBlankRow());
        tiles.add(makeJunctionRow("GLGLG"));
        tiles.add(makeBlankRow());
        List<String> lastRow = new ArrayList<>();
        lastRow.add(getChar("b_bottom_left"));
        for (int i = 0; i < GRID_SIZE - 2; i++) {
            lastRow.add(getChar("b_horizontal"));
        }
        lastRow.add(getChar("b_bottom_right"));
        tiles.add(lastRow);
    }

    public List<String> makeJunctionRow(String junctionHeights) {
        List<String> row = new ArrayList<>();
        row.add(getChar("b_left_wall"));
        row.add(getChar("blank"));
        row.add(getChar("j_" + junctionHeights.charAt(0)));
        row.add(getChar("blank"));
        row.add(getChar("j_" + junctionHeights.charAt(1)));
        row.add(getChar("blank"));
        row.add(getChar("j_" + junctionHeights.charAt(2)));
        row.add(getChar("blank"));
        row.add(getChar("j_" + junctionHeights.charAt(3)));
        row.add(getChar("blank"));
        row.add(getChar("j_" + junctionHeights.charAt(4)));
        row.add(getChar("blank"));
        row.add(getChar("b_right_wall"));
        return row;
    }

    public List<String> makeBlankRow() {
        List<String> row = new ArrayList<>();
        row.add(getChar("b_left_wall"));
        for (int i = 0; i < GRID_SIZE - 2; i++) {
            row.add(getChar("blank"));
        }
        row.add(getChar("b_right_wall"));
        return row;
    }

    public void printTiles() {
        // Print the 2d array of tiles
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (List<String> row : tiles) {
            for (String tile : row) {
                s.append(tile);
            }
            s.append("\n");
        }
        return s.toString();
    }

    public void printGridFake() {
        // Print top border, taking into account the ratio modifier
        // (multiply width characters by ratio modifier)
        System.out.print(chars.get("b_top_left"));
        for (int i = 0; i < GRID_SIZE * RATIO_MODIFIER; i++) {
            System.out.print(chars.get("b_horizontal"));
        }
        System.out.println(chars.get("b_top_right"));


        // Print grid
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print(chars.get("b_vertical"));
            for (int j = 0; j < GRID_SIZE * RATIO_MODIFIER; j++) {
                System.out.print(chars.get("blank"));
            }
            System.out.println(chars.get("b_vertical"));
        }

        // Print bottom border
        System.out.print(chars.get("b_bottom_left"));
        for (int i = 0; i < GRID_SIZE * RATIO_MODIFIER; i++) {
            System.out.print(chars.get("b_horizontal"));
        }
        System.out.println(chars.get("b_bottom_right"));
    }

    /* UNCOMMENT THIS
    public void setTile(Vector2d position, String tile) {
        setTile((int) position.getX(), (int) position.getY(), tile);
    }*/

    public void setTile(int x, int y, String tile) {
        // Don't allow setting of tiles outside of the inner grid
        if (x < 1 || y < 1 || x >= GRID_SIZE - 1 || y >= GRID_SIZE - 1) {
            return;
        }
        tiles.get(y).set(x, chars.get(tile));
    }

    public void move(TileMovementPretty.MoveDirection direction) {

        if (direction == TileMovementPretty.MoveDirection.UP) {
            if (lastDirection == TileMovementPretty.MoveDirection.LEFT) {
                setTile(position[0], position[1], "m_right_up");
                position[1]--;
                setTile(position[0], position[1], "m_up_up");
            } else if (lastDirection == TileMovementPretty.MoveDirection.RIGHT) {
                setTile(position[0], position[1], "m_left_up");
                position[1]--;
                setTile(position[0], position[1], "m_up_up");
            // Just started
            } else if (lastDirection == null) {
                position[1]--;
                setTile(position[0], position[1], "m_up_start");
            } else {
                setTile(position[0], position[1], "m_up_up");
                position[1]--;
                setTile(position[0], position[1], "m_up_up");
            }
            position[1]--;
            setTile(position[0], position[1], "m_up_end");
        } else if (direction == TileMovementPretty.MoveDirection.DOWN) {
            if (lastDirection == TileMovementPretty.MoveDirection.LEFT) {
                setTile(position[0], position[1], "m_right_down");
                position[1]++;
                setTile(position[0], position[1], "m_down_down");
            } else if (lastDirection == TileMovementPretty.MoveDirection.RIGHT) {
                setTile(position[0], position[1], "m_left_down");
                position[1]++;
                setTile(position[0], position[1], "m_down_down");
            // Just started
            } else if (lastDirection == null) {
                position[1]++;
                setTile(position[0], position[1], "m_down_start");
            } else {
                setTile(position[0], position[1], "m_down_down");
                position[1]++;
                setTile(position[0], position[1], "m_down_down");
            }
            position[1]++;
            setTile(position[0], position[1], "m_down_end");
        } else if (direction == TileMovementPretty.MoveDirection.LEFT) {
            if (lastDirection == TileMovementPretty.MoveDirection.UP) {
                setTile(position[0], position[1], "m_up_left");
                position[0]--;
                setTile(position[0], position[1], "m_left_left");
            } else if (lastDirection == TileMovementPretty.MoveDirection.DOWN) {
                setTile(position[0], position[1], "m_down_left");
                position[0]--;
                setTile(position[0], position[1], "m_left_left");
            // Just started
            } else if (lastDirection == null) {
                position[0]--;
                setTile(position[0], position[1], "m_left_start");
            } else {
                setTile(position[0], position[1], "m_left_left");
                position[0]--;
                setTile(position[0], position[1], "m_left_left");
            }
            position[0]--;
            setTile(position[0], position[1], "m_left_end");
        } else if (direction == TileMovementPretty.MoveDirection.RIGHT) {
            if (lastDirection == TileMovementPretty.MoveDirection.UP) {
                setTile(position[0], position[1], "m_up_right");
                position[0]++;
                setTile(position[0], position[1], "m_right_right");
            } else if (lastDirection == TileMovementPretty.MoveDirection.DOWN) {
                setTile(position[0], position[1], "m_down_right");
                position[0]++;
                setTile(position[0], position[1], "m_right_right");
            // Just started
            } else if (lastDirection == null) {
                position[0]++;
                setTile(position[0], position[1], "m_right_start");
            } else {
                setTile(position[0], position[1], "m_right_right");
                position[0]++;
                setTile(position[0], position[1], "m_right_right");
            }
            position[0]++;
            setTile(position[0], position[1], "m_right_end");
        }
        lastDirection = direction;
    }

    public void undo(TileMovementPretty.MoveDirection previousDirection) {
        if (lastDirection == TileMovementPretty.MoveDirection.UP) {
            setTile(position[0], position[1], "blank");
            position[1]++;
            setTile(position[0], position[1], "blank");
            position[1]++;
        } else if (lastDirection == TileMovementPretty.MoveDirection.DOWN) {
            setTile(position[0], position[1], "blank");
            position[1]--;
            setTile(position[0], position[1], "blank");
            position[1]--;
        } else if (lastDirection == TileMovementPretty.MoveDirection.LEFT) {
            setTile(position[0], position[1], "blank");
            position[0]++;
            setTile(position[0], position[1], "blank");
            position[0]++;
        } else if (lastDirection == TileMovementPretty.MoveDirection.RIGHT) {
            setTile(position[0], position[1], "blank");
            position[0]--;
            setTile(position[0], position[1], "blank");
            position[0]--;
        }
        switch (previousDirection) {
            case UP:
                setTile(position[0], position[1], "m_up_end");
                break;
            case DOWN:
                setTile(position[0], position[1], "m_down_end");
                break;
            case LEFT:
                setTile(position[0], position[1], "m_left_end");
                break;
            case RIGHT:
                setTile(position[0], position[1], "m_right_end");
                break;
        }
        lastDirection = previousDirection;
    }

    public void moveToJunction(TileMovementPretty.MoveDirection direction) {
        switch (direction) {
            case J_UP_LEFT: junctionUpLeft(); break;
            case J_UP_RIGHT: junctionUpRight(); break;
            case J_DOWN_LEFT: junctionDownLeft(); break;
            case J_DOWN_RIGHT: junctionDownRight(); break;
        }
    }

    public void junctionUpLeft() {
        setTile(position[0], position[1], "j_top_left");
    }
    public void junctionUpRight() {
        setTile(position[0], position[1], "j_top_right");
    }
    public void junctionDownLeft() {
        setTile(position[0], position[1], "j_bottom_left");
    }
    public void junctionDownRight() {
        setTile(position[0], position[1], "j_bottom_right");
    }

    public int[] asCenterTile(int x, int y) {
        return new int[] {2*x + 1, 2*y + 1};
    }

    public int[] asCenterTile(int[] tile) {
        return new int[] {2*tile[0] + 1, 2*tile[1] + 1};
    }

    public void setRobotPosition(double x, double y) {
        /* UNCOMMENT
        position = asCenterTile(Generic.getTileIndex(x, y));
        // */
        position = asCenterTile((int) x, (int) y);
        setTile(position[0], position[1], "robot");
    }

    public static String octantOutput(int octant) {
        switch (octant) {
            case 0: return "   \n ⦿→\n   ";
            case 1: return "  ↗\n ⦿ \n   ";
            case 2: return " ↑ \n ⦿ \n   ";
            case 3: return "↖  \n ⦿ \n   ";
            case 4: return "   \n←⦿ \n   ";
            case 5: return "   \n ⦿ \n↙  ";
            case 6: return "   \n ⦿ \n ↓ ";
            case 7: return "   \n ⦿ \n  ↘";
            default: return "   \n ⦿ \n   ";
        }
    }

    public static void main(String[] args) {
        System.out.println("start");
        long start = System.nanoTime();
        System.out.println(start);
        AsciiTrajectory asciiTrajectory = new AsciiTrajectory();
        asciiTrajectory.setRobotPosition(5, 5);
        asciiTrajectory.move(TileMovementPretty.MoveDirection.UP);
        asciiTrajectory.move(TileMovementPretty.MoveDirection.UP);
        asciiTrajectory.move(TileMovementPretty.MoveDirection.LEFT);
        asciiTrajectory.move(TileMovementPretty.MoveDirection.LEFT);
        asciiTrajectory.undo(TileMovementPretty.MoveDirection.LEFT);
        asciiTrajectory.move(TileMovementPretty.MoveDirection.DOWN);
        asciiTrajectory.printTiles();
        System.out.println("end");
        long end = System.nanoTime();
        System.out.println(end);
        System.out.println("time: " + (end - start));
    }
}

/* UNCOMMENT FOR TESTING
class TileMovementPretty {
    public enum MoveDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}

//*/
