package abakus

class ÖtvCsvParser {

    private static final String ötvCsv = "ötv.csv"

    ÖtvCsvParser() {
    }

    Tarif parseTarif() {
        new Tarif(gehälter: parseGehälter())
    }

    private def parseGehälter() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ötvCsv)))) {
            return br.lines().map(String::trim).filter((String l) -> !l.isEmpty() && !l.startsWith("#"))
                    .collect { parseLine(it) }.collectEntries()
        }
    }

    private def parseLine(String csvLine) {
        def parts = csvLine.split('\t')
        if (parts.size() != 9)
            throw new IllegalStateException("Expected 9 parts, but got ${parts.size()}: '$parts'")
        def jahr = Integer.valueOf(parts[0])
        def gruppe = Gruppe.valueOf(parts[1])

        def sz = Constants.toBigDec(parts[2])
        def bruttos = parts[3..-1].collect { Constants.toEuro(it) }

        [new GruppeUndJahr(gruppe, jahr), new Gehälter(sz, [Stufe.values(), bruttos].transpose().collectEntries())]
    }
}
