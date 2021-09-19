package abakusfx;

import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AppTitle {
	private static final Logger log = LogManager.getLogger();

	private final Stage stage;

	private String project;
	private boolean isDirty;

	AppTitle(final Stage stage) {
		this.stage = stage;
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
		stage.setTitle(String.format("Abakus%s%s", projectPart, dirtyPart));
	}
}
