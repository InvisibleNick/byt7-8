package test.b_Money;

import b_Money.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AccountTest {
	Currency SEK, DKK;
	Bank Nordea;
	Bank DanskeBank;
	Bank SweBank;
	Account testAccount;


	@Before
	public void setUp() throws Exception {
		SEK = new Currency("SEK", 0.15);
		SweBank = new Bank("SweBank", SEK);
		SweBank.openAccount("Alice");
		testAccount = new Account("Hans", SEK);
		testAccount.deposit(new Money(10000000, SEK));

		SweBank.deposit("Alice", new Money(1000000, SEK));
	}


	@Test
	public void testAddRemoveTimedPayment() {
		testAccount.addTimedPayment("?", 2, 2, new Money(100, SEK), SweBank, "Alice");
		testAccount.removeTimedPayment("?");
	}

	/**
	 * FOUND:
	 * 1. One tick of account = 2 ticks of TimePayment - FIXED
	 * 2. Not really a problem, but thing to notice: interval starts with 0, so interval 2 = actually 3 ticks
	 * 3. If TimePayment interval is bellow zero - payment never happens and TimePayment endless - FIXED
	 */
	@Test
	public void testTimedPayment() throws AccountDoesNotExistException {
		testAccount.addTimedPayment("?", 2, 2, new Money(100, SEK), SweBank, "Alice");

		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L, (long)SweBank.getBalance("Alice"));

		testAccount.tick();

		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L, (long)SweBank.getBalance("Alice"));

		testAccount.tick();

		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L, (long)SweBank.getBalance("Alice"));

		testAccount.tick();

		assertEquals(10000000L - 100, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L + 100, (long)SweBank.getBalance("Alice"));

		testAccount.addTimedPayment("???", -2, -2, new Money(100, SEK), SweBank, "Alice");

		SweBank.withdraw("Alice", new Money(100, SEK));
		testAccount.deposit(new Money(100, SEK));

		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L, (long)SweBank.getBalance("Alice"));

		testAccount.removeTimedPayment("?");

		testAccount.tick();
		testAccount.tick();
		testAccount.tick();
		testAccount.tick();
		testAccount.tick();
		testAccount.tick();

		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(1000000L, (long)SweBank.getBalance("Alice"));
	}

	@Test
	public void testAddWithdraw() {
		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());

		testAccount.deposit(new Money(100, SEK));
		assertEquals(10000100L, (long)testAccount.getBalance().getAmount());

		testAccount.withdraw(new Money(100, SEK));
		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
	}
	
	@Test
	public void testGetBalance() {
		assertEquals(10000000L, (long)testAccount.getBalance().getAmount());
		assertEquals(SEK, testAccount.getBalance().getCurrency());
	}
}
