/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2023 Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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