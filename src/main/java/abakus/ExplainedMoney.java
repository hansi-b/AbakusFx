package abakus;

import java.text.NumberFormat;
import java.util.Objects;

import org.javamoney.moneta.Money;

import abakusfx.Converters;

public class ExplainedMoney {

	private final NumberFormat numberFormat = Constants.getNumberFormat();

	private final Money money;
	private final String explained;
	private final boolean isElementary;

	private ExplainedMoney(final Money money, final String explained, final boolean isElementary) {
		this.money = money;
		this.explained = explained;
		this.isElementary = isElementary;
	}

	public Money money() {
		return money;
	}

	public String explain() {
		return explained;
	}

	public static ExplainedMoney of(final Money money, final String explain) {
		return new ExplainedMoney(money, String.format("%s %s", Converters.moneyConverter.toString(money), explain),
				true);
	}

	public ExplainedMoney add(final ExplainedMoney other) {
		final String newExplain = String.format("%s + %s", quotedExpl(), other.quotedExpl());
		return new ExplainedMoney(money.add(other.money()), newExplain, false);
	}

	public ExplainedMoney multiplyPercent(final Number percent, final String explain) {
		final String newExplain = String.format("%s × %s%%%s", quotedExpl(), numberFormat.format(percent),
				explainSuffix(explain));
		return new ExplainedMoney(money.multiply(percent).divide(100), newExplain, false);
	}

	public ExplainedMoney addPercent(final Number percent, final String explain) {
		final String suffix = String.format("%s + %s%%%s", quotedExpl(), numberFormat.format(percent),
				explainSuffix(explain));
		final Money zuschlag = money.multiply(percent).divide(100);
		return new ExplainedMoney(money.add(zuschlag), suffix, false);
	}

	private static String explainSuffix(final String explain) {
		return explain == null || explain.isEmpty() ? "" : String.format(" %s", explain);
	}

	private String quotedExpl() {
		return isElementary ? explained : String.format("( %s )", explained);
	}

	@Override
	public int hashCode() {
		return Objects.hash(money, explained, isElementary);
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
				isElementary == other.isElementary;
	}

	@Override
	public String toString() {
		return String.format("ExplainedMoney[%s, \"%s\"]", money, explained);
	}
}
