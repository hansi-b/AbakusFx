package abakus


import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class ÖtvCsvParser {

    private static final Charset utf8 = StandardCharsets.UTF_8
    private static final String ötvCsv = "ötv.csv"

    ÖtvCsvParser() {
    }

    File getResourceFile(String fileName) {
        def res = getClass().getClassLoader().getResource(fileName)
        new File(URLDecoder.decode(res.getFile(), utf8))
    }

    Tarif parseTarif() {
        def tarifMap = Files.lines(getResourceFile(ötvCsv).toPath()).map(String::trim).filter((String l) -> !l.isEmpty() && !l.startsWith("#"))
                .collect {
                    def parts = it.split('\t')
                    if (parts.size() != 9)
                        throw new IllegalStateException("Expected 9 parts, but got only ${parts.size()} in '$parts'")
                    def jahr = Integer.valueOf(parts[0])
                    def gruppe = Gruppe.valueOf(parts[1])

                    def sz = Help.toBigDec(parts[2])
                    def bruttos = parts[3..-1].collect { Help.toEuro(it) }

                    def guj = new GruppeUndJahr(gruppe, jahr)
                    def geh = new Gehälter(sz, [Stufe.values(), bruttos].transpose().collectEntries())

                    [guj, geh]
                }.collectEntries()
        new Tarif(gehälter: tarifMap)
    }
}
