/*
 * The MIT License
 *
 * Copyright 2016 Dereku.
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

import net.minecraft.server.v1_9_R1.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.BannerMeta;

/**
 *
 * @author Dereku
 */
public class v1_9_R1 extends Implementation{

    @Override
    public String getName(Item item) {
        ItemStack nms = CraftItemStack.asNMSCopy(item.getItemStack());
        if (this.keys.isEmpty()) {
            return nms.getName();
        }
        String out;
        //Banners, why you are written so badly?
        if (item.getItemStack().getType().equals(Material.BANNER)) {
            BannerMeta bm = (BannerMeta) item.getItemStack().getItemMeta();
            out = item.getName().replace("tile.", "") + "." + bm.getBaseColor().toString().toLowerCase().replace("light_blue", "lightBlue") + ".name";
        } else {
            out = nms.a() + ".name";
        }
        return this.keys.getProperty(out, out);
    }

    @Override
    public String getVersion() {
        return "1.9";
    }

    @Override
    public String getAssetsVersion() {
       return "1.9";
    }
    
}
