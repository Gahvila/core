package net.gahvila.gahvilacore.Panilla.NMS.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBTList;

public class NbtTagList {

    private final ReadableNBTList<?> handle;

    public NbtTagList(ReadableNBTList<?> handle) {
        this.handle = handle;
    }

    public NbtTagCompound getCompound(int index) {
        return new NbtTagCompound((NBTCompound) handle.get(index));
    }

    public String getString(int index) {
        return (String) handle.get(index);
    }

    public boolean isCompound(int index) {
        return handle.get(index) instanceof NBTCompound;
    }

    public int size() {
        return handle.size();
    }

}
