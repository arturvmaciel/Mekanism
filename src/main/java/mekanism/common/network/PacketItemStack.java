package mekanism.common.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.base.IItemNetwork;
import mekanism.common.network.PacketItemStack.ItemStackMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PacketItemStack implements IMessageHandler<ItemStackMessage, IMessage> {

    @Override
    public IMessage onMessage(ItemStackMessage message, MessageContext context) {
        PlayerEntity player = PacketHandler.getPlayer(context);
        if (player == null) {
            return null;
        }
        PacketHandler.handlePacket(() -> {
            ItemStack stack = player.getHeldItem(message.currentHand);
            if (!stack.isEmpty() && stack.getItem() instanceof IItemNetwork) {
                IItemNetwork network = (IItemNetwork) stack.getItem();
                try {
                    network.handlePacketData(stack, message.storedBuffer);
                } catch (Exception e) {
                    Mekanism.logger.error("FIXME: Packet handling error", e);
                }
                message.storedBuffer.release();
            }
        }, player);
        return null;
    }

    public static class ItemStackMessage implements IMessage {

        public Hand currentHand;

        public List<Object> parameters;

        public ByteBuf storedBuffer = null;

        public ItemStackMessage() {
        }

        public ItemStackMessage(Hand hand, List<Object> params) {
            currentHand = hand;
            parameters = params;
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            dataStream.writeInt(currentHand.ordinal());
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                PacketHandler.log("Sending ItemStack packet");
            }
            PacketHandler.encode(parameters.toArray(), dataStream);
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            currentHand = Hand.values()[dataStream.readInt()];
            storedBuffer = dataStream.copy();
        }
    }
}