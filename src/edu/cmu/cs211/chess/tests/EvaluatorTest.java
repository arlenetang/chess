package edu.cmu.cs211.chess.tests;

import static edu.cmu.cs211.chess.tests.TestUtil.evaluatorTest;
import org.junit.Test;

/**
 * This class tests the version of the evaluator you are
 * required to write for the unit tests on FrontDesk.
 * 
 * You should be able to add any tests that you are
 * failing on FrontDesk here and debug them locally.
 */
public class EvaluatorTest {

	@Test (timeout = 1000)
	public void evaluatorTest0 () {
		evaluatorTest ("rn1k1bnr/p1q1p1p1/1pp2p2/P2p3p/2PP2b1/5PQ1/1P2P1PP/RNB1KBNR w KQ -",-7);
	}
	
	@Test (timeout = 1000)
	public void evaluatorTest1 () {
		evaluatorTest ("1r1qkb1r/1pp1pp1p/7n/p2p2p1/3K2P1/P3P3/N1PPQ1PP/R1B2BNR b kq -",-484);
	}
	
	@Test (timeout = 1000)
	public void evaluatorTest2 () {
		evaluatorTest ("rnbk1b1r/1p1ppppp/p7/P1p5/5PnP/2P4R/RP1PN1P1/1NBQKB2 b KQ -",-811);
	}
	
	
	@Test (timeout = 1000)
	public void evaluatorTest3 () {
		evaluatorTest ("2r2q2/4p1k1/np1N1p1n/BPp3P1/P1P3Pr/3QPR2/3K4/5bN1 w - -",-261);
	}
}
