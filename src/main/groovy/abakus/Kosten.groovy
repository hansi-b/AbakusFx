package abakus

import org.javamoney.moneta.Money

class Kosten {

    /**
     * fixer Prozentsatz, der als Arbeitgeber-Kostenzuschlag genommen wird
     */
    static final BigDecimal zuschlagProzent = Constants.percent(130)

    private final Tarif tarif

    Kosten(Tarif tarif) {
        this.tarif = tarif
    }

    Money kosten(Gruppe gruppe, Stufe stufe, int jahr, BigDecimal umfang) {
        tarif.brutto(gruppe, stufe, jahr) * zuschlagProzent * Constants.percent(umfang)
    }
}
