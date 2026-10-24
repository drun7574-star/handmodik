package com.example.handmod;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NeedleItem extends Item {
    public NeedleItem(Settings settings) {
        super(settings);
    }

    /** ПКМ: пришить недостающую руку (нужна «Отрезанная рука» в инвентаре). Иглу можно держать «в зубах», даже без рук. */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }

        Arm main = player.getMainArm();
        Arm target = null;
        if (Arms.isMissing(player, main)) {
            target = main;
        } else if (Arms.isMissing(player, main.getOpposite())) {
            target = main.getOpposite();
        }
        if (target == null) {
            player.sendMessage(Text.translatable("message.handmod.nothing_to_sew"), true);
            return TypedActionResult.fail(stack);
        }

        if (!player.isCreative()) {
            int slot = findSeveredArm(player);
            if (slot < 0) {
                player.sendMessage(Text.translatable("message.handmod.need_arm"), true);
                return TypedActionResult.fail(stack);
            }
            player.getInventory().getStack(slot).decrement(1);
        }

        Arms.setMissing(player, target, false);
        player.damage(world.getDamageSources().generic(), 2.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200));
        player.getItemCooldownManager().set(this, 20);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER,
                SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.sendMessage(Text.translatable(target == Arm.RIGHT
                ? "message.handmod.sewn_right" : "message.handmod.sewn_left"), true);
        return TypedActionResult.success(stack);
    }

    private static int findSeveredArm(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(ModItems.SEVERED_ARM)) {
                return i;
            }
        }
        return -1;
    }
}
