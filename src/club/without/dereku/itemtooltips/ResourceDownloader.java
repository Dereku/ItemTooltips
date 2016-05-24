/*
 * The MIT License
 *
 * Copyright 2015 Dereku.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Dereku
 */
public class ResourceDownloader {

    private final static String ASSETS_URL = "http://resources.download.minecraft.net/";
    private final ItemTooltips plugin;
    private final FileConfiguration configuration = new YamlConfiguration();

    public ResourceDownloader(ItemTooltips plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        //Try without catch/finally? Heh.
        try (InputStreamReader isr = new InputStreamReader(plugin.getResource("hashs.yml"))) {
            this.configuration.load(isr);
        }
    }

    /**
     * Download locale file.
     * @param version version of assets
     * @param name Name of resource. Ex.: ru_RU, en_CA, etc.
     * @param destination Destination where to store file.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public void downloadResource(String version, String name, File destination) throws MalformedURLException, IOException {
        String hash;
        if ((hash = this.configuration.getString(version + "." + name)) == null) {
            throw new IllegalArgumentException("Resource with name \"" + name + "\" does not exists!");
        }
        this.plugin.getLogger().log(Level.INFO, "Downloading {0}.lang (hash: {1})", new Object[]{name, hash});
        FileUtils.copyURLToFile(new URL(ResourceDownloader.ASSETS_URL + this.createPathFromHash(hash)), destination);
    }

    /**
     * From Mojang, with love. 
     * @param hash
     * @return 
     */
    private String createPathFromHash(String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }
}
