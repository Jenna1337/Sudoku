package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Puzzle
{
	static final int MAXSIZE = 6;
	private static final int maxwallsize = MAXSIZE*MAXSIZE;
	private static final char basechar = '\u2800';
	private static final boolean ciUseBackup = false;
	private static final char empty = '0';
	private static final char[] charsToUse = {
		' ', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z','&'
	};
	private static HashMap<Character, Integer> charIntMap = new HashMap<>(MAXSIZE);
	static{
		for(int i=0;i<charsToUse.length;++i)
			charIntMap.put(charsToUse[i], i);
	}
	private class PuzzleStaticCache
	{
		private final int boxsize;
		private final int cellcount;
		private final ArrayList<Integer> complete = new ArrayList<>();
		private final String testRegex;
		private final String formatMatch, formatReplacement;
		private int[][] indexesByRow, indexesByCol, indexesByBox;
		PuzzleStaticCache(final int size)
		{
			boxsize = size * size;
			cellcount = boxsize * boxsize;
			for (int i = 1; i <= boxsize; ++i)
				complete.add((Integer)i);
			testRegex = "("+empty+"|(([" + new String(charsToUse).substring(1) + "])(?!.*\\3))){" + boxsize + "}";
			{
				char vbar = '|', hbar = '-', cross = '+', space = ' ';
				int var = 0, var2 = 0, var3 = 0, var4 = 0, par1 = size - 1;
				String s = "", hline, br = "\n", r = "", m = "(.)";
				{
					String hsub = "";
					for (int a = 0; a < size * 2 - 1; ++a)
						hsub += hbar;
					hline = br + hsub;
					for (int b = 1; b < size; ++b)
						hline += cross + hsub;
					hline += br;
				}
				for (int i = 0; i < cellcount; ++i)
				{
					r += m;
					s += "$" + (i + 1);
					if (var != par1)
						s += space;
					else
					{
						if (var2 != par1)
							s += vbar;
						else
						{
							if (var3 != par1)
								s += br;
							else
							{
								if (var4 != par1)
									s += hline;
								else
									var4 = -1;
								var4++;
								var3 = -1;
							}
							var3++;
							var2 = -1;
						}
						var2++;
						var = -1;
					}
					var++;
				}
				formatMatch = r;
				formatReplacement = s+'\n';
			}
			indexesByRow = new int[boxsize][boxsize];
			indexesByCol = new int[boxsize][boxsize];
			indexesByBox = new int[boxsize][boxsize];
			int i, j, k, m;
			for (i = 0; i < boxsize; ++i)
			{
				for (j = 0; j < boxsize; ++j)
				{
					indexesByRow[i][j] = i * boxsize + j;
					indexesByCol[i][j] = i + j * boxsize;
				}
			}
			for (i = 0; i < size; ++i)
				for (j = 0; j < size; ++j)
					for (k = 0; k < size; ++k)
						for (m = 0; m < size; ++m)
							indexesByBox[i * size + j][k * size + m] = i * boxsize * size + j * size + k * boxsize + m;
		}
	}
	private static Map<Integer, PuzzleStaticCache> varCache = new HashMap<Integer, PuzzleStaticCache>();
	private PuzzleStaticCache vars;
	private final int puzzlesize;
	private ArrayList<Cell> cells;
	
	
	public Puzzle(final int size)
	{
		this(makeString('0', (int)Math.pow(size, 4)));
	}
	private static String makeString(final char toPut, final int length)
	{
		char[] chs = new char[length];
		for (int i = 0; i < length; ++i)
			chs[i] = toPut;
		return new String(chs);
	}
	int[] shuffledIndexes;
	public Puzzle(String puzzle)
	{
		//puzzle = puzzle.replaceAll("\\s+", "");
		{
			double sq = Math.pow(puzzle.length(), 0.25);
			puzzlesize = (int)sq;
			if(puzzlesize>maxwallsize)
				throw new IllegalArgumentException(
					"Puzzle String \"" + puzzle + "\" is too long; " + puzzle.length());
			if (sq - puzzlesize != 0)
				throw new IllegalArgumentException(
					"Puzzle String \"" + puzzle + "\" is an invalid length; " + puzzle.length());
		}
		if (varCache.containsKey(puzzlesize))
			vars = varCache.get(puzzlesize);
		else
			varCache.put(puzzlesize, vars = new PuzzleStaticCache(puzzlesize));
		
		if (puzzle.length() != vars.cellcount)
			throw new IllegalArgumentException("Puzzle String \"" + puzzle + "\" is not the correct length; "
				+ puzzle.length() + "!=" + vars.cellcount);
		cells = new ArrayList<>(vars.cellcount);
		for (int i = 0; i < vars.cellcount; ++i)
			cells.add(new Cell(parseChar(puzzle.charAt(i))));
	}
	public Puzzle(Puzzle toCopy)
	{
		this.puzzlesize = toCopy.puzzlesize;
		this.vars = toCopy.vars;
		this.cells = new ArrayList<Cell>(toCopy.cells.size());
		for (Cell c : toCopy.cells)
			this.cells.add(new Cell(c));
	}
	public boolean isSolved()
	{
		for (Cell c : cells)
			if (!c.isSolved())
				return false;
		return true;
	}
	public boolean isNotSolved()
	{
		return !isSolved();
	}
	public boolean update()
	{
		boolean changed = false;
		for (int i = 0; i < vars.boxsize; ++i)
			changed |= updateCells(vars.indexesByRow[i])
			| updateCells(vars.indexesByCol[i])
			| updateCells(vars.indexesByBox[i]);
		return changed;
	}
	private boolean updateCells(int[] indexes)
	{
		ArrayList<Integer> taken = new ArrayList<>();
		for (int i : indexes)
		{
			Cell c = cells.get(i);
			if (c.isSolved())
				taken.add(c.getAnswer());
		}
		boolean changed = false;
		for (Integer val : taken)
			for (int i : indexes)
				changed |= cells.get(i).remove(val);
		return changed;
	}
	public boolean test()
	{
		boolean passes = true;
		for (int i = 0; i < vars.boxsize && passes; ++i)
			passes &= testCells(vars.indexesByRow[i])
			&& testCells(vars.indexesByCol[i])
			&& testCells(vars.indexesByBox[i]);
		return passes;
	}
	private boolean testCells(int[] indexes)
	{
		String s = "";
		for (int i : indexes)
			s += cells.get(i).toString();
		return s.matches(vars.testRegex);
	}
	public String toString()
	{
		String s = "";
		for (Cell c : cells)
			s += toChar(c.isSolved() ? c.getAnswer() : 0);
		if (s.length() != vars.cellcount)
			throw new InternalError("Puzzle String \"" + s + "\" is not the correct length");
		return s;
	}
	public String toFormattedString()
	{
		return this.toString().replaceFirst(vars.formatMatch, vars.formatReplacement);
	}
	static final int SUCCESS = 1, INVALID = -1, FAILED = 0;
	int assume(int choice)
	{
		choice -= 1;
		for (int k = 0; k < vars.cellcount; ++k)
		{
			Cell c = cells.get(k);
			if (!c.isSolved())
			{
				switch (c.assume(choice))
				{
					case SUCCESS:
						return SUCCESS;
					case FAILED:
						continue;
					case INVALID:
						return INVALID;
					default:
						throw new InternalError();
				}
			}
		}
		return FAILED;
	}
	boolean assumeRandomUnsolved()
	{
		if(shuffledIndexes==null){
			shuffledIndexes = new int[vars.cellcount];
			for (int i = 0; i < vars.cellcount; ++i)
				shuffledIndexes[i] = i;
			shuffleArray(shuffledIndexes);
		}
		for(int randomIndex : shuffledIndexes)
			switch(cells.get(randomIndex).assumeRandom())
			{
				case Puzzle.SUCCESS:
					return true;
				case Puzzle.FAILED:
				case Puzzle.INVALID:
					continue;
				default:
					throw new InternalError();
			}
		return false;
	}
	private static Random rnd = ThreadLocalRandom.current();
	static void setRandomSeed(long seed) {
		if(rnd.getClass().isAssignableFrom(ThreadLocalRandom.class))
			rnd = new Random(seed);
		else
			rnd.setSeed(seed);
	}
	private static void shuffleArray(int[] ar)
	{
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
	class Cell
	{
		private ArrayList<Integer> possibilities;
		public Cell(int val)
		{
			possibilities = new ArrayList<>(Puzzle.this.vars.boxsize);
			if (val > 0)
			{
				if (val <= Puzzle.this.vars.boxsize)
					possibilities.add((Integer)val);
			}
			else
				for (int i = 1; i <= Puzzle.this.vars.boxsize; ++i)
					possibilities.add((Integer)i);
		}
		@SuppressWarnings("unchecked")
		public Cell(Cell toCopy)
		{
			this.possibilities = (ArrayList<Integer>)toCopy.possibilities.clone();
		}
		public boolean isSolved()
		{
			return possibilities.size() == 1;
		}
		public boolean remove(Integer possibility)
		{
			if (isSolved())
				return false;
			return possibilities.remove(possibility);
		}
		public Integer getAnswer()
		{
			if (!isSolved())
				throw new IllegalStateException();
			return possibilities.get(0);
		}
		public int assume(int choice)
		{
			if (!isSolved())
			{
				if (choice >= possibilities.size())
					return INVALID;
				Integer a = possibilities.get(choice);
				possibilities.clear();
				possibilities.add(a);
				return SUCCESS;
			}
			return FAILED;
		}
		public int assumeRandom()
		{
			return assume(random(possibilities.size()));
		}
		public boolean has(Integer val)
		{
			return possibilities.contains(val);
		}
		public String toString()
		{
			return isSolved() ? new String(new char[]{toChar(getAnswer())}) : Character.toString(empty);
		}
	}
	private static int random(final int max){
		return ThreadLocalRandom.current().nextInt(max + 1);
	}
	static char toChar(int val){
		return ciUseBackup ? (char)(basechar+val) : charsToUse[val];
	}
	static int parseChar(char val){
		return val==empty ? 0 : (ciUseBackup ? (val-basechar) : charIntMap.get(val));
	}
}
