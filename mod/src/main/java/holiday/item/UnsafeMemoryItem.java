package holiday.item;

import java.util.HexFormat;
import java.util.function.Consumer;

import holiday.component.HolidayServerDataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

public class UnsafeMemoryItem extends Item {
    private static final HexFormat FORMAT = HexFormat.of().withUpperCase();

    public UnsafeMemoryItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (stack.contains(HolidayServerDataComponentTypes.MEMORY_VALUE)) {
            int value = stack.get(HolidayServerDataComponentTypes.MEMORY_VALUE);
            String valueString = FORMAT.toHexDigits(value);

            textConsumer.accept(Text.translatable("item.holiday-server-mod.unsafe_memory.tooltip", valueString).formatted(Formatting.GRAY));
        }
    }

    public static void setRandomMemoryValue(ItemStack stack, Random random) {
        int value = random.nextBetween(0, Integer.MAX_VALUE - 1);
        stack.set(HolidayServerDataComponentTypes.MEMORY_VALUE, value);
    }
}
