package holiday.item;

import holiday.component.HolidayServerDataComponentTypes;
import net.minecraft.SharedConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SmoothieItem extends Item {
    public SmoothieItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int addedTime = this.getMaxUseTime(stack, user);

        if (stack.contains(HolidayServerDataComponentTypes.LAST_DRINK_TIME)) {
            long lastDrinkTime = stack.get(HolidayServerDataComponentTypes.LAST_DRINK_TIME);

            if (world.getTime() - lastDrinkTime < 48) {
                int drinkTime = stack.getOrDefault(HolidayServerDataComponentTypes.TOTAL_DRINK_TIME, 0);
                stack.set(HolidayServerDataComponentTypes.TOTAL_DRINK_TIME, drinkTime + addedTime);
            } else {
                stack.set(HolidayServerDataComponentTypes.TOTAL_DRINK_TIME, addedTime);
            }
        } else {
            stack.set(HolidayServerDataComponentTypes.TOTAL_DRINK_TIME, addedTime);
        }

        stack.set(HolidayServerDataComponentTypes.LAST_DRINK_TIME, world.getTime());

        if (world.isClient() && user instanceof PlayerEntity player) {
            int drinkTime = stack.getOrDefault(HolidayServerDataComponentTypes.TOTAL_DRINK_TIME, 0);
            double drinkSeconds = Math.floor(drinkTime / (double) SharedConstants.TICKS_PER_SECOND * 10) / 10;

            player.sendMessage(Text.translatable("item.holiday-server-mod.smoothie.tooltip", drinkSeconds), true);
        }

        return super.finishUsing(stack, world, user);
    }
}
