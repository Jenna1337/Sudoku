import sudoku.Sudoku;

class Main
{
	public static void main(String[] args) throws Exception
	{
		Sudoku.printEveryPossibleValidCombinationCount(true);
		
		System.exit(0);
		for(int i=1;i<=Sudoku.MAXSIZE;++i)
			System.out.println(Sudoku.generateRandomUnsolved(i));
	}
}
