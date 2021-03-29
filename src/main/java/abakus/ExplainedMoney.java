package abakus;

import java.util.Objects;

import org.javamoney.moneta.Money;

public class ExplainedMoney {

	private final Money money;
	private final String explained;
	private final boolean elementary;

	private ExplainedMoney(final Money money, final String explained, final boolean elementary) {
		this.money = money;
		this.explained = explained;
		this.elementary = elementary;
	}

	public Money money() {
		return money;
	}

	public String explain() {
		return explained;
	}

	public static ExplainedMoney of(final Money money, final String explain) {
		return new ExplainedMoney(money, String.format("%s %s", money, explain), true);
	}

	public ExplainedMoney multiplyPercent(final Number percent, final String explainPercent) {
		final String newExplain = String.format("%s × %s%% %s", quotedExpl(), percent, explainPercent);
		return new ExplainedMoney(money.multiply(percent).divide(100), newExplain, false);
	}

	public ExplainedMoney addPercent(final Number percent, final String explainPercent) {
		final String newExplain = String.format("%s + %s%% %s", quotedExpl(), percent, explainPercent);
		Money zuschlag = money.multiply(percent).divide(100);
		return new ExplainedMoney(money.add(zuschlag), newExplain, false);
	}

	private String quotedExpl() {
		return elementary ? explained : String.format("( %s )", explained);
	}

	@Override
	public int hashCode() {
		return Objects.hash(money, explained, elementary);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final ExplainedMoney other = (ExplainedMoney) obj;

		return Constants.eq(money, other.money) && //
				Constants.eq(explained, other.explained) && //
				elementary == other.elementary;
	}

	@Override
	public String toString() {
		return String.format("ExplainedMoney[%s, \"%s\"]", money, explained);
	}
}
