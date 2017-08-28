import sudoku.Sudoku;

class Main
{
	public static void main(String[] args) throws Exception
	{
		for(int i=1;i<=36;++i)
			System.out.println(Sudoku.generateRandomUnsolved(i));
	}
}
