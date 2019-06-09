package fr.klemek.primedate;

import static org.junit.Assert.*;

import org.junit.Test;

public class PrimeCalculatorTests {

	public void testNumber(boolean expected, long number) throws Exception {
		if(expected)
			assertTrue(number+" should be prime", PrimeCalculator.isPrime(number));
		else
			assertFalse(number+" should not be prime", PrimeCalculator.isPrime(number));
	}
	
	@Test
	public void testIsPrimeSmall() throws Exception {
		testNumber(true, 1);
		testNumber(true, 2);
		testNumber(true, 3);
		testNumber(false, 4);
		testNumber(true, 5);
		testNumber(false, 6);
		testNumber(true, 7);
		testNumber(false, 9);
	}

	@Test
	public void testIsPrimeNormal() throws Exception {
		testNumber(true, 8011);
		testNumber(true, 8941);
		testNumber(false, 8943);
		testNumber(true, 9283);
	}
	
	@Test
	public void testIsPrimeBig() throws Exception {
		testNumber(true, 201802181381L);
		testNumber(false, 201802181383L);
		testNumber(false, 201802181307L);
		testNumber(false, 201802181409L);
		testNumber(true, 201802181411L);
	}
}
