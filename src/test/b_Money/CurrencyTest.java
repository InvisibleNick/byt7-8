package test.b_Money;

import static org.junit.Assert.*;

import b_Money.Currency;
import org.junit.Before;
import org.junit.Test;

public class CurrencyTest {
	Currency SEK, DKK, NOK, EUR;
	
	@Before
	public void setUp() throws Exception {
		/* Setup currencies with exchange rates */
		SEK = new Currency("SEK", 0.15);
		DKK = new Currency("DKK", 0.20);
		EUR = new Currency("EUR", 1.5);
	}


	@Test
	public void testGetName() {
		assertEquals("SEK", SEK.getName());
		assertEquals("DKK", DKK.getName());
		assertEquals("EUR", EUR.getName());
	}
	
	@Test
	public void testGetRate() {
		float tolerance = 0.0001f;
		assertTrue(SEK.getRate() - 0.15 < tolerance);
		assertTrue(DKK.getRate() - 0.2 < tolerance);
		assertTrue(EUR.getRate() - 1.5 < tolerance);
	}
	
	@Test
	public void testSetRate() {
		SEK.setRate(1.25);
		DKK.setRate(22.2);
		EUR.setRate(0.5);

		float tolerance = 0.0001f;
		assertTrue(SEK.getRate() - 1.25 < tolerance);
		assertTrue(DKK.getRate() - 22.2 < tolerance);
		assertTrue(EUR.getRate() - 0.5 < tolerance);

		SEK.setRate(0.15);
		DKK.setRate(0.2);
		EUR.setRate(1.5);
	}
	
	@Test
	public void testGlobalValue() {
		assertEquals(15L, (long)SEK.universalValue(100));
		assertEquals(150L, (long)EUR.universalValue(100));
		assertEquals(20L, (long)DKK.universalValue(100));
	}
	
	@Test
	public void testValueInThisCurrency() {
		assertEquals(1000L, (long)SEK.valueInThisCurrency(100, EUR));
		assertEquals(133L, (long)SEK.valueInThisCurrency(100, DKK));

		assertEquals(10L, (long)EUR.valueInThisCurrency(100, SEK));
		assertEquals(13L, (long)EUR.valueInThisCurrency(100, DKK));

		assertEquals(75L, (long)DKK.valueInThisCurrency(100, SEK));
		assertEquals(750L, (long)DKK.valueInThisCurrency(100, EUR));
	}

}
