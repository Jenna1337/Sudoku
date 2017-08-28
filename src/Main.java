import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.math.BigInteger;
import sudoku.Sudoku;

class Main
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
		final int size = 4;
		String puzzle = new Puzzle(size).toString();
		System.out.println(puzzle);
		System.out.println(formatPuzzle(puzzle));
		System.out.println(formatPuzzle(SudokuSolver.solve(puzzle)));
		System.exit(0);
		System.out.println(SudokuSolver.getSolutionCount(puzzle));
		// System.exit(0);
		Collection<String> solutions = SudokuSolver.getAllSolutions(puzzle);
		for (String p : solutions)
			System.out.println(formatPuzzle(p));
	}
}
