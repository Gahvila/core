/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import net.gahvila.gahvilacore.Panilla.API.exception.PacketException;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import org.bukkit.entity.Player;

public class PanillaPlayer {

    private final Player handle;

    public PanillaPlayer(Player handle) {
        this.handle = handle;
    }

    public Player getHandle() {
        return handle;
    }

    public String getName() {
        return handle.getName();
    }

    public String getCurrentWorldName() {
        return handle.getWorld().getName();
    }

    public boolean hasPermission(String node) {
        return handle.hasPermission(node);
    }

    public boolean canBypassChecks(Panilla panilla, PacketException e) {
        if (e.getFailedNbt().result == NbtCheck.NbtCheckResult.CRITICAL) {
            return false;   // to prevent crash exploits
        }

        boolean inDisabledWorld = panilla.getPConfig().disabledWorlds.contains(getCurrentWorldName());

        return inDisabledWorld || hasPermission(PConfig.PERMISSION_BYPASS);
    }

}
