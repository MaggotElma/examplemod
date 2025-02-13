package com.example.examplemod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class DestroyerItem extends Item {


    public DestroyerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        player.giveExperiencePoints(100);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos blockPosition = context.getClickedPos();

        if(!level.isClientSide()){

            level.getBlockState(context.getClickedPos()).getBlock().popExperience((ServerLevel) level, blockPosition, calculateExperience(context));
            level.destroyBlock(context.getClickedPos(),false);

            context.getItemInHand().hurtAndBreak(1, ((ServerLevel) level), context.getPlayer(), item -> context.getPlayer().onEquippedItemBroken(item, EquipmentSlot.MAINHAND));

        }

        return InteractionResult.SUCCESS;

    }

    private int calculateExperience(UseOnContext context) {

        Block clickedBlock = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
        return Math.round(clickedBlock.getFriction() * 3);

    }
}
