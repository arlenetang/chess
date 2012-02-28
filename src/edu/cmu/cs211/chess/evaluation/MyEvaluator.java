package edu.cmu.cs211.chess.evaluation;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayPiece;
import edu.cmu.cs211.chess.evaluation.Evaluator;

public class MyEvaluator implements Evaluator<ArrayBoard>
{

	private static final int INFINITY  = 1000000;
	private static final int MATE      = 300000;
	private static final int STALEMATE = 0;

	public int infty()
	{
		return INFINITY;
	}

	public int mate()
	{
		return MATE;
	}

	public int stalemate()
	{
		return STALEMATE;
	}

	/*
	 * This is the evaluator. It simply returns a score for the board position
	 * with respect to the player to move. It must function precisely as
	 * described here in order to pass the unit tests. [If you want to use a
	 * different evaluation function for a more advanced version of your
	 * program, you can do that, but the eval() method here must function as
	 * described.]
	 * 
	 * The evaluation function gives a score for each piece according to the
	 * pieceValue array below, and an additional amount for each piece depending
	 * on where it is (see comment below). A bonus of 10 points should be given
	 * if the current player has castled (and -10 for the opponent castling)
	 * 
	 * The eval of a position is the value of the pieces of the player whose
	 * turn it is, minus the value of the pieces of the other player (plus the
	 * castling points thrown in).
	 * 
	 * If it's WHITE's turn, and white is up a queen, then the value will be
	 * roughly 900. If it's BLACK's turn and white is up a queen, then the value
	 * returned should be about -900.
	 */


	private boolean useEndingTables;
	private int gamePhase;

	public int eval(ArrayBoard board)
	{

		int score = 0;
		int player = board.toPlay(); // player turn

		// phase check
		gamePhase = getGamePhase(board);
		if(gamePhase  <= PHASE_OPENING) useEndingTables = false;
		else useEndingTables = true;


		// gets an Iterable of all board pieces
		Iterable<ArrayPiece> pieceList = board.allPieces();

		// adds score of each piece
		for(ArrayPiece piece : pieceList){
			if(player == piece.color())
				score += getPieceScore(piece); 
			else
				score -= getPieceScore(piece);
		}


		// check if player has castled
		if(board.hasCastled[player] == true){
			score += CASTLE_BONUS;
		}

		// check if opponent has castled
		if(board.hasCastled[1-player]){
			score -= CASTLE_BONUS;
		}

		return score;
	}


	/*
	 * Takes a piece on the board and returns the score corresponding to that
	 * piece.
	 * 
	 * Note: start checking from type with the greatest number of pieces, pawn(16 pieces)
	 */
	private int getPieceScore(ArrayPiece piece){
		int pieceScore = 0;

		// gets type, row and col of pieces
		// optimization, so that we only call methods once.
		int type = piece.type();
		int row = piece.row();
		int col = piece.col();
		int[][] posArray;

		if(type == ArrayPiece.PAWN){

			if(gamePhase <= PHASE_MIDDLE)
				posArray = pawnpos;
			else
				posArray = pawnposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += pawnval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += pawnval + posArray[7-row][col];				

			if(gamePhase >= PHASE_ENDING)
				pieceScore += 30;
		}
		else if (type == ArrayPiece.KNIGHT){

			if(!useEndingTables)
				posArray = knightpos;
			else
				posArray = knightposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += knightval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += knightval + posArray[7-row][col];

		}	
		else if (type == ArrayPiece.BISHOP){

			if(!useEndingTables)
				posArray = bishoppos;
			else
				posArray = bishopposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += bishopval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += bishopval + posArray[7-row][col];

		}	
		else if (type == ArrayPiece.ROOK){

			if(!useEndingTables)
				posArray = rookpos;
			else
				posArray = rookposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += rookval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += rookval + posArray[7-row][col];

		}	
		else if (type == ArrayPiece.QUEEN){

			if(!useEndingTables)
				posArray = queenpos;
			else
				posArray = queenposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += queenval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += queenval + posArray[7-row][col];

		}	
		else if (type == ArrayPiece.KING){

			if(gamePhase <= PHASE_MIDDLE)
				posArray = kingpos;
			else
				posArray = kingposend;

			if(piece.color() == ArrayBoard.WHITE)
				pieceScore += kingval + posArray[row][col];
			else // piece.color() == board.black, flip values in array
				pieceScore += kingval + posArray[7-row][col];
		}	


		return pieceScore;
	}


