package sudoku;

import java.util.function.Consumer;
import java.util.function.Function;
import static sudoku.Assumption.*;
import static sudoku.Assumption.Mode.*;
import static sudoku.Assumption.Response.*;

class BruteForcer implements Consumer<Puzzle>
{
	private final boolean loopForever0;
	private final Function<Puzzle, Boolean> checkIfDone0;
	private final Consumer<Puzzle> onSolved0, onDone0;
	private final Consumer<Puzzle> onAssumeSuccess0;
	private final Mode assumptionMode0;
	private final boolean autoupdate0;
	
	BruteForcer(final boolean loopForever, final Function<Puzzle, Boolean> checkIfDone,
		final Consumer<Puzzle> onSolved, final Consumer<Puzzle> onDone, final Consumer<Puzzle> onAssumeSuccess)
	{
		this(loopForever, checkIfDone, onSolved, onDone, onAssumeSuccess, DEFAULT, true);
	}
	BruteForcer(final boolean loopForever, final Function<Puzzle, Boolean> checkIfDone,
		final Consumer<Puzzle> onSolved, final Consumer<Puzzle> onDone, final Consumer<Puzzle> onAssumeSuccess,
		final Mode assumptionMode, final boolean autoupdate)
	{
		this.loopForever0 = loopForever;
		this.checkIfDone0 = checkIfDone;
		this.onSolved0 = onSolved;
		this.onDone0 = onDone;
		this.onAssumeSuccess0 = onAssumeSuccess;
		this.assumptionMode0 = assumptionMode;
		this.autoupdate0 = autoupdate;
	}
	public void accept(Puzzle p)
	{
		try
		{
			accept0(p);
		}
		catch (IllegalArgumentException e)
		{
		}
	}
	private void accept0(Puzzle p)
	{
		int attempts = 0;
		while (loopForever0 || checkIfDone0.apply(p))
		{
			if (!p.test())
				throw new IllegalArgumentException("The puzzle is not valid");
			Puzzle save = new Puzzle(p);
			while (!autoupdate0 || !p.update())
			{
				if (p.isSolved())
					onSolved0.accept(p);
				attempts++;
				switch (p.assume(attempts, assumptionMode0))
				{
					case SUCCESS:
					{
						try{
							if(onAssumeSuccess0!=null)
								onAssumeSuccess0.accept(p);
							accept0(p);
						}
						catch (IllegalArgumentException iae)
						{
							p = new Puzzle(save);
							System.gc();
							continue;
						}
					}
					case INVALID:
						throw new IllegalArgumentException("The puzzle is not valid");
					case FAILED:
						throw new IllegalArgumentException("No more data to assume "+save);
					default:
						throw new InternalError();
				}
			}
		}
		System.gc();
		onDone0.accept(p);
	}
}

