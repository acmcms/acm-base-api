/*
 * Created on 07.11.2005
 */
package ru.myx.sapi;

import java.util.ArrayList;
import java.util.List;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.benchmarking.Benchmark;
import ru.myx.util.Counter;

/**
 * @author myx
 * 
 */
public class TicTacToeSAPI {
	private static enum Result {
		/**
		 * 
		 */
		NONE,
		/**
		 * 
		 */
		WIN,
		/**
		 * 
		 */
		LOSE,
		/**
		 * 
		 */
		CHECK_WIN,
		/**
		 * 
		 */
		CHECK_LOSE,
		/**
		 * 
		 */
		DRAW
	}
	
	private static final int[][]	MASKS	= { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };
	
	/**
	 * @param fieldSize
	 * @param winLength
	 * @return game
	 */
	public static final TicTacToeSAPI createGame(final int fieldSize, final int winLength) {
		return new TicTacToeSAPI( fieldSize, winLength );
	}
	
	private final int[]			field;
	
	private final int			fieldSize;
	
	private final int			fieldLength;
	
	private final int			winLength;
	
	private final String[]		rate;
	
	private String				state		= null;
	
	private boolean				highlight	= false;
	
	private final List<String>	move1List	= new ArrayList<>();
	
	private final List<String>	move2List	= new ArrayList<>();
	
	/**
	 * @param fieldSize
	 * @param winLength
	 */
	public TicTacToeSAPI(final int fieldSize, final int winLength) {
		this.fieldSize = fieldSize;
		this.fieldLength = fieldSize * fieldSize;
		this.winLength = winLength;
		this.field = new int[fieldSize * fieldSize];
		this.rate = new String[fieldSize * fieldSize];
	}
	
