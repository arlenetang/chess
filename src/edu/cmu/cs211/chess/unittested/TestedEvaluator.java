package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayPiece;
import edu.cmu.cs211.chess.evaluation.Evaluator;

public class TestedEvaluator implements Evaluator<ArrayBoard>
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

	public int eval(ArrayBoard board)
	{
		
		int score = 0;
		int player = board.toPlay(); // player turn

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
		
		if(type == ArrayPiece.PAWN){
			if(piece.color() == ArrayBoard.WHITE){
				pieceScore += pawnval + pawnpos[row][col];
			}
			else{ // piece.color() == board.black, flip values in array
				pieceScore += pawnval + pawnpos[7-row][col];				
			}
				
		}
		else if (type == ArrayPiece.KNIGHT){
			pieceScore += knightval + knightpos[row][col];
		}	
		else if (type == ArrayPiece.BISHOP){
			pieceScore += bishopval + bishoppos[row][col];
		}	
		else if (type == ArrayPiece.ROOK){
			pieceScore += rookval;
		}	
		else if (type == ArrayPiece.QUEEN){
			pieceScore += queenval;
		}	
		else if (type == ArrayPiece.KING){
			pieceScore += kingval;
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
	private static int bishoppos[][] =
	{
		{-5, -5, -5, -5, -5, -5, -5, -5},
		{-5, 10,  5,  8,  8,  5, 10, -5},
		{-5,  5,  3,  8,  8,  3,  5, -5},
		{-5,  3, 10,  3,  3, 10,  3, -5},
		{-5,  3, 10,  3,  3, 10,  3, -5},
		{-5,  5,  3,  8,  8,  3,  5, -5},
		{-5, 10,  5,  8,  8,  5, 10, -5},
		{-5, -5, -5, -5, -5, -5, -5, -5}
	};
	private static int knightpos[][] =
	{
		{-10, -5, -5, -5, -5, -5, -5,-10},
		{ -8,  0,  0,  3,  3,  0,  0, -8},
		{ -8,  0, 10,  8,  8, 10,  0, -8},
		{ -8,  0,  8, 10, 10,  8,  0, -8},
		{ -8,  0,  8, 10, 10,  8,  0, -8},
		{ -8,  0, 10,  8,  8, 10,  0, -8},
		{ -8,  0,  0,  3,  3,  0,  0, -8},
		{-10, -5, -5, -5, -5, -5, -5,-10}
	};
	private static int pawnpos[][] =
	{
		{0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0, -5, -5,  0,  0,  0},
		{0,  2,  3,  4,  4,  3,  2,  0},
		{0,  4,  6, 10, 10,  6,  4,  0},
		{0,  6,  9, 10, 10,  9,  6,  0},
		{4,  8, 12, 16, 16, 12,  8,  4},
		{5, 10, 15, 20, 20, 15, 10,  5},
		{0,  0,  0,  0,  0,  0,  0,  0}
	};
	
	/* Material value of a piece */
	private static final int kingval      = 350;
	private static final int queenval     = 900;
	private static final int rookval      = 500;
	private static final int bishopval    = 300;
	private static final int knightval    = 300;
	private static final int pawnval      = 100;
	//private static final int emptyval     = 0;
	
	/* The bonus for castling */
	private static final int CASTLE_BONUS = 10;
}
