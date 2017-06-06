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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dereku
 */
public class ItemTooltips extends JavaPlugin {

    public final Properties keys = new Properties();
    private String language;
    private ResourceDownloader rd;
    public List<String> worlds;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.language = this.getConfig().getString("lang", "en_US");
        
        if (!this.language.equals("en_US")) {
            //TODO
            this.downloadAndApplyLanguage("", this.language);
        }

        this.worlds = this.getConfig().getStringList("worlds");
        if (this.worlds.isEmpty()) {
            this.worlds.addAll(
                    this.getServer().getWorlds().stream()
                            .map(w -> w.getName())
                            .collect(Collectors.toList())
            );
            this.getConfig().set("worlds", this.worlds);
            this.saveConfig();
        }
        
        Listeners listeners;
        try {
            listeners = new Listeners(this);
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException ex) {
            this.getLogger().log(Level.SEVERE, "Failed to init listeners", ex);
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        
        this.getServer().getPluginManager().registerEvents(listeners, this);
        this.getLogger().info("Enabled.");
    }

    public void downloadAndApplyLanguage(String version, String lang) {
        File file = new File(this.getDataFolder().toString() + File.separator + "lang" + File.separator + version, lang + ".lang");
        if (!file.exists()) {
            file.mkdir();
            try {
                this.rd = new ResourceDownloader(this);
                this.rd.downloadResource(version, lang, file);
            } catch (IOException | InvalidConfigurationException | IllegalArgumentException ex) {
                this.getLogger().log(Level.WARNING, "Failed to download " + file.getName(), ex);
                this.getLogger().log(Level.WARNING, "Using en_US language.");
                this.keys.clear();
                return;
            }
        }
        this.loadLanguage(file);
    }

    public void loadLanguage(File file) {
        Charset charset = Charset.forName("UTF-8");
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(file), charset)) {
            this.keys.load(is);
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed to load " + file.getName(), ex);
            this.getLogger().log(Level.WARNING, "Using en_US language.");
            this.keys.clear();
        }
    }
}
