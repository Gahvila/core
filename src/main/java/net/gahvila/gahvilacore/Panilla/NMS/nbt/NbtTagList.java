package net.gahvila.gahvilacore.Panilla.NMS.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBTList;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagList;

public class NbtTagList implements INbtTagList {

    private final ReadableNBTList<?> handle;

    public NbtTagList(ReadableNBTList<?> handle) {
        this.handle = handle;
    }

    @Override
    public INbtTagCompound getCompound(int index) {
        return new NbtTagCompound((NBTCompound) handle.get(index));
    }

    @Override
    public String getString(int index) {
        return (String) handle.get(index);
    }

    @Override
    public boolean isCompound(int index) {
        return handle.get(index) instanceof NBTCompound;
    }

    @Override
    public int size() {
        return handle.size();
    }

}
