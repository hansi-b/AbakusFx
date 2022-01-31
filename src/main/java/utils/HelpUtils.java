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
				htmlResult.append("<tr><th>").append(l.replaceAll("\t", "</th><th>").replaceFirst("^#\s+", ""))
						.append("</th></tr>\n");
			} else {
				if (!foundHeader)
					throw new IllegalStateException("Expected header first");
				htmlResult.append("<tr><td>").append(l.replaceAll("\t", "</td><td>")).append("</td></tr>\n");
			}
		}
		return htmlResult.append("</table>\n").toString();
	}
}
