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
