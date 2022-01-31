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
package utils;

public class HelpUtils {

	private HelpUtils() {
		// instantiation prevention
	}

	public static String csvTarifToHtmlTable(String tarifCsvString) {

		boolean foundHeader = false;
		StringBuilder htmlResult = new StringBuilder("<table>\n");
		String[] lines = tarifCsvString.split("\n");
		if (lines.length == 0)
			throw new IllegalStateException("Found no lines");

		for (String l : lines) {
			if (l.startsWith("#")) {
				if (foundHeader)
					throw new IllegalStateException("Found second header");
				foundHeader = true;
				htmlResult.append("<tr><th>").append(l.replace("\t", "</th><th>").replaceFirst("^#\s+", ""))
						.append("</th></tr>\n");
			} else {
				if (!foundHeader)
					throw new IllegalStateException("Expected header first");
				htmlResult.append("<tr><td>").append(l.replace("\t", "</td><td>")).append("</td></tr>\n");
			}
		}
		return htmlResult.append("</table>\n").toString();
	}
}
