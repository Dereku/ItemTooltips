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
package club.without.dereku.itemtooltips.implementations;

import java.util.Properties;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

/**
 *
 * @author Dereku
 */
public abstract class Implementation {
    
    public final Properties keys = new Properties();
    
    public abstract String getName(Item item);
    
    public abstract String getVersion();
    
    public abstract String getAssetsVersion();
    
    public static Implementation getImpl(String version) {
        if (version.startsWith("1.8.8")) {
            return new v1_8_R3();
        }
        if (version.startsWith("1.9")) {
            if (version.contains("R1")) {
                return new v1_9_R1();
            } else if (version.contains("R2")) {
                return new v1_9_R2();
            } else {
                return null;
            }
        }
        Bukkit.getLogger().log(Level.WARNING, "Failed to parse \"{0}\"", version);
        return null;
    }
}
