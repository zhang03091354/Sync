package NettySelf.NettyServer.EchoServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2019/12/17 0017.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        //Please note that we did not release the received message unlike we did in the DISCARD example.
        // It is because Netty releases it for you when it is written out to the wire.
        ctx.write(msg);
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
