package sudoku;

public class Sudoku
{
	private Puzzle p;
	private static final int mode_blank=0, mode_random=1, mode_solved=2;
	private Sudoku(int size, int gentype)
	{
		
	}
	public static Sudoku generateBlank(int size)
	{
		return new Sudoku(size, mode_blank);
	}
	public static Sudoku generateRandomUnsolved(int size)
	{
		return new Sudoku(size, mode_random);
	}
	public static Sudoku generateRandomSolved(int size)
	{
		return new Sudoku(size, mode_random | mode_solved);
	}
	public static void printEveryPossibleValidCombinationCount(){
		for(int i=1;i<=36;++i)
		{
			System.out.print("f(");
			if(i<10)
				System.out.print(' ');
			System.out.print(i+") = ");
			System.out.println(SudokuSolver.getSolutionCount(new Puzzle(i)));
			System.gc();
		}
	}
	
	public String toString() {
		return p.toFormattedString();
	}
	
	@Deprecated
	private static String formatPuzzle(String puzzle)
	{
		return new Puzzle(puzzle).toFormattedString();
	}
}
