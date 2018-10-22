package sudoku;

import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Function;

class SudokuGenerator
{
	private SudokuGenerator(){}
	
	public static Puzzle generateEmptyPuzzle(int size)
	{
		return new Puzzle(size);
	}
	public static Puzzle generateRandomPuzzle(int size, long seed)
	{
		Puzzle p = new Puzzle(size);
		Puzzle.setRandomSeed(seed);
		return makePuzzle0(p);
	}
	private static Puzzle makePuzzle0(Puzzle p)
	{
		final boolean loopForever = false;
		final Function<Puzzle, Boolean> checkIfDone = pz->(
			SudokuSolver.getSolutionCount(pz, count->{
				if(count.compareTo(BigInteger.ONE)>0) throw new IllegalArgumentException();
			}).compareTo(BigInteger.ONE)>0);
		final Consumer<Puzzle> throwError = pz->{throw new UnknownError("This shouldn't happen");},
			onSolved = throwError,
			onDone = throwError;
		final Consumer<Puzzle> onAssumeSuccess = pz->checkIfDone.apply(pz);
		
		
		BruteForcer solver = new BruteForcer(loopForever, checkIfDone, onSolved, onDone, onAssumeSuccess, Assumption.Mode.RANDOM, false);
		solver.accept(p);
		return p;
		
		//TODO
		//if !p.test revert to previous state
		//if # of solutions is greater than 1
		//    assume random unsolved
		//the only other possibility is that the puzzle has exactly one solution
	}
}
