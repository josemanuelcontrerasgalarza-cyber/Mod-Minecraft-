package com.kratos.arsenal.item;

import com.kratos.arsenal.gun.AttachmentType;
import com.kratos.arsenal.gun.GunData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Accesorio montable. Para instalarlo: sostén el accesorio en una mano, un
 * arma en la otra, agáchate (Shift) y haz clic derecho.
 */
public class AttachmentItem extends Item {

    private final AttachmentType type;

    public AttachmentItem(AttachmentType type, Settings settings) {
        super(settings);
        this.type = type;
    }

    public AttachmentType getAttachmentType() {
        return type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack attachmentStack = user.getStackInHand(hand);
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack gunStack = user.getStackInHand(otherHand);

        if (user.isSneaking() && gunStack.getItem() instanceof GunItem) {
            if (!world.isClient) {
                GunData.setAttachment(gunStack, type);
                if (!user.getAbilities().creativeMode) {
                    attachmentStack.decrement(1);
                }
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value(), SoundCategory.PLAYERS, 0.8f, 1.2f);
            }
            return TypedActionResult.success(attachmentStack, world.isClient);
        }
        return TypedActionResult.pass(attachmentStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.attachment_slot",
                Text.translatable("attachment.slot.kratos_arsenal." + type.getSlot().name().toLowerCase()))
                .formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.attachment_help").formatted(Formatting.DARK_GRAY));
    }
}
