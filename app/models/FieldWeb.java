package models;

public interface FieldWeb {
	abstract public boolean setCell(int i, int j, Byte value);
	abstract public JsonPoint startGame();
	abstract public JsonPoint nextMove(int i, int j);
	abstract public JsonPoint stopGame();
}