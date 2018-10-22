package sudoku;

class Assumption{
	private Assumption() {}
	public static enum Mode{
		DEFAULT ,
		FORWARD ,
		BACKWARD,
		RANDOM  ,
		;
	}
	public static enum Response{
		SUCCESS, INVALID, FAILED;
	}
}