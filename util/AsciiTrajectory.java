package incognito.cog.util;

import com.acmerobotics.roadrunner.geometry.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import incognito.teamcode.robot.TileMovementPretty;

public class AsciiTrajectory {
    HashMap<String, String> chars = new HashMap<>();
    int GRID_SIZE = 10;
    int RATIO_MODIFIER = 3;
    int[] position;
    TileMovementPretty.MoveDirection lastDirection;
    List<List<String>> tiles = new ArrayList<>();

    /*
@-----------------------@
|                       |
|   G   L   G   L   G   |
|                       |
|   L   M   H   M   L   |
|                       |
|   G   L   G   L   G   |
|             ←───╮     |
|   L   M   H   M │ L   |
|                 x     |
|   G   L   G   L   G   |
|                       |
@-----------------------@
     */


    AsciiTrajectory() {
        /*chars.put("b_horizontal", "═");
        chars.put("b_vertical", "║");
        chars.put("b_top_left", "╔");
        chars.put("b_top_right", "╗");
        chars.put("b_bottom_left", "╚");
        chars.put("b_bottom_right", "╝");*/
        chars.put("b_horizontal", "-");
        chars.put("b_vertical", "|");
        chars.put("b_top_left", "@");
        chars.put("b_top_right", "@");
        chars.put("b_bottom_left", "@");
        chars.put("b_bottom_right", "@");

        chars.put("blank", " ");
        chars.put("j_high", "H");
        chars.put("j_medium", "M");
        chars.put("j_low", "L");
        chars.put("j_ground", "G");

        chars.put("j_top_left", "⬉");
        chars.put("j_top_right", "⬈");
        chars.put("j_bottom_left", "⬋");
        chars.put("j_bottom_right", "⬊");

        chars.put("m_up_start", "╵");
        chars.put("m_up_end", "↑");

        chars.put("m_down_start", "╷");
        chars.put("m_down_end", "↓");

        chars.put("m_right_right", "─");
        chars.put("m_left_left", "─");
        chars.put("m_up_up", "│");
        chars.put("m_down_down", "│");

        chars.put("m_left_start", "╴");
        chars.put("m_left_end", "←");

        chars.put("m_right_start", "╶");
        chars.put("m_right_end", "→");

        chars.put("m_up_left", "╮");
        chars.put("m_up_right", "╭");
        chars.put("m_down_left", "╯");
        chars.put("m_down_right", "╰");
        chars.put("m_right_up", "╰");
        chars.put("m_right_down", "╭");
        chars.put("m_left_up", "╯");
        chars.put("m_left_down", "╮");
    }

    public void printGrid() {
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

    public static void main(String[] args) {
        AsciiTrajectory asciiTrajectory = new AsciiTrajectory();
        asciiTrajectory.printGrid();
    }

    public void setTile(Vector2d position, String tile) {
        setTile((int) position.getX(), (int) position.getY(), tile);
    }

    public void setTile(int x, int y, String tile) {
        if (x < 0 || y < 0 || x >= tiles.get(0).size() || y >= tiles.size()) {
            return;
        }
        tiles.get(y).set(x, chars.get(tile));
    }

    public void move(TileMovementPretty.MoveDirection direction) {
        if (lastDirection == null) {
            lastDirection = direction;
        }

        if (direction == TileMovementPretty.MoveDirection.UP) {
            if (lastDirection == TileMovementPretty.MoveDirection.LEFT) {
                setTile(position[0], position[1], "m_up_left");
            } else if (lastDirection == TileMovementPretty.MoveDirection.RIGHT) {
                setTile(position[0], position[1], "m_up_right");
            } else {
                setTile(position[0], position[1], "m_up_start");
            }
            position[1] += 1;
            setTile(position[0], position[1], "m_up_end");
        } else if (direction == TileMovementPretty.MoveDirection.DOWN) {
            if (lastDirection == TileMovementPretty.MoveDirection.LEFT) {
                setTile(position[0], position[1], "m_down_left");
            } else if (lastDirection == TileMovementPretty.MoveDirection.RIGHT) {
                setTile(position[0], position[1], "m_down_right");
            } else {
                setTile(position[0], position[1], "m_down_start");
            }
            position[1] -= 1;
            setTile(position[0], position[1], "m_down_end");
        } else if (direction == TileMovementPretty.MoveDirection.LEFT) {
            if (lastDirection == TileMovementPretty.MoveDirection.UP) {
                setTile(position[0], position[1], "m_left_up");
            } else if (lastDirection == TileMovementPretty.MoveDirection.DOWN) {
                setTile(position[0], position[1], "m_left_down");
            } else {
                setTile(position[0], position[1], "m_left_start");
            }
            position[0] -= 1;
            setTile(position[0], position[1], "m_left_end");
        } else if (direction == TileMovementPretty.MoveDirection.RIGHT) {
            if (lastDirection == TileMovementPretty.MoveDirection.UP) {
                setTile(position[0], position[1], "m_right_up");
            } else if (lastDirection == TileMovementPretty.MoveDirection.DOWN) {
                setTile(position[0], position[1], "m_right_down");
            } else {
                setTile(position[0], position[1], "m_right_start");
            }
            position[0] += 1;
            setTile(position[0], position[1], "m_right_end");
        }
    }

    public void junctionUpLeft() {}
    public void junctionUpRight() {}
    public void junctionDownLeft() {}
    public void junctionDownRight() {}

    public void setPosition(double x, double y) {
        position = Generic.getTileIndex(x, y);
    }
}