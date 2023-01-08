package abakusfx;

import org.hansib.sundries.fx.table.CsvCopyTableEnabler.MenuItemsLocalizer;

class L10n {

	public static MenuItemsLocalizer csvTableMenuItemsLocalizer = new MenuItemsLocalizer() {

		@Override
		public String selectAll() {
			return "Alles auswählen";
		}

		@Override
		public String copySelection() {
			return "Auswahl kopieren";
		}
	};
}