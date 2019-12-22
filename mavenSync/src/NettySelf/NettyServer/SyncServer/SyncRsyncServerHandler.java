package NettySelf.NettyServer.SyncServer;

import CommonFunc.DataTypeRsyncChecksum;
import Sync.Sync_PciSrc;
import Sync.Sync_RsyncSrc;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogLevel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class SyncRsyncServerHandler extends ChannelInboundHandlerAdapter {
    private final Sync_RsyncSrc syncRsyncSrc = new Sync_RsyncSrc();
    private final DataTypeRsyncChecksum dataTypeOfRsync = new DataTypeRsyncChecksum();

    @Override
    public void channelActive(final ChannelHandlerContext ctx){
        Charset utf8 = Charset.forName("UTF-8");
        final ByteBuf resConnect = Unpooled.copiedBuffer("Welcome to join Rsync Server!\n", utf8);

        final ChannelFuture f = ctx.writeAndFlush(resConnect);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf buf = (ByteBuf)msg;
        //创建目标大小的数组
        byte[] barray = new byte[buf.readableBytes()];
        //把数据从bytebuf转移到byte[]
        buf.getBytes(0,barray);
        //将byte[]转成字符串用于打印
        String str=new String(barray);

        if(!str.equals("end")){
            int ind = str.indexOf(',');
            if (ind!=-1){
                dataTypeOfRsync.adler32 = Integer.parseInt(str.substring(0,ind));
                dataTypeOfRsync.MD5 = str.substring(ind+1,str.length());
                syncRsyncSrc.dataStore.add(dataTypeOfRsync);
            }
        }
        if(str.equals("end")){
            System.out.println("Transform finished!");
            syncRsyncSrc.DoSync_Rsync2();
        }
        ReferenceCountUtil.release(buf);
        //System.out.print(str+"\n");
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