	/*
	 * Piece value tables modify the value of each piece according to where it
	 * is on the board.
	 * 
	 * To orient these tables, each row of 8 represents one row (rank) of the
	 * chessboard.
	 * 
	 * !!! The first row is where white's pieces start !!!
	 * 
	 * So, for example
	 * having a pawn at d2 is worth -5 for white. Having it at d7 is worth
	 * 20. Note that these have to be flipped over to evaluate black's pawns
	 * since pawn values are not symmetric.
	 */


	private static int knightpos[][] =
	{
		{-135, -25, -15, -10, -10, -15, -25,-135},
		{-20, -10,   0,   5,   5,   0, -10, -20},
		{-5,   5,  15,  20,  20,  15,   5,  -5},
		{ -5,   5,  15,  20,  20,  15,   5,  -5},
		{-10,   0,  10,  15,  15,  10,   0, -10},
		{-20, -10,   0,   5,   5,   0, -10, -20},
		{-35, -25, -15, -10, -10, -15, -25, -35},
		{-50, -40, -30, -25, -25, -30, -40, -50}
	};

	private static int knightposend[][] =
	{
		{-10,  -5,  -5,  -5,  -5,  -5,  -5, -10},   
		{-5,   0,   0,   0,   0,   0,   0,  -5},  
		{-5,   0,   5,   5,   5,   5,   0,  -5},  
		{-5,   0,   5,  10,  10,   5,   0,  -5},  
		{-5,   0,   5,  10,  10,   5,   0,  -5},  
		{-5,   0,   5,   5,   5,   5,   0,  -5},  
		{-5,   0,   0,   0,   0,   0,   0,  -5},
		{-10,  -5,  -5,  -5,  -5,  -5,  -5, -10}
	};

	private static int bishoppos[][] =
	{
		{-20, -15, -15, -13, -13, -15, -15, -20},
		{-5,   0,  -5,   0,   0,  -5,   0,  -5},
		{-6,  -2,   4,   2,   2,   4,  -2,  -6},
		{-4,   0,   2,  10,  10,   2,   0,  -4},
		{-4,   0,   2,  10,  10,   2,   0,  -4},
		{-6,  -2,   4,   2,   2,   4,  -2,  -6},
		{-5,   0,  -2,   0,   0,  -2,   0,  -5},
		{-8,  -8,  -6,  -4,  -4,  -6,  -8,  -8}

	};

	private static int bishopposend[][] =
	{
		{-18, -12,  -9,  -6,  -6,  -9, -12, -18}, 
		{-12,  -6,  -3,   0,   0,  -3,  -6, -12},  
		{-9,  -3,   0,   3,   3,   0,  -3,  -9},  
		{-6,   0,   3,   6,   6,   3,   0,  -6},  
		{-6,   0,   3,   6,   6,   3,   0,  -6},  
		{-9,  -3,   0,   3,   3,   0,  -3,  -9},  
		{-12,  -6,  -3,   0,   0,  -3,  -6, -12},  
		{-18, -12,  -9,  -6,  -6,  -9, -12, -18},  
	};

	private static int rookpos[][] =
	{
		{-6,   -3,   0,   3,   3,   0,   -3,   -6},
		{-6,   -3,   0,   3,   3,   0,   -3,   -6},
		{-6,   -3,   0,   3,   3,   0,   -3,   -6},
		{-6,   -3,   0,   3,   3,   0,   -3,   -6},
		{-6,   -3,   0,   3,   3,   0,   -3,   -6},
		{-3,    0,   5,   3,   3,   5,    0,   -3},
		{-2,   10,  10,  10,  10,  10,   10,   -2},
		{-6,   -3,   0,   3,   3,   0,   -3,   -6}
	};

	private static int rookposend[][] =
	{
		{-18, -12,  -9,  -6,  -6,  -9, -12, -18}, 
		{-12,  -6,  -3,   0,   0,  -3,  -6, -12},  
		{-9,  -3,   0,   3,   3,   0,  -3,  -9},  
		{-6,   0,   3,   6,   6,   3,   0,  -6},  
		{-6,   0,   3,   6,   6,   3,   0,  -6},  
		{-9,  -3,   0,   3,   3,   0,  -3,  -9},  
		{-12,  -6,  -3,   0,   0,  -3,  -6, -12},  
		{-18, -12,  -9,  -6,  -6,  -9, -12, -18},
	};



