package sudoku;

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
		while(p.test() && p.isNotSolved())
		{
			if(!p.assumeRandomUnsolved())
				throw new IllegalArgumentException("The puzzle is not valid");
			
			//TODO
			//if !p.test revert to previous state
			//if # of solutions is greater than 1
			//    assume random unsolved
			//the only other possibility is that the puzzle has exactly one solution
		}
		return p;
	}
}
