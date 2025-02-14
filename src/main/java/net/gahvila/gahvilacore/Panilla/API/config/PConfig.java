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
package net.gahvila.gahvilacore.Panilla.API.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PConfig {

    public static String PERMISSION_LOG_CHAT = "panilla.log.chat";
    public static String PERMISSION_BYPASS = "panilla.bypass";

    /* Defaults */
    public String language = "en";
    public boolean consoleLogging = true;
    public boolean chatLogging = false;
    public PStrictness strictness = PStrictness.AVERAGE;
    public boolean preventMinecraftEducationSkulls = false;
    public boolean preventFaweBrushNbt = false;
    public boolean ignoreNonPlayerInventories = false;
    public boolean noBlockEntityTag = false;
    public List<String> nbtWhitelist = new ArrayList<>();
    public List<String> disabledWorlds = new ArrayList<>();
    public int maxNonMinecraftNbtKeys = 16;
    public boolean overrideMinecraftMaxEnchantmentLevels = false;
    public Map<String, Integer> minecraftMaxEnchantmentLevelOverrides = new HashMap<>();

}
