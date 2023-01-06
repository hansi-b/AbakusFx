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

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AppTitle {
	private static final Logger log = LogManager.getLogger();

	private final Consumer<String> titleHandler;

	private String project;
	private boolean isDirty;

	AppTitle(final Consumer<String> titleHandler) {
		this.titleHandler = titleHandler;
		this.project = null;
		this.isDirty = false;
	}

	void updateProject(final String newProject) {
		log.debug("Updating title: project = '{}'", newProject);
		this.project = newProject;
		updateTitle();
	}

	void updateIsDirty(final boolean newIsDirty) {
		log.debug("Updating title: isDirty = '{}'", newIsDirty);
		this.isDirty = newIsDirty;
		updateTitle();
	}

	private void updateTitle() {
		String pName = project;
		if (pName != null && pName.endsWith(".aba"))
			pName = pName.substring(0, pName.length() - 4);
		final String projectPart = pName != null ? String.format(": %s", pName) : "";
		final String dirtyPart = isDirty ? "*" : "";
		titleHandler.accept(String.format("Abakus%s%s", projectPart, dirtyPart));
	}
}
