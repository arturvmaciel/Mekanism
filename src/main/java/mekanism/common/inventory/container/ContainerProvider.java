package mekanism.common.inventory.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

public class ContainerProvider implements INamedContainerProvider {

    private final ITextComponent displayName;
    private final IContainerProvider provider;

    public ContainerProvider(String translationKey, IContainerProvider provider) {
        this(TextComponentUtil.translate(translationKey), provider);
    }

    public ContainerProvider(ITextComponent displayName, IContainerProvider provider) {
        this.displayName = displayName;
        this.provider = provider;
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return provider.createMenu(i, inv, player);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return displayName;
    }
}