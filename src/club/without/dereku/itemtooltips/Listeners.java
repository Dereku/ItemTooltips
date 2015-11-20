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

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
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
    
    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Item item = event.getEntity();
        if (!this.plugin.worlds.contains(item.getLocation().getWorld().getName())) {
            return;
        }
        
        ItemMeta im = item.getItemStack().getItemMeta();
        item.setCustomName(im.hasDisplayName() ? im.getDisplayName() : this.plugin.getName(item.getName()));
        item.setCustomNameVisible(true);
    }
}
