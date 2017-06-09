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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.apache.commons.lang3.ClassUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Dereku
 */
public class Listeners implements Listener {

    private final ItemTooltips plugin;
    private final String withoutAmount;
    private final String withAmount;
    
    private final Method asNMSCopy;
    private final Method itemStack_getName;
    private final Method itemStack_getI18n;

    public Listeners(ItemTooltips aThis) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        this.plugin = aThis;
        this.withAmount = ChatColor.translateAlternateColorCodes('&',
                this.plugin.getConfig().getString("format.withAmount", "%name% x%amount%")
        );
        this.withoutAmount = ChatColor.translateAlternateColorCodes('&',
                this.plugin.getConfig().getString("format.withoutAmount", "%name%")
        );

        String pckg = aThis.getServer().getClass().getPackage().getName();
        String nmsVersion = pckg.substring(pckg.lastIndexOf('.') + 1);
        
        Class<?> nmsItemStack = ClassUtils.getClass("net.minecraft.server." + nmsVersion + ".ItemStack");
        Class<?> obcbCraftItemStack = ClassUtils.getClass("org.bukkit.craftbukkit." + nmsVersion + ".inventory.CraftItemStack");
        this.asNMSCopy = obcbCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        this.itemStack_getName = nmsItemStack.getDeclaredMethod("getName", (Class<?>[]) null);
        this.itemStack_getI18n = nmsItemStack.getDeclaredMethod("a", (Class<?>[]) null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (!this.plugin.worlds.contains(event.getEntity().getLocation().getWorld().getName())) {
            return;
        }
        this.setName(event.getEntity(), event.getEntity().getItemStack());
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMergeEvent(ItemMergeEvent event) {
        if (!this.plugin.worlds.contains(event.getTarget().getLocation().getWorld().getName())) {
            return;
        }
        ItemStack is = event.getEntity().getItemStack().clone();
        is.setAmount(is.getAmount() + event.getTarget().getItemStack().getAmount());
        this.setName(event.getTarget(), is);
    }

    private void setName(Item item, ItemStack itemStack) {
        ItemMeta im = item.getItemStack().getItemMeta();
        String name = itemStack.getAmount() > 1 ? this.withAmount : this.withoutAmount;
        String displayName = im.hasDisplayName() ? im.getDisplayName() : this.getName(item);
        item.setCustomName(name.replace("%name%", displayName).replace("%amount%", Integer.toString(itemStack.getAmount())));
        item.setCustomNameVisible(true);
    }

    private String getName(Item item) {
        String i18n;
        try {
            Object nmsis = this.asNMSCopy.invoke(null, item.getItemStack());
            if (this.plugin.keys.isEmpty()) {
                return (String) this.itemStack_getName.invoke(nmsis, new Object[0]);
            }
            i18n = (String) this.itemStack_getI18n.invoke(nmsis, new Object[0]);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) { 
            this.plugin.getLogger().log(Level.WARNING, "Failed to get name", ex);
            return null;
        }

        String out = this.getBannerKey(item);
        if (out == null) {
            out = i18n.concat(".name");
        }

        return this.plugin.keys.getProperty(out, out);
    }

    //TODO: Remove this shit
    private String getBannerKey(Item item) {
        if (!item.getItemStack().getType().equals(Material.BANNER)) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        BannerMeta bm = (BannerMeta) item.getItemStack().getItemMeta();
        try {
            out.append(item.getName().replace("tile.", ""))
                    .append(".")
                    .append(bm.getBaseColor().toString().toLowerCase().replace("light_blue", "lightBlue"))
                    .append(".name");
        } catch (Exception ex) {
            out.append(ex.getMessage());
        }
        return out.toString();
    }
}
