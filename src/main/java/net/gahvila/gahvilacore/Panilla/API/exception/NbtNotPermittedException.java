package net.gahvila.gahvilacore.Panilla.API.exception;

public class NbtNotPermittedException extends PacketException {

    private final int itemSlot;

    public NbtNotPermittedException(String nmsClass, boolean from, FailedNbt failedNbt, int itemSlot) {
        super(nmsClass, from, failedNbt);
        this.itemSlot = itemSlot;
    }

    public int getItemSlot() {
        return itemSlot;
    }

}
