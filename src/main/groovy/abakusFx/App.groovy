/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package abakusFx


import abakus.ÖtvCsvParser

class App {

    static void main(String[] args) {
        def tarif = new ÖtvCsvParser().parseTarif()

        println tarif
    }
}
