module hansi_abakusFx {
	requires java.prefs;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.yaml;
	requires org.apache.logging.log4j;

	requires transitive org.javamoney.moneta;

	opens abakusfx to javafx.graphics, javafx.fxml;

	exports abakus to com.fasterxml.jackson.databind;
	exports abakusfx.models to com.fasterxml.jackson.databind;
}