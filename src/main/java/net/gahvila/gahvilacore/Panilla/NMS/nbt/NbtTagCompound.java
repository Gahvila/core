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
package net.gahvila.gahvilacore.Panilla.NMS.nbt;

import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtChecks;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NbtTagCompound {

    private final ReadWriteNBT handle;

    public NbtTagCompound(ReadWriteNBT handle) {
        this.handle = handle;
    }

    public Object getHandle() {
        return handle;
    }

    public boolean hasKey(String key) {
        if (handle == null) return false;
        return handle.hasTag(key);
    }

    public boolean hasKeyOfType(String key, NbtDataType nbtDataType) {
        if (handle == null) return false;
        return handle.hasTag(key, NBTType.valueOf(nbtDataType.id));
    }

    public Set<String> getKeys() {
        if (handle == null) return Collections.emptySet();
        return handle.getKeys();
    }

    public Set<String> getNonMinecraftKeys() {
        Set<String> defaultKeys = NbtChecks.getChecks().keySet();
        Set<String> nonMinecraftKeys = new HashSet<>();

        for (String key : getKeys()) {
            if (!defaultKeys.contains(key)) {
                nonMinecraftKeys.add(key);
            }
        }

        return nonMinecraftKeys;
    }

    public int getInt(String key) {
        return handle.getInteger(key);
    }

    public double getDouble(String key) {
        return handle.getDouble(key);
    }

    public short getShort(String key) {
        return handle.getShort(key);
    }

    public byte getByte(String key) {
        return handle.getByte(key);
    }

    public String getString(String key) {
        return handle.getString(key);
    }

    public int[] getIntArray(String key) {
        return handle.getIntArray(key);
    }

    public NbtTagList getList(String key, NbtDataType nbtDataType) {
        return new NbtTagList(nbtDataType == NbtDataType.STRING ? handle.getStringList(key) : handle.getCompoundList(key));
    }

    public NbtTagList getList(String key) {
        return new NbtTagList(handle.getCompoundList(key));
    }

    public NbtTagCompound getCompound(String key) {
        return new NbtTagCompound(handle.getCompound(key));
    }

    public int getStringSizeBytes() {
        try {
            return getHandle().toString().getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
