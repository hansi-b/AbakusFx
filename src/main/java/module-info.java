module hansi_abakusFx {
	requires java.prefs;

	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.yaml;
	requires org.apache.logging.log4j;

	requires org.javamoney.moneta;

	opens abakusfx to javafx.graphics;
}