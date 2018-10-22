package sudoku;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.function.Function;

class SudokuSolver
{
	private SudokuSolver(){}
	
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
	private static volatile BigInteger solutionCount = BigInteger.ZERO;
	public static BigInteger getSolutionCount(Puzzle puzzle)
	{
		return getSolutionCount(puzzle, null);
	}
	public static BigInteger getSolutionCount(Puzzle puzzle, Consumer<BigInteger> onupdate)
	{
		solutionCount = BigInteger.ZERO;
		iterateSolutions(puzzle, (p) -> {
			solutionCount = solutionCount.add(BigInteger.ONE);
			if(onupdate!=null)
				onupdate.accept(solutionCount);
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
	static Puzzle solve(Puzzle puzzle)
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
		new BruteForcer(loopForever, checkIfDone, onSolved, onDone, null).accept(p);
	}
	public static BigInteger getSolutionCountMultithreaded(Puzzle puzzle)
	{
		return getSolutionCountMultithreaded(puzzle, null);
	}
	public static BigInteger getSolutionCountMultithreaded(Puzzle puzzle, Consumer<BigInteger> onupdate)
	{
		solutionCount = BigInteger.ZERO;
		if(onupdate!=null)
			onupdate.accept(solutionCount);
		solveWithCallbackMultithreaded(puzzle, true, null, (p) -> {
			synchronized(solutionCount) {
				solutionCount = solutionCount.add(BigInteger.ONE);
				if(onupdate!=null)
					onupdate.accept(solutionCount);
			}
			throw new IllegalArgumentException();
		}, (p) -> {});
		return solutionCount;
	}
	private static ThreadGroup threadGroup = new ThreadGroup("SudokuSolvers");
	private static Consumer<Puzzle> threadStarter;
	private static void solveWithCallbackMultithreaded(Puzzle p, final boolean loopForever, final Function<Puzzle, Boolean> checkIfDone,
		final Consumer<Puzzle> onSolved, final Consumer<Puzzle> onDone)
	{
		threadStarter = (pu)->{
			if(pu.test() && pu.isNotSolved()) {
				final Puzzle puco = new Puzzle(pu);
				Thread t = new Thread(threadGroup, ()->{
					new BruteForcer(loopForever, checkIfDone, onSolved, onDone, threadStarter).accept(puco);
				});
				t.start();
			}
			throw new IllegalArgumentException();
		};
		try
		{
			new BruteForcer(loopForever, checkIfDone, onSolved, onDone, threadStarter).accept(p);
		}
		catch(IllegalArgumentException e)
		{
		}
		threadGroup.setMaxPriority(4);
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		long sleeptime = (long)Math.pow(10, Math.pow(p.toString().length(), 0.25));
		while(threadGroup.activeCount()>0)
		{
			try {
				//System.out.print(solutionCount+", ");
				if(threadGroup.activeCount()>10000){
					threadGroup.interrupt();
					new OutOfMemoryError("Too many active threads").printStackTrace();
					System.exit(1);
				}
				Thread.sleep(sleeptime);
				if(threadGroup.activeCount()<1){
					Thread.sleep(sleeptime);
					if(threadGroup.activeCount()<1)
						break;
				}
			}
			catch (Exception e){
				break;
			}
		}
		
		
		System.out.print(solutionCount+", ");
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
	}
}
