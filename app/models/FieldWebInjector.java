package models;

import com.kruchkov.di.*;
import com.kruchkov.ai.*;

public class FieldWebInjector implements ServiceInjector {
	@Override
	public FieldWeb getField(final int x, final int y) {
		Field f = new TicTacToeField(x, y);
		Robot r = new AiRobot(f);
		return new TicTacToeApp(f, r);
	}
}