	private final Result analyzeResult(final int index, final boolean keep) {
		final int[] field = this.field;
		if (field[index] <= 0) {
			return Result.NONE;
		}
		final int fieldSize = this.fieldSize;
		final int winLength = this.winLength;
		final int compare = field[index] & 1;
		final int x = index % fieldSize;
		final int y = index / fieldSize;
		for (final int[] mask : TicTacToeSAPI.MASKS) {
			final int maskX = mask[0];
			final int maskY = mask[1];
			int count = 1;
			for (int shift = 1;; shift++) {
				final int cx = x + maskX * shift;
				final int cy = y + maskY * shift;
				if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
					final int idx = cx + cy * fieldSize;
					final int value = field[idx];
					if (value > 0 && compare == (value & 1)) {
						count++;
					} else {
						break;
					}
				} else {
					break;
				}
				
			}
			for (int shift = 1;; shift++) {
				final int cx = x - maskX * shift;
				final int cy = y - maskY * shift;
				if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
					final int idx = cx + cy * fieldSize;
					final int value = field[idx];
					if (value > 0 && compare == (value & 1)) {
						count++;
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (count >= winLength) {
				if (!keep) {
					this.field[index] = compare == 0
							? 4
							: 3;
					for (int shift = 1;; shift++) {
						final int cx = x + maskX * shift;
						final int cy = y + maskY * shift;
						if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
							final int idx = cx + cy * fieldSize;
							final int value = field[idx];
							if (value > 0 && compare == (value & 1)) {
								this.field[idx] = compare == 0
										? 4
										: 3;
							} else {
								break;
							}
						} else {
							break;
						}
					}
					for (int shift = 1;; shift++) {
						final int cx = x - maskX * shift;
						final int cy = y - maskY * shift;
						if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
							final int idx = cx + cy * fieldSize;
							final int value = field[idx];
							if (value > 0 && compare == (value & 1)) {
								this.field[idx] = compare == 0
										? 4
										: 3;
							} else {
								break;
							}
						} else {
							break;
						}
					}
				}
				return compare == 1
						? Result.WIN
						: Result.LOSE;
			}
		}
		for (int i = this.fieldLength - 1; i >= 0; --i) {
			if (this.field[i] <= 0) {
				return Result.NONE;
			}
		}
		this.state = "draw / ничья";
		return Result.DRAW;
	}
	
	private final int bestMove() {
		int bestMove = 0;
		double bestRate = 0;
		for (int index = this.fieldLength - 1; index >= 0; index--) {
			if (this.field[index] <= 0) {
				this.rate[index] = "";
				final double rate = this.moveRate( index );
				this.rate[index] += "rate=" + rate;
				if (rate > bestRate) {
					bestRate = rate;
					bestMove = index;
				}
			}
		}
		if (bestRate > 0) {
			return bestMove;
		}
		return this.fieldSize - 1;
	}
	
	private final int bestMove2() {
		int bestMove = 0;
		double bestRate = 0;
		for (int index = this.fieldLength - 1; index >= 0; index--) {
			if (this.field[index] <= 0) {
				final double rate = this.moveRate2( index );
				if (rate > bestRate) {
					bestRate = rate;
					bestMove = index;
				}
			}
		}
		if (bestRate > 0) {
			return bestMove;
		}
		return this.fieldSize - 1;
	}
	
	/**
	 * @param ctx
	 * @param moves
	 */
	public final void build(final ExecProcess ctx, final String moves) {
		if (Benchmark.USER_ID != Context.getUserId( ctx )) {
			try {
				Thread.sleep( 500L );
			} catch (final InterruptedException e) {
				throw new RuntimeException( e );
			}
		}
		int move1last = -1;
		int move2last = -1;
		for (final String token : moves.split( "/" )) {
			if (token == null || token.length() < 2) {
				continue;
			}
			if ("finish".equals( token )) {
				for (;;) {
					{
						final int move = this.bestMove2();
						if (move < 0 || move >= this.fieldLength) {
							this.state = "illegal coordinate";
							return;
						}
						if (this.field[move] > 0) {
							this.state = "illegal move";
							return;
						}
						this.field[move] = 1;
						this.move1List.add( this.getMoveName( move ).toUpperCase() );
						move1last = move;
						if (this.makeResult( move )) {
							return;
						}
					}
					{
						final int move = this.bestMove();
						if (move < 0 || move >= this.fieldLength) {
							this.state = "illegal coordinate";
							return;
						}
						if (this.field[move] > 0) {
							this.state = "illegal move";
							return;
						}
						this.field[move] = 2;
						this.move2List.add( this.getMoveName( move ).toUpperCase() );
						move2last = move;
						if (this.makeResult( move )) {
							return;
						}
					}
				}
			}
			if ("finish2".equals( token )) {
				for (;;) {
					{
						final int move = this.bestMove();
						if (move < 0 || move >= this.fieldLength) {
							this.state = "illegal coordinate";
							return;
						}
						if (this.field[move] > 0) {
							this.state = "illegal move";
							return;
						}
						this.field[move] = 1;
						this.move1List.add( this.getMoveName( move ).toUpperCase() );
						move1last = move;
						if (this.makeResult( move )) {
							return;
						}
					}
					{
						final int move = this.bestMove();
						if (move < 0 || move >= this.fieldLength) {
							this.state = "illegal coordinate";
							return;
						}
						if (this.field[move] > 0) {
							this.state = "illegal move";
							return;
						}
						this.field[move] = 2;
						this.move2List.add( this.getMoveName( move ).toUpperCase() );
						move2last = move;
						if (this.makeResult( move )) {
							return;
						}
					}
				}
			}
			{
				final int move = token.charAt( 0 ) - 'a' + (token.charAt( 1 ) - 'a') * this.fieldSize;
				if (move < 0 || move >= this.fieldLength) {
					this.state = "illegal coordinate";
					return;
				}
				if (this.field[move] > 0) {
					this.state = "illegal move";
					return;
				}
				this.field[move] = 1;
				this.move1List.add( this.getMoveName( move ).toUpperCase() );
				move1last = move;
				if (this.makeResult( move )) {
					return;
				}
			}
			{
				final int move = this.bestMove();
				if (move < 0 || move >= this.fieldLength) {
					this.state = "illegal coordinate";
					return;
				}
				if (this.field[move] > 0) {
					this.state = "illegal move";
					return;
				}
				this.field[move] = 2;
				this.move2List.add( this.getMoveName( move ).toUpperCase() );
				move2last = move;
				if (this.makeResult( move )) {
					return;
				}
			}
		}
		for (int i = this.fieldLength - 1; i >= 0; --i) {
			if (this.field[i] == 0) {
				this.field[i] = 2;
				if (this.analyzeResult( i, true ) == Result.LOSE) {
					this.field[i] = 1;
					if (this.analyzeResult( i, true ) == Result.WIN) {
						this.highlight = true;
						this.field[i] = -7;
					} else {
						this.highlight = true;
						this.field[i] = -5;
					}
				} else {
					this.field[i] = 1;
					final Result result = this.analyzeResult( i, true );
					if (result == Result.DRAW) {
						this.highlight = true;
						this.field[i] = -6;
						continue;
					}
					if (result == Result.WIN) {
						this.highlight = true;
						this.field[i] = -7;
						continue;
					}
					this.field[i] = 0;
				}
			}
		}
		if (!this.highlight) {
			if (move1last != -1) {
				this.field[move1last] = 3;
			}
			if (move2last != -1) {
				this.field[move2last] = 4;
			}
		}
	}
	
	private final double cellRate(final int index, final int checkOriginal) {
		this.rate[index] += '\n';
		final Counter rate = new Counter( 1.0 );
		final int check = checkOriginal & 1;
		final int fieldSize = this.fieldSize;
		final int winLength = this.winLength;
		final int[] field = this.field;
		final int x = index % fieldSize;
		final int y = index / fieldSize;
		for (final int[] mask : TicTacToeSAPI.MASKS) {
			final int maskX = mask[0];
			final int maskY = mask[1];
			for (int position = winLength; position > 0; position--) {
				double valueWeight = 0;
				int count = 1;
				int vacant = 0;
				{
					final int lineCheck = 1 + winLength - position;
					for (int shift = 1; shift < lineCheck; shift++) {
						final int cx = x + maskX * shift;
						final int cy = y + maskY * shift;
						if (cx < 0 || cy < 0 || cx >= fieldSize || cy >= fieldSize) {
							break;
						}
						final int value = field[cx + cy * fieldSize];
						if (value > 0) {
							if ((value & 1) == check) {
								count++;
							} else {
								break;
							}
						} else {
							vacant++;
						}
					}
				}
				{
					final int lineCheck = position;
					for (int shift = 1; shift < lineCheck; shift++) {
						final int cx = x - maskX * shift;
						final int cy = y - maskY * shift;
						if (cx < 0 || cy < 0 || cx >= fieldSize || cy >= fieldSize) {
							break;
						}
						final int value = field[cx + cy * fieldSize];
						if (value > 0) {
							if ((value & 1) == check) {
								count++;
							} else {
								break;
							}
						} else {
							vacant++;
						}
					}
				}
				if (count + Math.min( vacant, 1 ) < winLength) {
					// if(count + vacant >= winLength){
					valueWeight += 1.0 * Math.min( vacant, winLength ) * count * count;
					// }
				} else {
					valueWeight += count * count * count * count * count * winLength;
				}
				// this.rate[index] += "" + "val=" + valueWeight + '\n';
				if (valueWeight > 0) {
					rate.register( valueWeight );
				}
			}
		}
		this.rate[index] += "weight=" + (rate.getAverage() + rate.doubleValue()) + '\n';
		return rate.getAverage() + rate.doubleValue();
		// this.rate[index] += "weight="
		// + (rate.doubleValue() + rate.getAverage() + rate.getMaximum())
		// + '\n';
		// return rate.doubleValue() + rate.getAverage() + rate.getMaximum();
	}
	
	private final void cellRate2(final int index, final Counter rate, final int checkOriginal) {
		final int check = checkOriginal & 1;
		final int fieldSize = this.fieldSize;
		final int winLength = this.winLength;
		final int[] field = this.field;
		final int x = index % fieldSize;
		final int y = index / fieldSize;
		for (final int[] mask : TicTacToeSAPI.MASKS) {
			final int maskX = mask[0];
			final int maskY = mask[1];
			int count = 1;
			int row = 2 - check;
			int open = 0;
			double vacant = 0;
			{
				boolean broken = false;
				for (int shift = 1; shift < winLength; shift++) {
					final int cx = x + maskX * shift;
					final int cy = y + maskY * shift;
					if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
						final int value = field[cx + cy * fieldSize];
						if (value > 0) {
							if ((value & 1) == check) {
								if (!broken) {
									row++;
								}
								count++;
							} else {
								break;
							}
						} else {
							if (!broken) {
								open++;
							}
							broken = true;
							vacant += 1.0 * winLength / shift;
						}
					} else {
						break;
					}
				}
			}
			{
				boolean broken = false;
				for (int shift = 1; shift < winLength; shift++) {
					final int cx = x - maskX * shift;
					final int cy = y - maskY * shift;
					if (cx >= 0 && cy >= 0 && cx < fieldSize && cy < fieldSize) {
						final int value = field[cx + cy * fieldSize];
						if (value > 0) {
							if ((value & 1) == check) {
								if (!broken) {
									row++;
								}
								count++;
							} else {
								break;
							}
						} else {
							if (!broken) {
								open++;
							}
							broken = true;
							vacant += 1.0 * winLength / shift;
						}
					} else {
						break;
					}
				}
			}
			vacant /= winLength;
			rate.register( (row + open) * row * row + (count + open) * count + vacant + check );
			if (count >= winLength) {
				rate.register( count * count * (winLength + checkOriginal) );
			}
		}
	}
	
	/**
	 * @return int array
	 */
	public final int[] getField() {
		return this.field;
	}
	
	/**
	 * @return int
	 */
	public final int getFieldSize() {
		return this.fieldSize;
	}
	
	/**
	 * @return boolean
	 */
	public boolean getHighlight() {
		return this.highlight;
	}
	
	/**
	 * @return list
	 */
	public final List<String> getMove1List() {
		return this.move1List;
	}
	
	/**
	 * @return list
	 */
	public final List<String> getMove2List() {
		return this.move2List;
	}
	
	private final String getMoveName(final int index) {
		return (char) ('a' + index % this.fieldSize) + "" + (char) ('a' + index / this.fieldSize);
	}
	
	/**
	 * @return string array
	 */
	public final String[] getRate() {
		return this.rate;
	}
	
	/**
	 * @return string
	 */
	public final String getState() {
		return this.state;
	}
	
	/**
	 * @return int
	 */
	public final int getWinLength() {
		return this.winLength;
	}
	
	private final boolean makeResult(final int index) {
		final Result result = this.analyzeResult( index, false );
		if (result == Result.NONE) {
			this.state = null;
			return false;
		}
		if (result == Result.WIN) {
			this.state = "win / победа";
			return true;
		}
		if (result == Result.LOSE) {
			this.state = "lose / проигрыш";
			return true;
		}
		if (result == Result.DRAW) {
			this.state = "draw / ничья";
			return true;
		}
		return false;
	}
	
	private final double moveRate(final int index) {
		return this.cellRate( index, 1 ) * 1.01 + this.cellRate( index, 2 );
	}
	
	private final double moveRate2(final int index) {
		final Counter rate = new Counter( 1.0 );
		this.cellRate2( index, rate, 1 );
		this.cellRate2( index, rate, 2 );
		return rate.doubleValue() + rate.getMaximum();
	}
	
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		for (int y = 0; y < this.fieldSize; y++) {
			for (int x = 0; x < this.fieldSize; x++) {
				builder.append( this.field[x + y * this.fieldSize] );
			}
			builder.append( '\n' );
		}
		return builder.toString();
	}
}