	private static int queenpos[][] =
	{
		{-10, -10, -10, -10, -10, -10, -10, -10},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0},    
		{0,   0,   0,   0,   0,   0,   0,   0} 
	};

	private static int queenposend[][] =
	{
		{-24, -16, -12,  -8,  -8, -12, -16, -24},   
		{-16,  -8,  -4,   0,   0,  -4,  -8, -16},  
		{-12,  -4,   0,   4,   4,   0,  -4, -12},  
		{-8,   0,   4,   8,   8,   4,   0,  -8},  
		{ -8,   0,   4,   8,   8,   4,   0,  -8},  
		{-12,  -4,   0,   4,   4,   0,  -4, -12},  
		{-16,  -8,  -4,   0,   0,  -4,  -8, -16},  
		{-24, -16, -12,  -8,  -8, -12, -16, -24},  
	};

	private static int pawnpos[][] =
	{
		{-15,   -5,   0,   5,   5,   0,   -5,   -15},    
		{-15,   -5,   0,   5,   5,   0,   -5,   -15},    
		{-15,   -5,   0,  15,  15,   0,   -5,   -15},    
		{-15,   -5,   0,  25,  25,   0,   -5,   -15},    
		{-15,   -5,   0,  15,  15,   0,   -5,   -15},    
		{-15,   -5,   0,   5,   5,   0,   -5,   -15},    
		{-15,   -5,   0,   5,   5,   0,   -5,   -15},    
		{-15,   -5,   0,   5,   5,   0,   -5,   -15}    
	};

	private static int pawnposend[][] =
	{
		{ 0,  0,   0,   0,   0,   0,  0,  0},
		{ 0,  0,   0,  -5,  -5,   0,  0,  0},
		{ 5, 12,  10,   5,   5,  10, 12,  5},
		{ 9, 19,  17,  15,  15,  17, 19,  9},
		{10, 24,  22,  20,  20,  22, 24, 10},
		{31, 29,  27,  30,  30,  27, 29, 31},
		{50, 40,  32,  40,  40,  32, 40, 50},
		{ 0,  0,   0,   0,   0,   0,  0,  0}

	};


	// The following two king positions will be used in opening and middle game
	private static int kingpos[][] =
	{
		{10,  20,   0,   0,   0,  10,  20,  10},
		{10,  15,   0,   0,   0,   0,  15,  10},
		{-10, -20, -20, -25, -25, -20, -20, -10},
		{-15, -25, -40, -40, -40, -40, -25, -15},
		{-30, -40, -40, -40, -40, -40, -40, -30},
		{-40, -50, -50, -50, -50, -50, -50, -40}, 
		{-50, -50, -50, -50, -50, -50, -50, -50}, 
		{-50, -50, -50, -50, -50, -50, -50, -50},
	};	

	// Used to encourage the kings to move to the center in the ending
	private static int kingposend[][] =
	{
		{-20, -15, -10, -10, -10, -10, -15, -20},  
		{-15,  -5,   0,   0,   0,   0,  -5, -15},  
		{-10,   0,   5,   5,   5,   5,   0, -10},  
		{-10,   0,   5,  10,  10,   5,   0, -10},  
		{-10,   0,   5,  10,  10,   5,   0, -10},  
		{-10,   0,   5,   5,   5,   5,   0, -10},  
		{-15,  -5,   0,   0,   0,   0,  -5, -15},  
		{-20, -15, -10, -10, -10, -10, -10, -20},  
	};	




	/* Material value of a piece */
	private static final int kingval      = 350;
	private static final int queenval     = 975;
	private static final int rookval      = 500;
	private static final int bishopval    = 325;
	private static final int knightval    = 325;
	private static final int pawnval      = 100;
	//private static final int emptyval     = 0;

	/* The bonus for castling */
	private static final int CASTLE_BONUS = 20;







	// Game phase constans
	public static final int PHASE_OPENING = 0;
	public static final int PHASE_MIDDLE = 1;
	public static final int PHASE_ENDING = 2;
	public static final int PHASE_PAWN_ENDING = 3;

	// method to check game phase
	public static int getGamePhase(ArrayBoard board)
	{
		int phase = PHASE_OPENING; // Initialize to opening
		int gamePhaseCheck = 0; // Initialize the count

		gamePhaseCheck += board.countOfType(ArrayPiece.KNIGHT);
		gamePhaseCheck += board.countOfType(ArrayPiece.BISHOP);
		gamePhaseCheck += board.countOfType(ArrayPiece.ROOK) *2;
		gamePhaseCheck += board.countOfType(ArrayPiece.QUEEN) *4;


		if(gamePhaseCheck <= 0) phase = PHASE_PAWN_ENDING;
		else if(gamePhaseCheck <= 8) phase = PHASE_ENDING;
		else if(gamePhaseCheck >20) phase = PHASE_OPENING;
		else phase = PHASE_MIDDLE;

		return phase;		
	}



}

