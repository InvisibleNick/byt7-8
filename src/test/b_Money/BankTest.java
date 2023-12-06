package test.b_Money;

import static org.junit.Assert.*;

import b_Money.*;
import org.junit.Before;
import org.junit.Test;

public class BankTest {
	Currency SEK, DKK;
	Bank SweBank, Nordea, DanskeBank;

	@Before
	public void setUp() throws Exception {
		DKK = new Currency("DKK", 0.20);
		SEK = new Currency("SEK", 0.15);
		SweBank = new Bank("SweBank", SEK);
		Nordea = new Bank("Nordea", SEK);
		DanskeBank = new Bank("DanskeBank", DKK);
		SweBank.openAccount("Ulrika");
		SweBank.openAccount("Bob");
		Nordea.openAccount("Bob");
		DanskeBank.openAccount("Gertrud");
	}


	@Test
	public void testGetName() {
		assertEquals("SweBank", SweBank.getName());
		assertEquals("Nordea", Nordea.getName());
		assertEquals("DanskeBank", DanskeBank.getName());
	}

	@Test
	public void testGetCurrency() {
		assertEquals(SEK, SweBank.getCurrency());
		assertEquals(SEK, Nordea.getCurrency());
		assertEquals(DKK, DanskeBank.getCurrency());
	}

	/**
	 * FOUND:
	 * 1. When opening account in bank, the bank gets value, not puts - FIXED
	 */
	@Test
	public void testOpenAccount() throws AccountExistsException, AccountDoesNotExistException {
		SweBank.openAccount("TestOpen");
		Nordea.openAccount("TestOpen");
		DanskeBank.openAccount("TestOpen");

		assertEquals(0, (long)SweBank.getBalance("TestOpen"));
		assertEquals(0, (long)Nordea.getBalance("TestOpen"));
		assertEquals(0, (long)DanskeBank.getBalance("TestOpen"));
	}

	/**
	 * FOUND:
	 * 1. Deposit operation don't find accounts - FIXED
	 */
	@Test
	public void testDeposit() throws AccountDoesNotExistException {
		SweBank.deposit("Ulrika", new Money(100, SEK));
		SweBank.deposit("Bob", new Money(100, DKK));
		Nordea.deposit("Bob", new Money(100, SEK));
		DanskeBank.deposit("Gertrud", new Money(100, DKK));

		assertEquals(100L, (long)SweBank.getBalance("Ulrika"));
		assertEquals(133L, (long)SweBank.getBalance("Bob"));
		assertEquals(100L, (long)Nordea.getBalance("Bob"));
		assertEquals(100L, (long)DanskeBank.getBalance("Gertrud"));
	}

	/**
	 * FOUND:
	 * 1. Withdraw operation adds money, not removes - FIXED
	 */
	@Test
	public void testWithdraw() throws AccountDoesNotExistException {
		SweBank.withdraw("Ulrika", new Money(100, SEK));
		SweBank.withdraw("Bob", new Money(100, DKK));
		Nordea.withdraw("Bob", new Money(100, SEK));
		DanskeBank.withdraw("Gertrud", new Money(100, DKK));

		assertEquals(-100L, (long)SweBank.getBalance("Ulrika"));
		assertEquals(-133L, (long)SweBank.getBalance("Bob"));
		assertEquals(-100L, (long)Nordea.getBalance("Bob"));
		assertEquals(-100L, (long)DanskeBank.getBalance("Gertrud"));
	}
	
	@Test
	public void testGetBalance() throws AccountDoesNotExistException {
		assertEquals(0, (long)SweBank.getBalance("Ulrika"));
		assertEquals(0, (long)SweBank.getBalance("Bob"));
		assertEquals(0, (long)Nordea.getBalance("Bob"));
		assertEquals(0, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.deposit("Ulrika", new Money(100, SEK));
		SweBank.deposit("Bob", new Money(100, DKK));
		Nordea.deposit("Bob", new Money(100, SEK));
		DanskeBank.deposit("Gertrud", new Money(100, DKK));

		assertEquals(100L, (long)SweBank.getBalance("Ulrika"));
		assertEquals(133L, (long)SweBank.getBalance("Bob"));
		assertEquals(100L, (long)Nordea.getBalance("Bob"));
		assertEquals(100L, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.withdraw("Ulrika", new Money(100, SEK));
		SweBank.withdraw("Bob", new Money(100, DKK));
		Nordea.withdraw("Bob", new Money(100, SEK));
		DanskeBank.withdraw("Gertrud", new Money(100, DKK));

		assertEquals(0, (long)SweBank.getBalance("Ulrika"));
		assertEquals(0, (long)SweBank.getBalance("Bob"));
		assertEquals(0, (long)Nordea.getBalance("Bob"));
		assertEquals(0, (long)DanskeBank.getBalance("Gertrud"));
	}

	/**
	 * FOUND:
	 * 1. If transfer money in the same bank - do not transfer - FIXED
	 */
	@Test
	public void testTransfer() throws AccountDoesNotExistException {
		SweBank.deposit("Ulrika", new Money(100, SEK));

		assertEquals(100L, (long)SweBank.getBalance("Ulrika"));
		assertEquals(0, (long)SweBank.getBalance("Bob"));

		SweBank.transfer("Ulrika", "Bob", new Money(100, SEK));

		assertEquals(0, (long)SweBank.getBalance("Ulrika"));
		assertEquals(100L, (long)SweBank.getBalance("Bob"));

		SweBank.transfer("Bob", DanskeBank, "Gertrud", new Money(100, SEK));

		assertEquals(0, (long)SweBank.getBalance("Bob"));
		assertEquals(75L, (long)DanskeBank.getBalance("Gertrud"));
	}

	/**
	 * FOUND:
	 * 1. Not really a problem, but thing to notice: interval starts with 0, so interval 2 = actually 3 ticks
	 */
	@Test
	public void testTimedPayment() throws AccountDoesNotExistException {
		SweBank.deposit("Bob", new Money(100, SEK));
		SweBank.addTimedPayment("Bob", "?", 2, 2, new Money(100, SEK), DanskeBank, "Gertrud");

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0L, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.tick();

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0L, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.tick();

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0L, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.tick();

		assertEquals(0, (long)SweBank.getBalance("Bob"));
		assertEquals(75L, (long)DanskeBank.getBalance("Gertrud"));

		SweBank.deposit("Bob", new Money(100, SEK));
		SweBank.addTimedPayment("Bob", "?", 2, 2, new Money(100, SEK), SweBank, "Ulrika");

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0, (long)SweBank.getBalance("Ulrika"));

		SweBank.tick();

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0, (long)SweBank.getBalance("Ulrika"));

		SweBank.tick();

		assertEquals(100L, (long)SweBank.getBalance("Bob"));
		assertEquals(0, (long)SweBank.getBalance("Ulrika"));

		SweBank.tick();

		assertEquals(0, (long)SweBank.getBalance("Bob"));
		assertEquals(100L, (long)SweBank.getBalance("Ulrika"));
	}
}
