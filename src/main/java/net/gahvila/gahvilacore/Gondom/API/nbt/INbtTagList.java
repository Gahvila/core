package net.gahvila.gahvilacore.Gondom.API.nbt;

public interface INbtTagList {

    INbtTagCompound getCompound(int index);

    String getString(int index);

    boolean isCompound(int index);

    int size();

}
