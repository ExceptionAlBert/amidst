package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.DotMinecraftDirectoryFinder;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;

@Immutable
public class MojangApiBuilder {
	private final WorldBuilder worldBuilder;
	private final String preferedDotMinecraftDirectory;
	private final String preferedLibraries;
	private final String preferedVersionJar;
	private final String preferedVersionJson;

	public MojangApiBuilder(WorldBuilder worldBuilder,
			String preferedDotMinecraftDirectory, String preferedLibraries,
			String preferedVersionJar, String preferedVersionJson) {
		this.worldBuilder = worldBuilder;
		this.preferedDotMinecraftDirectory = preferedDotMinecraftDirectory;
		this.preferedLibraries = preferedLibraries;
		this.preferedVersionJar = preferedVersionJar;
		this.preferedVersionJson = preferedVersionJson;
	}

	@NotNull
	public MojangApi construct() throws FileNotFoundException,
			LocalMinecraftInterfaceCreationException {
		DotMinecraftDirectory dotMinecraftDirectory = createDotMinecraftDirectory();
		if (!dotMinecraftDirectory.isValid()) {
			throw new FileNotFoundException(
					"Unable to find valid minecraft directory at: "
							+ dotMinecraftDirectory.getRoot());
		}
		MojangApi result = new MojangApi(worldBuilder, dotMinecraftDirectory,
				readRemoteOrLocalVersionList(), createPreferedJson());
		result.set(createProfileDirectory(), createVersionDirectory(result));
		return result;
	}

	@NotNull
	private DotMinecraftDirectory createDotMinecraftDirectory() {
		if (preferedLibraries != null) {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory),
					new File(preferedLibraries));
		} else {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory));
		}
	}

	@NotNull
	private VersionListJson readRemoteOrLocalVersionList()
			throws FileNotFoundException {
		return JsonReader.readRemoteOrLocalVersionList();
	}

	private File createPreferedJson() {
		if (preferedVersionJson != null) {
			File result = new File(preferedVersionJson);
			if (result.isFile()) {
				return result;
			}
		}
		return null;
	}

	private ProfileDirectory createProfileDirectory() {
		if (preferedDotMinecraftDirectory != null) {
			ProfileDirectory result = new ProfileDirectory(new File(
					preferedDotMinecraftDirectory));
			if (result.isValid()) {
				return result;
			}
		}
		return null;
	}

	private VersionDirectory createVersionDirectory(MojangApi mojangApi) {
		if (preferedVersionJar != null) {
			File jar = new File(preferedVersionJar);
			File json = new File(getJsonFileName());
			VersionDirectory result = mojangApi.createVersionDirectory(jar,
					json);
			if (result.isValid()) {
				return result;
			}
		}
		return null;
	}

	private String getJsonFileName() {
		return preferedVersionJar.substring(0, preferedVersionJar.length() - 4)
				+ ".json";
	}
}