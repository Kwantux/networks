
package net.quantum625.updater;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public Updater(JavaPlugin plugin, String currentVersion, String projectName, String projectId) {
        this.plugin = plugin;
        this.currentVersion = currentVersion;
        this.projectName = projectName;
        this.projectId = projectId;
        this.logger = plugin.getLogger();
    }

    public UpdateResult update(ReleaseType type, File pluginFile) {
        logger.info("[PluginUpdater] Checking for updates...");
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/"+projectId+"/version");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Quantum625/PluginUpdater v1.0 (Updating Plugin '" + projectName + "')");

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();

            Reader streamReader = null;

            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
                logger.severe("[PluginUpdater] Unable to update plugin! Response code: " + status);
                if (status == 404) return UpdateResult.INVALID_PROJECT;
                return UpdateResult.INVALID_CONNECTION_CODE;
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
                    if (shouldUpdate(newestVersion, versionNumber) && ReleaseType.parse((String) jsonObject.get("version_type")) == type) {

                        JSONArray gameVersions = (JSONArray) jsonObject.get("game_versions");

                        boolean supported = false;

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
            }
            catch (NullPointerException | ClassCastException e) {
                e.printStackTrace();
                logger.severe("[PluginUpdater] Invalid response from Modrinth API, cancelling update…");
                return UpdateResult.INVALID_API_RESPONSE;
            }

            if (!filename.endsWith(".jar")) {
                logger.severe("[PluginUpdater] The Plugin file on Modrinth is not a JAR file!, cancelling update…");
                logger.severe("[PluginUpdater] File Name: " + filename);
                return UpdateResult.ERROR;
            }
            if (newestURL.equals("")) {
                logger.info("[PluginUpdater] Plugin is already up to date!");
                return UpdateResult.NO_UPDATE;
            }
            if (!shouldUpdate(currentVersion, newestVersion)) {
                logger.info("[PluginUpdater] Plugin is already up to date!");
                return UpdateResult.NO_UPDATE;
            }

            logger.info("[PluginUpdater] Found newer version: " + newestVersion);

            if (!updateAllowed()) {
                logger.info("[PluginUpdater] Automatic update installation is disabled.");
                logger.info("[PluginUpdater] Please manually download the newest version of the plugin here:");
                logger.info("[PluginUpdater] " + newestURL);
                return UpdateResult.DISABLED;
            }


            if (filename.equals(pluginFile.getName())) {
                logger.info("[PluginUpdater] Filenames are equal, Plugin is considered up to date!");
                return UpdateResult.NO_UPDATE;
            }

            url = new URL(newestURL);

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("plugins/"+filename);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            if (verifyHash(new File("plugins/"+filename), sha512)) {
                pluginFile.delete();
                logger.info("[PluginUpdater] Successfully updated plugin to version " + newestVersion);
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
                System.out.println("Hash of File:        " + fileHash);
                System.out.println("Hash from Modrinth:  " + sha512);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashFile(File file)
            throws NoSuchAlgorithmException, IOException {
        // Set your algorithm
        // "MD2","MD5","SHA","SHA-1","SHA-256","SHA-384","SHA-512"
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }



    public boolean shouldUpdate(String oldVersion, String newVersion) {
        Version oldV = Version.parse(oldVersion);
        Version newV = Version.parse(newVersion);
        return oldV.compareTo(newV) < 0;
    }

    public boolean updateAllowed() {
        File file = new File(plugin.getDataFolder().getParentFile(), "updater.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.get("allowUpdates") == null) {
            config.set("allowUpdates", true);
            try {
                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config.getBoolean("allowUpdates");
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
        ERROR;
    }


    public enum ReleaseType {
        STABLE,
        BETA,
        ALPHA,

        UNKNOWN;

        static ReleaseType parse(String type) {
            switch (type) {
                case "stable":
                    return STABLE;
                case "release":
                    return STABLE;
                case "beta":
                    return BETA;
                case "alpha":
                    return ALPHA;
            }
            return UNKNOWN;
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
}
