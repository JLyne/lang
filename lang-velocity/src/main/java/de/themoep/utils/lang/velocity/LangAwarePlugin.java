package de.themoep.utils.lang.velocity;

import java.nio.file.Path;
import java.util.logging.Logger;

public interface LangAwarePlugin {
	public Logger getLogger();

	public Path getDataFolder();

	public String getName();
}
