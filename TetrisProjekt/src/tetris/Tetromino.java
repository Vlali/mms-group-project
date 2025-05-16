package src.tetris;

import java.awt.Color;

public class Tetromino {

    public enum Shape {
        NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
    }

    private Shape pieceShape;
    private int[][] coords;

    public Tetromino() {
        coords = new int[4][2];
        setShape(Shape.NoShape);
    }

    public void setShape(Shape shape) {
        int[][][] shapeTable = new int[][][]{
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}},           // NoShape
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},        // ZShape
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}},          // SShape
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}},          // LineShape
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},          // TShape
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}},           // SquareShape
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},        // LShape
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}}          // MirroredLShape
        };

        for (int i = 0; i < 4; i++) {
            coords[i][0] = shapeTable[shape.ordinal()][i][0];
            coords[i][1] = shapeTable[shape.ordinal()][i][1];
        }

        pieceShape = shape;
    }

    public Shape getShape() {
        return pieceShape;
    }

    public int getX(int index) {
        return coords[index][0];
    }

    public int getY(int index) {
        return coords[index][1];
    }

    public Tetromino rotateRight() {
        if (pieceShape == Shape.SquareShape)
            return this;

        Tetromino result = new Tetromino();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; i++) {
            result.coords[i][0] = -coords[i][1];
            result.coords[i][1] = coords[i][0];
        }

        return result;
    }

    public static Color getColor(Shape shape) {
        return switch (shape) {
            case ZShape -> Color.RED;
            case SShape -> Color.GREEN;
            case LineShape -> Color.CYAN;
            case TShape -> Color.MAGENTA;
            case SquareShape -> Color.YELLOW;
            case LShape -> Color.ORANGE;
            case MirroredLShape -> Color.BLUE;
            default -> Color.GRAY;
        };
    }
}

