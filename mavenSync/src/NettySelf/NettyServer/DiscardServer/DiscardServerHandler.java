package NettySelf.NettyServer.DiscardServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Administrator on 2019/12/17 0017.
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        //Discard the received data silently.
        //((ByteBuf)msg).release();

        //Let put some code into the channelRead() method of the DiscardServerHandler;
        ByteBuf in = (ByteBuf)msg;

        try {
            while (in.isReadable()) { // (1)
                System.out.print((char)in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
