/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
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
package fxTools;

import javafx.stage.Stage;
import javafx.stage.Window;

public class Windows {

	/**
	 * @return the last, i.e.g, current focused stage
	 */
	public static Stage findFocusedStage() {
		return Window.getWindows().stream()//
				.map(w -> (w instanceof Stage) ? (Stage) w : null) //
				.filter(w -> w != null && w.isFocused()).reduce((a, b) -> b).orElse(null);
	}
}
