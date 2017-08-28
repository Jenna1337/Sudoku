package sudoku;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.function.Function;

class SudokuSolver
{
	public static void main(String[] args) throws Exception
	{
		/*
		 * BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(System.in)); String line;
		 * while((line=reader.readLine())!=null) { Collection<String> solutions
		 * = SudokuSolver.getAllSolutions(line);
		 * System.out.println(solutions.size()); for(String p : solutions)
		 * System.out.println(formatPuzzle(p)); } System.exit(0);
		 */
		for(int i=1;i<=36;++i)
		{
			System.out.print("f(");
			if(i<10)
				System.out.print(' ');
			System.out.print(i+") = ");
			System.out.println(getSolutionCount(new Puzzle(i)));
			System.gc();
		}
		/*
		final int size = 36;
		Puzzle puzzle = new Puzzle(size);
		System.out.println(puzzle.toString());
		System.out.println(puzzle.toFormattedString());
		System.out.println(SudokuSolver.solve(puzzle).toFormattedString());
		System.exit(0);
		System.out.println(SudokuSolver.getSolutionCount(puzzle));
		// System.exit(0);
		SudokuSolver.iterateSolutions(puzzle, (p)->{
			System.out.println(p.toFormattedString());
		})*/;
	}
	public static String solve(String puzzle){
		return solve(new Puzzle(puzzle)).toString();
	}
	private static final int maxbytes = 1000000; // 1MB
	// private static int maxsize;
	private static Collection<String> solutions = new LinkedHashSet<>();
	@Deprecated
	public static Collection<String> getAllSolutions(String puzzle)
	{
		solutions.clear();
		final int maxsize = maxbytes / (puzzle.length() + 1);
		iterateSolutions(new Puzzle(puzzle), (p) -> {
			if (solutions.size() > maxsize)
				throw new IllegalArgumentException("There is too many possible solutions");
			solutions.add(p.toString());
		});
		return solutions;
	}
	private static BigInteger solutionCount;
	public static BigInteger getSolutionCount(Puzzle puzzle)
	{
		solutionCount = BigInteger.ZERO;
		iterateSolutions(puzzle, (p) -> {
			//System.out.println(p);
			solutionCount = solutionCount.add(BigInteger.ONE);
		});
		return solutionCount;
	}
	public static void iterateSolutions(Puzzle puzzle, Consumer<Puzzle> consumer)
	{
		try
		{
			solveWithCallback(puzzle, true, null, consumer, (p) -> {
			});
		}
		catch (IllegalArgumentException e)
		{
		}
	}
	@SuppressWarnings("serial")
	private static final class FoundSolutionException extends RuntimeException
	{
	}
	private static Puzzle pzl = null;
	private static void setPzl(Puzzle p)
	{
		pzl = p;
		throw new FoundSolutionException();
	}
	private static Puzzle solve(Puzzle puzzle)
	{
		try
		{
			solveWithCallback(puzzle, false, Puzzle::isNotSolved, SudokuSolver::setPzl, SudokuSolver::setPzl);
		}
		catch (FoundSolutionException e)
		{
		}
		return pzl;
	}
	private static void solveWithCallback(Puzzle p, final boolean loopForever, final Function<Puzzle, Boolean> checkIfDone,
		final Consumer<Puzzle> onSolved, final Consumer<Puzzle> onDone)
	{
		int attempts = 0;
		while (loopForever || checkIfDone.apply(p))
		{
			if (!p.test())
				throw new IllegalArgumentException("The puzzle is not valid");
			Puzzle save = new Puzzle(p);
			while (!p.update())
			{
				if (p.isSolved())
					onSolved.accept(p);
				attempts++;
				switch (p.assume(attempts))
				{
					case Puzzle.SUCCESS:
					{
						try{
							solveWithCallback(p, loopForever, checkIfDone, onSolved, onDone);
						}
						catch (IllegalArgumentException iae)
						{
							p = new Puzzle(save);
							System.gc();
							continue;
						}
					}
					case Puzzle.INVALID:
						throw new IllegalArgumentException("The puzzle is not valid");
					case Puzzle.FAILED:
						throw new IllegalArgumentException("No more data to assume "+save);
					default:
						throw new InternalError();
				}
			}
		}
		System.gc();
		onDone.accept(p);
	}
}