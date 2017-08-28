package sudoku;

class SudokuGenerator
{
	public static Puzzle GeneratePuzzle(int size)
	{
		Puzzle p = new Puzzle(size);
		while(p.test() && p.isNotSolved())
		{
			if(!p.assumeRandomUnsolved())
				throw new IllegalArgumentException("The puzzle is not valid");
			
			//TODO
		}
		return p;
	}
}
