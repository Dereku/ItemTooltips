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

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Dereku
 */
public class Listeners implements Listener {

    private final ItemTooltips plugin;

    public Listeners(ItemTooltips aThis) {
        this.plugin = aThis;
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        this.setName(event.getEntity(), event.getEntity().getItemStack());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onItemMergeEvent(ItemMergeEvent event) {
        ItemStack is = event.getEntity().getItemStack().clone();
        is.setAmount(is.getAmount() + event.getTarget().getItemStack().getAmount());
        this.setName(event.getTarget(), is);
    }
    
    private void setName(Item item, ItemStack itemStack) {
        if (!this.plugin.worlds.contains(item.getLocation().getWorld().getName())) {
            return;
        }
        ItemMeta im = item.getItemStack().getItemMeta();
        String name = this.plugin.getConfig().getString("format.withoutAmount", "%name%");
        if (itemStack.getAmount() > 1) {
            name = this.plugin.getConfig().getString("format.withAmount", "%name% x%amount%");
        }
        
        String displayName = im.hasDisplayName() ? im.getDisplayName() : this.plugin.getImpl().getName(item);
        item.setCustomName(
                ChatColor.translateAlternateColorCodes('&',
                        name
                        .replace("%name%", displayName)
                        .replace("%amount%", Integer.toString(itemStack.getAmount())))
        );
        item.setCustomNameVisible(true);
    }
}
