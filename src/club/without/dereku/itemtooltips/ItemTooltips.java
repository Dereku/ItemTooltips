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

import club.without.dereku.itemtooltips.implementations.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dereku
 */
public class ItemTooltips extends JavaPlugin {

    private String language;
    private ResourceDownloader rd;
    private Implementation impl;
    public List<String> worlds;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.language = this.getConfig().getString("lang", "en_US");
        this.impl = Implementation.getImpl(this.getServer().getBukkitVersion());
        if (this.impl == null) {
            this.getLogger().info("Not implemented yet.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        
        if (!this.language.equals("en_US")) {
            this.downloadAndApplyLanguage(this.impl.getVersion(), this.language);
        }

        this.worlds = this.getConfig().getStringList("worlds");
        if (worlds.isEmpty()) {
            for (World world : this.getServer().getWorlds()) {
                worlds.add(world.getName());
            }
            this.getConfig().set("worlds", this.worlds);
            this.saveConfig();
        }
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getLogger().info("Enabled.");
    }

    public Implementation getImpl() {
        return this.impl;
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
                this.impl.keys.clear();
                return;
            }
        }
        this.loadLanguage(file);
    }

    public void loadLanguage(File file) {
        Charset charset = Charset.forName("UTF-8");
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(file), charset)) {
            this.impl.keys.load(is);
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed to load " + file.getName(), ex);
            this.getLogger().log(Level.WARNING, "Using en_US language.");
            this.impl.keys.clear();
        }
    }
}
