package edu.cmu.cs211.chess.tests;

import org.junit.Test;

/**
 * This class tests the version of alpha beta you are
 * required to write for the unit tests on FrontDesk.
 * 
 * You should be able to add any tests that you are
 * failing on FrontDesk here and debug them locally.
 */
public class AlphaBetaTest {

	@Test
	public void alphaBetaCheckmateAndStalemateTest () {
		TestUtil.alphaBetaTest ("5R2/8/8/8/7K/8/Q7/7k w - -",3,
			new String[] {"f8f1"});
	}
	
	@Test
	public void alphaBetaDepth2Test () {
		TestUtil.alphaBetaTest ("r1bq1b1r/pppkpppp/3p4/8/8/P2PP2P/1PP2PP1/RNB1KBNR b KQ -",2,
			new String[] {"e7e5"});
	}
	
	@Test
	public void alphaBetaDepth3Test () {
		TestUtil.alphaBetaTest ("2b2k2/1rqpn2B/n3p1r1/P1p1P1p1/PN6/3PP2P/1B2K3/3Rb1NR b - -",3,
			new String[] {"e1b4", "b7b4","a6b4"});
	}
	
	@Test
	public void alphaBetaDepth4Test () {
		TestUtil.alphaBetaTest ("rN3Bn1/2p2k2/pp2p2r/2P2bpp/P3Pp1P/1P1P4/N4P2/RQ1K1B1R w - -",4,
			new String[] {"f8h6"});
	}
}
