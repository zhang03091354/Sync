package NettySelf.NettyClient.SyncClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.Buffer;
import java.nio.charset.Charset;


/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class SyncPciClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf buf = (ByteBuf)msg;
        //创建目标大小的数组
        byte[] barray = new byte[buf.readableBytes()];
        //把数据从bytebuf转移到byte[]
        buf.getBytes(0,barray);
        //将byte[]转成字符串用于打印
        String str=new String(barray);
        String header = "New message:";
        System.out.print(header+str);
        ReferenceCountUtil.release(buf);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
