
package quantum625.updater;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Logger;
import java.lang.module.ModuleDescriptor.Version;



/**
 * This will check if a newer version of your plugin is available and download it if wanted
 *
 * @author Quantum625
 * @version 1.0
 */

public class Updater {

    private final JavaPlugin plugin;
    private final String currentVersion;
    private final String projectName;
    private final String projectId;

    private final Logger logger;

    private UpdaterConfig config;

    public UpdateResult updateResult;
    public LinkResult linkResult;

    public Updater(JavaPlugin plugin, File pluginFile, String projectName, String projectId) {
        this.plugin = plugin;
        this.currentVersion = plugin.getPluginMeta().getVersion();
        this.projectName = projectName;
        this.projectId = projectId;
        this.logger = plugin.getLogger();
        config = new UpdaterConfig();
        if (config.updateType.equals(UpdateType.DISABLED)) return;
        updateResult = update(config.releaseType, pluginFile);
    }

    public UpdateResult result() { return updateResult; }
    public LinkResult linkResult() { return linkResult; }

    public LinkResult getLink(ReleaseType type) {
        logger.info("[PluginUpdater] Checking for updates...");
        if (config.updateAllowed()) {
            logger.info("[PluginUpdater] If you wish to disable this auto updater, open serverfiles/plugins/updater.yml and set 'allowUpdates' to false");
        }
        else {
            logger.info("[PluginUpdater] Automatic update installation was disabled, only a version check will be done.");
            logger.info("[PluginUpdater] If you wish to have updates automatically installed, enable 'auto-updates' in the networks config");
        }
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/" + projectId + "/version");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Quantum625/PluginUpdater v1.0 (Updating Plugin '" + projectName + "')");

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();

            Reader streamReader = null;

            if (status > 299) {
                logger.severe("[PluginUpdater] Unable to update plugin! Response code: " + status);
                if (status == 404) return new LinkResult(UpdateResult.INVALID_PROJECT);
                return new LinkResult(UpdateResult.INVALID_CONNECTION_CODE);
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }

            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String answer = content.toString();
            JSONArray json = (JSONArray) JSONValue.parse(answer);

            String newestVersion = "0";
            String newestURL = "";
            String filename = "";
            String sha512 = "";


            try {
                for (Object object : json) {
                    JSONObject jsonObject = (JSONObject) object;
                    String versionNumber = (String) jsonObject.get("version_number");
                    if (config.shouldUpdate(newestVersion, versionNumber) && ReleaseType.parse((String) jsonObject.get("version_type")) == type) {

                        JSONArray gameVersions = (JSONArray) jsonObject.get("game_versions");

                        boolean supported = true;

                        for (Object gv : gameVersions) {
                            if (Bukkit.getMinecraftVersion().equalsIgnoreCase((String) gv)) supported = true;
                        }

                        if (supported) {
                            newestVersion = versionNumber;
                            JSONObject files = (JSONObject) ((JSONArray) jsonObject.get("files")).get(0);
                            newestURL = (String) files.get("url");
                            filename = (String) files.get("filename");
                            sha512 = (String) ((JSONObject) files.get("hashes")).get("sha512");
                        }
                    }
                }
            } catch (NullPointerException | ClassCastException e) {
                e.printStackTrace();
                logger.severe("[PluginUpdater] Invalid response from Modrinth API, cancelling update…");
                return new LinkResult(UpdateResult.INVALID_API_RESPONSE);
            }

            if (newestURL.equals("")) {
                logger.info("[PluginUpdater] Plugin is already up to date!");
                return new LinkResult(UpdateResult.NO_UPDATE);
            }
            if (!filename.endsWith(".jar")) {
                logger.severe("[PluginUpdater] The Plugin file on Modrinth is not a JAR file!, cancelling update…");
                logger.severe("[PluginUpdater] File Name: " + filename);
                return new LinkResult(UpdateResult.ERROR);
            }
            if (!config.shouldUpdate(currentVersion, newestVersion)) {
                logger.info("[PluginUpdater] Plugin is already up to date!");
                return new LinkResult(UpdateResult.NO_UPDATE);
            }


            if (!config.updateAllowed()) {
                logger.info("[PluginUpdater] Automatic update installation is disabled.");
                logger.info("[PluginUpdater] Please manually download the newest version of the plugin here:");
                logger.info("[PluginUpdater] " + url);
                return new LinkResult(UpdateResult.DISABLED);
            }


