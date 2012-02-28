package edu.cmu.cs211.chess.search;

import edu.cmu.cs211.chess.board.ArrayMove;
import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.*;

/**
 * My implementation of Alpha Beta search.
 * -negamax
 * -alpha beta pruning
 * -iterative deepening with move ordering
 * -repetition table
 */
public class MySearcher<M extends Move<M>,B extends Board<M,B>> extends AbstractSearcher<M,B> {
	// instance variables
	// keeps track of best move
	M bestMove;
	Hashtable<Integer,M> bestMoveMap;
	Hashtable<Long,Integer> countMap;
	Cmp cmp;
	
	int currentMaxDepth;
	boolean depthCompleted;
	
	public MySearcher(){
		bestMoveMap = new Hashtable<Integer,M>();
		countMap = new Hashtable<Long,Integer>(1000);
		this.cmp = new Cmp();
	}

	public M getBestMove(B board, int myTime, int opTime){
		bestMove = null;
		bestMoveMap.clear();
		depthCompleted = false;
		
		long sig = board.signature();
		increaseCount(sig);
		
		// start timer
		timer.start(myTime, opTime);
		System.out.println("ply" + board.plyCount());
		currentMaxDepth = minDepth;
		while(!(timer.timeup()) && currentMaxDepth <= maxDepth){
			depthCompleted = true;
			negamax(board, currentMaxDepth, -evaluator.infty(), evaluator.infty());

			System.out.println("currentMaxDepth = " + currentMaxDepth + "   bestMove " + bestMove);
			currentMaxDepth++;
		}

		System.out.println(bestMoveMap);
		if(depthCompleted == false)
			bestMove = bestMoveMap.get(currentMaxDepth -2);
		
		
		// keep track of moves made by you
		// increase count for best move
		board.applyMove(bestMove);
		sig = board.signature();
		increaseCount(sig);
		board.undoMove();
		
		return bestMove;
	}

	// negamax with alpha beta pruning
	private int negamax(B board, int depth, int alpha, int beta){

		if(timer.timeup()){
			depthCompleted = false;
			return evaluator.eval(board);
		}
		
		if (depth == 0) // depth reached
			return qSearch(board, 4, alpha, beta); // search 4 depth for q search

		// check count of this move
		if(depth <= currentMaxDepth-1 &&  getCount(board.signature()) >= 2){
			return 0;
		}
		
		
		// get all moves
		List<M> moves = board.generateMoves();
		Collections.sort(moves, this.cmp);

		// no legal moves, checkmate or stalemate
		if (moves.isEmpty()){
			if (board.inCheck()) 
				return -evaluator.mate() - depth;
			else
				return -evaluator.stalemate();
		}

		if(currentMaxDepth > minDepth && depth == currentMaxDepth){
			moves.add(0,bestMove);
		}
		
		// iterate through all moves
		for (M move : moves){
			board.applyMove(move);
			
			// call negamax on next move
			int nega = -negamax(board, depth-1, -beta, -alpha);
			// update best move (only the move at max depth my next move)
			if(depth == currentMaxDepth  && alpha < nega){
				bestMove = move;
				bestMoveMap.put(currentMaxDepth,move);
			}
			// update alpha
			alpha = Math.max(alpha, nega);

			board.undoMove();
			// alpha beta pruning
			if(alpha >= beta)
				return alpha;
		}


		return alpha;
	}
	
	
	private int qSearch( B board, int depth, int alpha, int beta) {
		int standpat = evaluator.eval(board);

		if (depth <= 0 || this.timer.timeup()) return standpat;

	    if( standpat >= beta ) return beta;
	    if( alpha < standpat ) alpha = standpat;
	    
	//    long thisSig = board.signature();
	//	if (this.getRepCount(thisSig) > MySearcher.allowedReps){
	//		return this.evaluator.infty() * (alpha >=0 ? -1 : 1);
	//	}
	 
		List<M> moves = board.generateMoves();
		for (M move : moves){
			if (!move.isCapture() && !move.isPromotion()) continue;
			
			board.applyMove(move);

			int score = -qSearch( board, depth-1, -beta, -alpha );
			
			board.undoMove();
			
	        if( score >= beta ) return beta;
	        if( score > alpha ) alpha = score;
	    }
		
	    return alpha;
	}
	
	
	
	
	private void increaseCount(long sig){
		// first encountered position
		if(!countMap.containsKey(sig))
			countMap.put(sig,1);
		else
			countMap.put(sig,countMap.get(sig)+1);
	}
	
	private void decreaseCount(long sig){
		// prevent error
		if(!countMap.containsKey(sig))
			return;
		else
			countMap.put(sig,countMap.get(sig)-1);
	}
	
	private int getCount(long sig){
		if(!countMap.containsKey(sig))
			return 0;
		else
			return countMap.get(sig);
		
	}
	
	
	private static class Cmp<M extends Move<M>> implements Comparator<M>{
		public int compare(M arg0, M arg1) {
			if (arg0.isCapture() && !arg1.isCapture()) return -1;
			else if (!arg0.isCapture() && arg0.isCapture()) return 1;
			else return 0;
		}
	}

}
