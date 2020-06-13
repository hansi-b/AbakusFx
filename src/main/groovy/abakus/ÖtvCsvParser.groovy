package abakus

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ÖtvCsvParser {

    private static final Charset utf8 = StandardCharsets.UTF_8
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
            throw new IllegalStateException("Expected 9 parts, but got only ${parts.size()} in '$parts'")
        def jahr = Integer.valueOf(parts[0])
        def gruppe = Gruppe.valueOf(parts[1])

        def sz = Help.toBigDec(parts[2])
        def bruttos = parts[3..-1].collect { Help.toEuro(it) }

        def guj = new GruppeUndJahr(gruppe, jahr)
        def geh = new Gehälter(sz, [Stufe.values(), bruttos].transpose().collectEntries())

        [guj, geh]
    }
}
