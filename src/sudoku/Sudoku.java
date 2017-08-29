package sudoku;

import java.math.BigInteger;
import java.util.function.Consumer;
import window.TextFrame;

public class Sudoku
{
	private Puzzle p;
	private static final int mode_blank=0, mode_random=1, mode_solved=2;
	public static final int MAXSIZE=Puzzle.MAXSIZE;
	private Sudoku(int size, int gentype)
	{
		if(gentype==mode_blank)
			p =  SudokuGenerator.generateEmptyPuzzle(size);
		else if((gentype & mode_random) > 0)
			p = SudokuGenerator.generateRandomPuzzle(size, 0);
		else
			throw new InternalError("gentype "+gentype+" is undefined");
		if((gentype & mode_solved) > 0)
			p = SudokuSolver.solve(p);
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
	private static final boolean multithreaded = false;
	public static void printEveryPossibleValidCombinationCount(){
		printEveryPossibleValidCombinationCount(false);
	}
	public static void printEveryPossibleValidCombinationCount(boolean gui){
		TextFrame frame = new TextFrame();
		frame.pack();
		frame.setVisible(gui);
		for(int m=1;m<=MAXSIZE;++m)
		{
			System.out.print("f(");
			if(m<10)
				System.out.print(' ');
			System.out.print(m+") = ");
			int k=1,p=0;
			char[] puzzle = new char[(int)Math.pow(m, 4)];
			for(int r=1;r<=m*m;++r){
				for(int c=1; c<=m*m; ++c){
					puzzle[p++] = (r<=m&&c<=m) ? Puzzle.toChar(k++) : ' ';
				}
			}
			Consumer<BigInteger> frameupdater = (i)->{
				frame.setText(i.toString());
			};
			
			BigInteger amount = pow(multithreaded ? SudokuSolver.getSolutionCountMultithreaded(new Puzzle(new String(puzzle)), frameupdater) : 
				SudokuSolver.getSolutionCount(new Puzzle(new String(puzzle)), frameupdater), new BigInteger(Integer.toString(m)));
			frame.setText(amount.toString());
			System.out.println(amount);
			frame.newLine();
			System.gc();
		}
	}
	private static BigInteger pow(BigInteger i, BigInteger n)
	{
		BigInteger val = n;
		for(BigInteger k=BigInteger.ZERO; k.compareTo(n)<0; k=k.add(BigInteger.ONE))
			val=val.multiply(i);
		return val;
	}
	private static BigInteger factorial(BigInteger n)
	{
		BigInteger val = n;
		for(BigInteger k=BigInteger.ONE; k.compareTo(n)<0; k=k.add(BigInteger.ONE))
			val=val.multiply(k);
		return val;
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
