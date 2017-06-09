/*
 * The MIT License
 *
 * Copyright 2017 Dereku.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package club.without.dereku.itemtooltips;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dereku
 */
public class ResourceDownloader {

    private final static String VERSIONS_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private final static String ASSETS_URL = "http://resources.download.minecraft.net/";
    private final Gson gson = new Gson();
    private final ItemTooltips plugin;

    public ResourceDownloader(ItemTooltips plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param locale Name of resource. Ex.: ru_RU, en_CA, etc.
     * @param destination Destination where to store file.
     * @throws MalformedURLException
     * @throws IOException
     */
    public void downloadResource(String locale, File destination) throws MalformedURLException, IOException {
        VersionManifest vm = this.downloadObject(new URL(ResourceDownloader.VERSIONS_LIST), VersionManifest.class);
        ClientVersion client = this.downloadObject(new URL(vm.getLatestRelease().getUrl()), ClientVersion.class);
        AssetIndex ai = this.downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);
        String hash = ai.getLocaleHash(locale);
        this.plugin.getLogger().log(Level.INFO, "Downloading {0}.lang (hash: {1})", new Object[]{locale, hash});
        FileUtils.copyURLToFile(new URL(ResourceDownloader.ASSETS_URL + this.createPathFromHash(hash)), destination);
    }

    private <T extends Object> T downloadObject(URL url, Class<T> object) throws IOException {
        try (InputStream inputStream = url.openConnection().getInputStream();
                InputStreamReader r = new InputStreamReader(inputStream);
                JsonReader jr = new JsonReader(r)) {
            return this.gson.fromJson(jr, object);
        }
    }

    /**
     * From Mojang, with love.
     *
     * @param hash
     * @return
     */
    private String createPathFromHash(String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }

    /*
        Gson serialization
     */
    class VersionManifest {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> latest;
        private ArrayList<RemoteClient> versions;

        public RemoteClient getLatestRelease() {
            String release = this.latest.get("release");
            for (RemoteClient c : this.versions) {
                if (c.getId().equals(release)) {
                    return c;
                }
            }

            throw new IllegalArgumentException(release + " does not exists. There something is definitely wrong.");
        }
    }

    class RemoteClient {

        private String id, url;

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    }

    class ClientVersion {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> assetIndex;

        public String getAssetUrl() {
            return this.assetIndex.get("url");
        }
    }

    class AssetIndex {

        private final static String PATH = "minecraft/lang/%s.lang";
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, LinkedTreeMap<String, String>> objects;

        public String getLocaleHash(String locale) {
            LinkedTreeMap<String, String> asset
                    = this.objects.get(String.format(PATH, locale.toLowerCase()));
            if (asset == null) {
                throw new IllegalArgumentException("Locale " + locale + " does not exists!");
            }
            return asset.get("hash");
        }
    }
}
