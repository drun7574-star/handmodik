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

public class ScalpelItem extends Item {
    public ScalpelItem(Settings settings) {
        super(settings);
    }

    /** Присядь (Shift) и нажми ПКМ, чтобы отрезать себе руку. */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking()) {
            return TypedActionResult.pass(stack);
        }
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }

        // Сначала отрезается рука, которой ты НЕ держишь скальпель, потом — та, что держит.
        Arm holding = Arms.armForHand(player, hand);
        Arm other = holding.getOpposite();
        Arm target = null;
        if (!Arms.isMissing(player, other)) {
            target = other;
        } else if (!Arms.isMissing(player, holding)) {
            target = holding;
        }
        if (target == null) {
            player.sendMessage(Text.translatable("message.handmod.no_arms_left"), true);
            return TypedActionResult.fail(stack);
        }

        Arms.setMissing(player, target, true);
        player.getInventory().offerOrDrop(new ItemStack(ModItems.SEVERED_ARM));
        player.damage(world.getDamageSources().generic(), 6.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 1200));
        player.getItemCooldownManager().set(this, 20);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT,
                SoundCategory.PLAYERS, 1.0f, 0.8f);
        player.sendMessage(Text.translatable(target == Arm.RIGHT
                ? "message.handmod.cut_right" : "message.handmod.cut_left"), true);
        return TypedActionResult.success(stack);
    }
}
