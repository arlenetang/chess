package edu.cmu.cs211.chess.tests.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;

import static org.junit.Assert.*;

/**
 * This class aids in testing the Board interface.
 * 
 * You do not need to edit this file.  It is provided
 * in case you want to write your own board and use
 * similar tests on it.
 * 
 * FrontDesk will ignore any changes you make to this
 * file.
 */
public class LegalMoveTestUtil
{
  private static List<String> allSquareStrings = new ArrayList<String>(64);
  static {    
    //Create all squares squares
    for(int si = 0; si < 8; ++si)
    {
      for(int sj = 0; sj < 8; ++sj)
      {
        String sq  = ("" + (char)('a'+si)) + (sj+1);
        allSquareStrings.add(sq);
      }
    }
  }
  
  public static
  <
    M extends Move<M>,
    B extends Board<M,B>
  >
  void checkAll(B b)
  {    
    for( String fen : PerftTestUtil.database.keySet())
    {
      b = b.init(fen);
      checkLegalMoveConsistency(b);
    }
  }
  
  /**
   * All moves generated by the move generator should be
   * legal, and all other moves should be illegal.
   */
  public static
  <
    M extends Move<M>,
    B extends Board<M,B>
  >
  void checkLegalMoveConsistency(B b)
  {
    Set<M>      genMoves    = new HashSet<M>(b.generateMoves());
    Set<String> stringMoves = new HashSet<String>();
    Set<String> foundLegalMoves = new HashSet<String>();
    int         legalCount  = 0;
    
    for( M m : genMoves )
    {
      stringMoves.add(m.serverString());
    }
    
    String[] captures = {
        "k",
        "q",
        "r",
        "b",
        "n",
        "p",
        "E",
        "C",
        "c",
        ""
    };
    
    String[] promotes = {
        "K",
        "Q",
        "R",
        "B",
        "N",
        "P",
        "" // no promote
    };
    
    for( String src : allSquareStrings )
    {
      for( String dest : allSquareStrings )
      {
        for( String promote : promotes )
        {
          for( String capture : captures )
          {
            String  smithString = src+dest+capture+promote;
            String  eqString    = src+dest+ ( promote.length() == 0 ? "" : "=" + promote );
            M       move       = null;
            boolean exception  = false;
            
            String  errorString = b.toString()
              + "\nFen: "  + b.fen()
              + "\nSmithMove: " + smithString
              + "\nEqString: " + eqString;
            
            try
            {
              move = b.createMoveFromString(smithString);
              errorString += "\nMoveStringRoundTrip: " + move.serverString() + "\n";
              assertEquals(eqString, move.serverString());
            }
            catch (Exception e)
            {
              exception = true;
            }
            catch (AssertionError e)
            {
              exception = true;
            }
            
            if( exception )
            {
              if(genMoves.contains(move))
              {
            	  fail("Exception thrown for legal move" + errorString);
              }
            }
            else
            {
              boolean legalMove   = b.isLegalMove(move);
              
              assertEquals( errorString, genMoves.contains(move), legalMove );
              assertEquals( errorString, stringMoves.contains(eqString), legalMove );
              
              if( legalMove && foundLegalMoves.add(eqString) )
              {
                ++legalCount;
              }
            }
          }
        }
      }
    }
    
    assertEquals( b.toString() + "\n" + foundLegalMoves.toString(), genMoves.size(), legalCount );
  }
}
