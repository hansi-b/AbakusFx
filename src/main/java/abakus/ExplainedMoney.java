/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package abakus;

import java.text.NumberFormat;

import org.javamoney.moneta.Money;

import abakusfx.Converters;

public record ExplainedMoney(Money money, String explained, boolean isElementary) {

	private static final NumberFormat numberFormat = Constants.getNumberFormat();

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
}