            logger.info("[PluginUpdater] Found newer version: " + newestVersion);

            return new LinkResult(new URL(newestURL), newestVersion, filename, sha512);
        } catch (ProtocolException e) {
            logger.severe("[PluginUpdater] Unable to reach server: " + e.getMessage());
            return new LinkResult(UpdateResult.NO_CONNECTION);
        } catch (UnknownHostException e) {
            logger.severe("[PluginUpdater] Unable to connect to server, cancelling update..");
            return new LinkResult(UpdateResult.NO_CONNECTION);
        } catch (MalformedURLException e) {
            logger.severe("[PluginUpdater] Malformed URL: " + e.getMessage());
            return new LinkResult(UpdateResult.NO_CONNECTION);
        } catch (SocketTimeoutException e) {
            logger.severe("[PluginUpdater] Connection timeout");
            return new LinkResult(UpdateResult.NO_CONNECTION);
        } catch (IOException e) {
            logger.severe("[PluginUpdater] Unknown IO Error occurred");
            e.printStackTrace();
            return new LinkResult(UpdateResult.ERROR);
        }
    }

    public @NotNull UpdateResult update(ReleaseType type, File pluginFile) {

        try {

            linkResult = getLink(type);

            if (!linkResult.wasSuccessful()) {
                if (linkResult.getResult() == null) {
                    logger.info("[PluginUpdater] Missing data in API Response:");
                    logger.info("[PluginUpdater] URL:" + linkResult.getURL());
                    logger.info("[PluginUpdater] Version:" + linkResult.getVersion());
                    logger.info("[PluginUpdater] Filename:" + linkResult.getFilename());
                    logger.info("[PluginUpdater] Hash:" + linkResult.getHash());
                    return UpdateResult.ERROR;
                }
                return linkResult.getResult();
            }
            URL url = linkResult.getURL();
            String version = linkResult.getVersion();
            String filename = linkResult.getFilename();
            String sha512 = linkResult.getHash();


            if (filename.equals(pluginFile.getName())) {
                logger.info("[PluginUpdater] Filenames are equal, Plugin is considered up to date!");
                return UpdateResult.NO_UPDATE;
            }


            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("plugins/"+filename);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            if (verifyHash(new File("plugins/"+filename), sha512)) {
                pluginFile.delete();
                logger.info("[PluginUpdater] Successfully updated plugin to version " + version);
                logger.info("[PluginUpdater] Restarting the server is recommended");
                return UpdateResult.SUCCESS;
            }
            else {
                new File("plugins/"+filename).delete();
                logger.severe("[PluginUpdater] Hash sum of the file does not match with Modrinth's hash sum, cancelling update…");
                return UpdateResult.INVALID_HASH;
            }

        }
        catch (MalformedURLException e) {
            logger.severe("[PluginUpdater] Malformed URL: " + e.getMessage());
            return UpdateResult.NO_CONNECTION;
        }
        catch (IOException e) {
            logger.severe("[PluginUpdater] IOException: " + e.getMessage());
            return UpdateResult.ERROR;
        }
    }


    public boolean verifyHash(File file, String sha512) {
        try {
            String fileHash = Files.hash(file, Hashing.sha512()).toString();

            boolean result = fileHash.equalsIgnoreCase(sha512);
            if (!result) {
                logger.severe("Hash of File:        " + fileHash);
                logger.severe("Hash from Modrinth:  " + sha512);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public enum UpdateResult {

        /**
         * The plugin was successfully updated
         */
        SUCCESS,

        /**
         * The plugin is already up to date
         */
        NO_UPDATE,

        /**
         * Updating was disabled in the config
         */
        DISABLED,

        /**
         * Returned when the given project ID does not exist
         */
        INVALID_PROJECT,

        /**
         * Returned when your device is not connected to the internet, can also occur, when Modrinth servers are down
         */
        NO_CONNECTION,


        /**
         * If the recieved respose code is not a success code
         */
        INVALID_CONNECTION_CODE,


        /**
         * Returned when the SHA512 hash from Modrinth and from the file don't match
         */
        INVALID_HASH,


        /**
         * If the response of the API is invalid
         */
        INVALID_API_RESPONSE,

        /**
         * A different error
         */
        ERROR
    }

    public class LinkResult {
        UpdateResult result;
        URL url;
        String version;
        String filename;
        String sha512;

        public LinkResult(UpdateResult result) {
            this.result = result;
            this.url = null;
        }

        public LinkResult(@NotNull URL url, @NotNull String version, @NotNull String filename, @NotNull String sha512) {
            this.result = null;
            this.url = url;
            this.version = version;
            this.filename = filename;
            this.sha512 = sha512;
        }

        public boolean wasSuccessful() {
            return url != null && version != null && filename != null && sha512 != null;
        }

        public @Nullable URL getURL() {
            return url;
        }

        public @Nullable String getVersion() {
            return version;
        }

        public @Nullable String getFilename() {
            return filename;
        }

        public @Nullable String getHash() {
            return sha512;
        }


        public UpdateResult getResult() {
            return result;
        }
    }


    public enum ReleaseType {
        STABLE,
        BETA,
        ALPHA,

        UNKNOWN;

        static ReleaseType parse(@Nullable String type) {
            if (type == null) return ALPHA;
            return switch (type.toLowerCase()) {
                case "stable", "release" -> STABLE;
                case "beta" -> BETA;
                case "alpha", "dev", "snapshot" -> ALPHA;
                default -> UNKNOWN;
            };
        }

        static boolean shouldUpdate(ReleaseType minimum, String releaseType) {
            if (minimum == UNKNOWN) return true;
            switch (minimum) {
                case ALPHA:
                    return true;
                case BETA:
                    return (parse(releaseType) != ALPHA);
                case STABLE:
                    return (parse(releaseType) == STABLE);
            }
            return false;
        }
    }

    public enum UpdateType {
        ENABLED,        // Automatically install updates
        VERSION_CHECK,  // Only check for newer versions
        DISABLED;       // Do not check for versions

        public static @NotNull UpdateType parse(@Nullable String updateType) {
            if (updateType == null) return VERSION_CHECK;
            if (updateType.equalsIgnoreCase("true")) return ENABLED;
            if (updateType.equalsIgnoreCase("false")) return DISABLED;
            try {
                return valueOf(updateType.toUpperCase());
            } catch (IllegalArgumentException  e) {
                Bukkit.getLogger().severe("[PluginUpdater] Invalid Update Type: " + updateType);
                Bukkit.getLogger().severe("[PluginUpdater] Version check mode will be used.");
                return VERSION_CHECK;
            }
        }
    }

    public class UpdaterConfig {

        public UpdateType updateType;
        public ReleaseType releaseType;

        public UpdaterConfig() {
            try {
                load(new File(plugin.getDataFolder().getParentFile(), "updater.yml"));
            } catch (IOException e) {
                try {
                    load(new File(plugin.getDataFolder(), "updater.yml"));
                } catch (IOException e1) {
                    Bukkit.getLogger().severe("[PluginUpdater] Plugin " + plugin.getName() + " seems to have no write access on its data folder.");
                    updateType = UpdateType.DISABLED;
                    throw new RuntimeException(e1);
                }
            }
        }
        public boolean shouldUpdate(String oldVersion, String newVersion) {
            Version oldV = Version.parse(oldVersion);
            Version newV = Version.parse(newVersion);
            return oldV.compareTo(newV) < 0;
        }

        public boolean updateAllowed() { return updateType.equals(UpdateType.ENABLED); }

        private UpdateType load(File file) throws IOException {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            updateType = UpdateType.parse(config.getString("allowUpdates"));
            releaseType = ReleaseType.parse(config.getString("releaseChannel"));
            config.set("allowUpdates", updateType.name());
            config.set("releaseChannel", releaseType.name());
            config.setComments("allowUpdates", List.of("This config only applies to plugins using the Updater by Quantum625", "See more info at https://github.com/Quantum625/Updater", "Plugins using other methods of automatic update installations will have their own config", "Possible values:","ENABLED (automatic update installation)","VERSION_CHECK (will search for updates, but not install them)", "DISABLED (Updater won't run)"));
            config.setComments("releaseChannel", List.of("Release channel that will be used for the updates (Stable/Beta/Alpha)"));
            config.save(file);
            return updateType;
        }
    }
}
