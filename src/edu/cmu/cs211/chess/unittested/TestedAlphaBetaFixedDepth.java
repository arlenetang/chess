package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.*;

/**
 * An implementation of Alpha Beta search.
 * 
 * This is the class that will be unit tested by FrontDesk.
 */
public class TestedAlphaBetaFixedDepth
<
M extends Move<M>,
B extends Board<M,B>
>
extends AbstractSearcher<M,B>
{

	// keeps track of best move
	M bestMove;

	public M getBestMove(B board, int myTime, int opTime){
		negamax(board, maxDepth, -evaluator.infty(), evaluator.infty());
		return bestMove;
	}

	// negamax with alpha beta pruning
	private int negamax(B board, int depth, int alpha, int beta){

		if (depth == 0){
			return evaluator.eval(board);
		}

		// get all moves
		List<M> moves = board.generateMoves();

		// no legal moves, checkmate or stalemate
		if (moves.isEmpty()){
			if (board.inCheck()) 
				return -evaluator.mate() - depth;
			else
				return -evaluator.stalemate();
		}

		// iterate through all moves
		for (M move : moves){
			board.applyMove(move);
			
			// call negamax on next move
			int nega = -negamax(board, depth-1, -beta, -alpha);
			// update best move
			if(depth == maxDepth  && alpha < nega)
				bestMove = move;
			// update alpha
			alpha = Math.max(alpha, nega);
			
			board.undoMove();
			
			// alpha beta pruning
			if(alpha >= beta)
				return alpha;
		}
		
		
		return alpha;
	}

}
