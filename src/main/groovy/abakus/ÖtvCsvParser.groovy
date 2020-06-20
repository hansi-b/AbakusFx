package abakus

class ÖtvCsvParser {

    private static final String ötvCsv = "ötv.csv"

    ÖtvCsvParser() {
    }

    Tarif parseTarif() {
        new Tarif(gehälter: parseGehälter())
    }

    private Map<Gruppe, Map<Integer, Gehälter>> parseGehälter() {
        Map<Gruppe, Map<Integer, Gehälter>> gehälterMap = new HashMap<>()
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ötvCsv)))) {
            br.lines().map(String::trim).filter((String l) -> !l.isEmpty() && !l.startsWith("#"))
                    .collect {
                        def (Gruppe gruppe, int jahr, Gehälter gehälter) = parseLine(it)
                        def gruppeMap = gehälterMap.computeIfAbsent(gruppe, g -> new HashMap<>())
                        if (gruppeMap.containsKey(jahr))
                            throw new IllegalArgumentException("Doppelte Daten für Gruppe ${gruppe} im Jahr ${jahr}")
                        gruppeMap.put(jahr, gehälter)
                    }
        }
        return gehälterMap
    }

    private def parseLine(String csvLine) {
        def parts = csvLine.split('\t')
        if (parts.size() != 9)
            throw new IllegalStateException("Expected 9 parts, but got ${parts.size()}: '$parts'")
        def jahr = Integer.valueOf(parts[0])
        def gruppe = Gruppe.valueOf(parts[1])

        def sz = Constants.percent(Constants.toBigDec(parts[2]))
        def bruttos = parts[3..-1].collect { Constants.toEuro(it) }

        [gruppe, jahr, new Gehälter(sz, [Stufe.values(), bruttos].transpose().collectEntries())]
    }
}
