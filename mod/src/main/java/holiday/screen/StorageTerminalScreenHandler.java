package holiday.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import holiday.item.HolidayServerItems;
import holiday.item.UnsafeMemoryItem;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class StorageTerminalScreenHandler extends ScreenHandler {
    public static final Direction STORAGE_DIRECTION = Direction.UP;

    protected static final int INVENTORY_WIDTH = 9;
    protected static final int INVENTORY_HEIGHT = 5;

    private static final int INVENTORY_SIZE = INVENTORY_WIDTH * INVENTORY_HEIGHT;

    public static final int MAX_SEARCH_LENGTH = 50;

    private static final Comparator<Object2LongMap.Entry<ItemVariant>> COMPARATOR = Comparator.<Object2LongMap.Entry<ItemVariant>>comparingLong(Object2LongMap.Entry::getLongValue)
            .reversed()
            .thenComparing(Comparator.<Object2LongMap.Entry<ItemVariant>, String>comparing(entry -> {
                return entry.getKey().getItem().getName().getString().toLowerCase(Locale.ROOT);
            }));

    private final BlockPos storagePos;
    private final World world;

    private Storage<ItemVariant> cachedStorage = null;
    private final Inventory inventory;

    private final Storage<ItemVariant> disconnectedStorage;

    private final Property[] slotCounts = new Property[INVENTORY_SIZE];

    private final Property size = Property.create();
    private final Property connected = Property.create();

    private String search = "";
    private int skip = 0;

    public StorageTerminalScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos storagePos, World world) {
        super(HolidayServerScreenHandlers.STORAGE_TERMINAL, syncId);

        this.storagePos = storagePos;
        this.world = world == null ? playerInventory.player.getEntityWorld() : world;

        this.inventory = new SimpleInventory(INVENTORY_SIZE);

        int left = 9;
        int top = 18;

        for (int y = 0; y < INVENTORY_HEIGHT; y++) {
            for (int x = 0; x < INVENTORY_WIDTH; x++) {
                int index = (y * INVENTORY_WIDTH) + x;

                this.slotCounts[index] = Property.create();
                this.addProperty(this.slotCounts[index]);

                this.addSlot(new LockedSlot(this.inventory, index, left + x * 18, top + y * 18));
            }
        }

        this.disconnectedStorage = createDisconnectedStorage(playerInventory.player.getRandom());

        this.addPlayerSlots(playerInventory, left, top + 104);

        this.addProperty(this.size);
        this.addProperty(this.connected);

        this.refresh();
    }

    public StorageTerminalScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, null);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        boolean inventorySlot = slotIndex >= 0 && slotIndex < this.inventory.size();

        if ((inventorySlot && actionType == SlotActionType.PICKUP_ALL)) {
            return;
        }

        Storage<ItemVariant> storage = this.getStorage();

        if ((storage == this.disconnectedStorage) != (this.cachedStorage == this.disconnectedStorage)) {
            this.refresh();
        } else if ((inventorySlot || actionType == SlotActionType.QUICK_MOVE) && actionType != SlotActionType.CLONE) {
            if (slotIndex < 0 || slotIndex >= this.slots.size()) {
                return;
            }

            List<Task> tasks = new ArrayList<>();

            SlottedStorage<ItemVariant> playerInventory = PlayerInventoryStorage.of(player);
            Storage<ItemVariant> cursor = PlayerInventoryStorage.getCursorStorage(this);

            Slot slot = this.getSlot(slotIndex);

            ItemVariant item = ItemVariant.of(slot.getStack());
            int maxCount = item.getComponentMap().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);

            if (actionType == SlotActionType.PICKUP) {
                if (button == 1) {
                    maxCount = MathHelper.ceil(slot.getStack().getCount() / 2d);
                }

                tasks.add(new Task(cursor, storage, null, button == 1 ? 1 : Integer.MAX_VALUE));
                tasks.add(new Task(storage, cursor, item, maxCount));
            } else if (actionType == SlotActionType.QUICK_MOVE) {
                Storage<ItemVariant> from = inventorySlot ? storage : playerInventory.getSlot(slot.getIndex());
                Storage<ItemVariant> to = inventorySlot ? playerInventory : storage;

                tasks.add(new Task(from, to, item, maxCount));
            }

            try (Transaction transaction = Transaction.openOuter()) {
                for (Task task : tasks) {
                    long amount = StorageUtil.move(task.from(), task.to(), task, task.maxAmount(), transaction);

                    if (amount != 0) {
                        break;
                    }
                }

                transaction.commit();
            }

            this.refresh();
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    private Storage<ItemVariant> getStorage() {
        if (this.storagePos == null) {
            return null;
        }

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(this.world, this.storagePos, STORAGE_DIRECTION);
        return storage == null ? this.disconnectedStorage : storage;
    }

    private void refresh() {
        if (this.world.isClient()) {
            return;
        }

        this.inventory.clear();

        for (Property property : this.slotCounts) {
            property.set(0);
        }

        Storage<ItemVariant> storage = this.getStorage();
        this.cachedStorage = storage;

        this.setConnected(storage != this.disconnectedStorage);

        Object2LongOpenHashMap<ItemVariant> map = new Object2LongOpenHashMap<ItemVariant>();
        Iterator<StorageView<ItemVariant>> iterator = storage.nonEmptyIterator();

        while (iterator.hasNext()) {
            StorageView<ItemVariant> view = iterator.next();
            map.addTo(view.getResource(), view.getAmount());
        }

        List<Object2LongMap.Entry<ItemVariant>> filtered = map.object2LongEntrySet().stream()
            .filter(this::matchesSearch)
            .toList();

        this.size.set(filtered.size());

        List<Object2LongMap.Entry<ItemVariant>> entries = filtered.stream()
            .sorted(COMPARATOR)
            .skip(this.skip)
            .limit(this.inventory.size())
            .toList();

        int slot = 0;

        for (Object2LongMap.Entry<ItemVariant> entry : entries) {
            int count = entry.getLongValue() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) entry.getLongValue();

            this.slotCounts[slot].set(count);
            this.inventory.setStack(slot, entry.getKey().toStack(count));

            slot += 1;
        }
    }

    public int getCount(Slot slot) {
        if (slot.inventory == this.inventory) {
            return this.slotCounts[slot.getIndex()].get();
        }

        return -1;
    }

    public int getSize() {
        return this.size.get();
    }

    private void setConnected(boolean connected) {
        this.connected.set(connected ? 1 : 0);
    }

    public boolean isConnected() {
        return this.connected.get() > 0;
    }

    public void updateSearch(String search, int skip) {
        if (!this.search.equals(search) || this.skip != skip) {
            this.search = search;
            this.skip = skip;

            this.refresh();
        }
    }

    private boolean matchesSearch(Object2LongMap.Entry<ItemVariant> entry) {
        if (this.search.isEmpty()) {
            return true;
        }

        Item item = entry.getKey().getItem();
        String name = item.getName().getString();

        return name.toLowerCase(Locale.ROOT).contains(this.search.toLowerCase(Locale.ROOT));
    }

    private static Storage<ItemVariant> createDisconnectedStorage(Random random) {
        Inventory inventory = new SimpleInventory(INVENTORY_SIZE);

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = new ItemStack(HolidayServerItems.UNSAFE_MEMORY);
            UnsafeMemoryItem.setRandomMemoryValue(stack, random);

            inventory.setStack(slot, stack);
        }

        return FilteringStorage.extractOnlyOf(InventoryStorage.of(inventory, null));
    }

    record Task(Storage<ItemVariant> from, Storage<ItemVariant> to, ItemVariant item, int maxAmount) implements Predicate<ItemVariant> {
        @Override
        public boolean test(ItemVariant item) {
            return this.item() == null || this.item().equals(item);
        }
    }
}
