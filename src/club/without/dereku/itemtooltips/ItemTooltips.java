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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dereku
 */
public class ItemTooltips extends JavaPlugin {

    public final Properties keys = new Properties();
    public List<String> worlds;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        String language = this.getConfig().getString("lang", "en_us").toLowerCase();

        if (!language.equals("en_us")) {
            this.downloadAndApplyLanguage(language);
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

    private void downloadAndApplyLanguage(String lang) {
        File file = FileUtils.getFile(this.getDataFolder().toString(), "lang", lang + ".lang");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                new ResourceDownloader(this).downloadResource(lang, file);
                this.loadLanguage(file);
            } catch (IOException | IllegalArgumentException ex) {
                this.getLogger().log(Level.WARNING, "Failed to download " + file.getName(), ex);
                this.getLogger().log(Level.WARNING, "Using en_US language.");
                this.keys.clear();
                return;
            }
        }
        this.loadLanguage(file);
    }

    private void loadLanguage(File file) {
        Charset charset = Charset.forName("UTF-8");
        try (FileInputStream fis = new FileInputStream(file);
                InputStreamReader is = new InputStreamReader(fis, charset)) {
            this.keys.load(is);
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed to load " + file.getName(), ex);
            this.getLogger().log(Level.WARNING, "Using en_US language.");
            this.keys.clear();
        }
    }
}
