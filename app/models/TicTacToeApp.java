package models;

import java.util.Arrays;
import com.kruchkov.di.*;
import com.kruchkov.di.Player;
import com.kruchkov.ai.*;

public class TicTacToeApp implements FieldWeb {
    private Field model = null;
    private Robot robot = null;
    private int rows = 10;
    private int cols = 10;
    private JsonPoint p = new JsonPoint();

    public TicTacToeApp(final Field f, final Robot r) {
        this.model = f;
        this.robot = r;
        this.rows = model.rows();
        this.cols = model.cols();
    }

    @Override
    public boolean setCell(int i, int j, Byte value) {
        if (value == null || i >= rows || j >= cols || i < 0 || j < 0) {
            //log.warn("Cell setter fails with invalid arguments");
            return false;
        }
        return model.set(i, j, value);
    }


    @Override
    public JsonPoint startGame() {
        this.model.reset();
        p.status(GameStatus.GAME_STARTED);
        return p;
    }

    @Override
    public JsonPoint nextMove(int x, int y) {
        /*if (arr == null) {
            p.status(GameStatus.MATRIX_ERROR);
            p.details("Matrix is null");
            return p;
        }*/

        p.status(GameStatus.MOVE_ERROR);

        Byte item = model.get(x, y);
        /*if (item == null) {
            //log.error("Null value in [" + x + "][" + y + "]");
            return p;
        }*/
        if (item != null && item.intValue() == 0) {
            if (setCell(x, y, (byte) 2)) {

                Result res = robot.isSomeoneWin();
                Player winner = res.getPlayer();

                if (winner == Player.NONE) {
                    TicPoint robotMove = robot.makeAMove();
                    if (robotMove == null) {
                        //log.error("Robot can not make a move");
                        System.out.println("Robot can not make a move");
                        return p;
                    }

                    if (setCell(robotMove.getX(), robotMove.getY(), (byte) 1)) {
                        p.x(robotMove.getX());
                        p.y(robotMove.getY());
                        p.status(GameStatus.NEXT_MOVE);

                        res = robot.isSomeoneWin();
                        winner = res.getPlayer();

                        if (winner != Player.NONE) {
                            res.setPoint(robotMove);
                            return gameOver(res);
                        }
                    }
                    
                } else {
                    res.setPoint(x, y);
                    return gameOver(res);
                }
            }
        }

        return p;
    }

    private JsonPoint gameOver(Result result) {
        p.status(GameStatus.UNKNOWN_STATUS);
        if (result == null) {
            return p;
        }
        p.x(result.getPoint().getX());  
        p.y(result.getPoint().getY());

        Player winner = result.getPlayer();

        /*String msg = "";
        switch (winner.ordinal()) {
            case 1: msg = "Robot wins!"; break;
            case 2: msg = "You win!"; break;
            case 3: msg = "Draw!"; break;
            default: 
                msg = "Something went wrong :o";
                p.status(GameStatus.GAME_OVER);
        }*/

        TicPoint p1 = result.getArrPoint(0);
        TicPoint p2 = result.getArrPoint(4);
        String msg = String.format("{\"winner\":%d,\"x0\":%d,\"y0\":%d,\"x1\":%d,\"y1\":%d}", 
        winner.ordinal(), p1.getX(), p1.getY(), p2.getX(), p2.getY());

        p.details(msg);
        return p;
    }

    @Override
    public JsonPoint stopGame() {
        this.model = null;
        this.robot = null;
        //this.rows = 10;
        //this.cols = 10;
        p.status(GameStatus.GAME_STOPPED);
        return p;
    }
}