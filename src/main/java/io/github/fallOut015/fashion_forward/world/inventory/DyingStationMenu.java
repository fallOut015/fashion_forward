package io.github.fallOut015.fashion_forward.world.inventory;

import io.github.fallOut015.fashion_forward.sounds.SoundEventsFashionForward;
import io.github.fallOut015.fashion_forward.world.item.WearableItem;
import io.github.fallOut015.fashion_forward.world.level.block.BlocksFashionForward;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class DyingStationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    Runnable slotUpdateListener = () -> {
    };
    private final Slot[] inputSlots;
    private final Slot resultSlot;
    private final Container inputContainer = new SimpleContainer(65) {
        public void setChanged() {
            super.setChanged();
            DyingStationMenu.this.slotsChanged(this);
            DyingStationMenu.this.slotUpdateListener.run();
        }
    };
    private final Container outputContainer = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            DyingStationMenu.this.slotUpdateListener.run();
        }
    };

    public DyingStationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public DyingStationMenu(int containerId, Inventory inventory, final ContainerLevelAccess containerLevelAccess) {
        super(ContainersFashionForward.DYING_STATION.get(), containerId);
        this.access = containerLevelAccess;
        this.inputSlots = new Slot[this.inputContainer.getContainerSize()];
        this.inputSlots[0] = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof WearableItem;
            }
        });
        for (int i = 1; i < this.inputSlots.length; ++i) {
            this.inputSlots[i] = this.addSlot(new Slot(this.inputContainer, 0, 13 + (i % 8) * 20, 26 + ((i / 8) % 8) * 19) {
                public boolean mayPlace(ItemStack itemStack) {
                    return itemStack.getItem() instanceof DyeItem;
                }
            });
        }

        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            public void onTake(Player player, ItemStack itemStack) {
                for (Slot inputSlot : DyingStationMenu.this.inputSlots) {
                    inputSlot.remove(1);
                }

                containerLevelAccess.execute((level, blockPos) ->
                    level.playSound((Player) null, blockPos, SoundEventsFashionForward.UI_DYING_STATION_TAKE_RESULT.get(), SoundSource.BLOCKS, 1.0F, 1.0F)
                );
                super.onTake(player, itemStack);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BlocksFashionForward.DYING_STATION.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // if wearable in inventory move to input slot if empty, if clicking on result, move to inventory if space is available
        return super.quickMoveStack(player, index);
    }
    @Override
    public void slotsChanged(Container container) {
        this.setupResultSlot();
        this.broadcastChanges();
    }

    public void registerUpdateListener(Runnable slotUpdateListener) {
        this.slotUpdateListener = slotUpdateListener;
    }
    private void setupResultSlot() {
        String dyeData = "";
        if(Arrays.stream(this.getDyeSlots()).allMatch(slot -> !slot.hasItem())) {
            return;
        }
        for(Slot slot : this.getDyeSlots()) {
            char type = 'g';
            // return a different letter for each color, g is for transparent
            dyeData += type;
        }
        ItemStack result = this.getWearableSlot().getItem().copy();
        CompoundTag data = (CompoundTag) result.getOrCreateTag().get("data");
        data.putString("design", dyeData);
        if(!ItemStack.matches(result, this.resultSlot.getItem())) {
            this.resultSlot.set(result);
        }
    }
    public Slot getWearableSlot() {
        return this.inputSlots[0];
    }
    public Slot[] getDyeSlots() {
        return Arrays.copyOfRange(this.inputSlots, 1, 65);
    }
    public Slot getResultSlot() {
        return this.resultSlot;
    }
}