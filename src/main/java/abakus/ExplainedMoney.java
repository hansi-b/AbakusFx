package abakus;

import java.util.Objects;

import org.javamoney.moneta.Money;

public class ExplainedMoney {

	private final Money money;
	private final String explained;

	private ExplainedMoney(final Money money, final String explained) {
		this.money = money;
		this.explained = explained;
	}

	public Money money() {
		return money;
	}

	public String explain() {
		return explained;
	}

	public static ExplainedMoney of(final Money money, final String explain) {
		return new ExplainedMoney(money, explain);
	}

	public ExplainedMoney multiplyPercent(final Number percent, final String explainPercent) {
		return new ExplainedMoney(money.multiply(percent).divide(100),
				String.format("%s × %s%% %s", explained, percent, explainPercent));
	}

	@Override
	public int hashCode() {
		return Objects.hash(money, explained);
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
				Constants.eq(explained, other.explained);
	}
}
