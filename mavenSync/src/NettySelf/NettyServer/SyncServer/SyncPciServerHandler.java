package NettySelf.NettyServer.SyncServer;

import Sync.Sync_PciSrc;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class SyncPciServerHandler extends ChannelInboundHandlerAdapter {
    private final Sync_PciSrc syncPciSrc = new Sync_PciSrc();
//    FileOutputStream fileOut;
//    BufferedOutputStream dataOut;
//    public SyncPciServerHandler(){
//        try{
//            fileOut = new FileOutputStream("e://abstractOfSrc"+".txt");
//            dataOut=new BufferedOutputStream(fileOut);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    @Override
    public void channelActive(final ChannelHandlerContext ctx){
        Charset utf8 = Charset.forName("UTF-8");
        final ByteBuf resConnect = Unpooled.copiedBuffer("Welcome to join!\n", utf8);

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

//        try{
//            dataOut.write((str+"\r\n").getBytes());
//            dataOut.flush();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        if(!str.equals("end")) syncPciSrc.dataStore.add(str);
        if(str.equals("end")){
            System.out.println("Transform finished!");
            syncPciSrc.DoSync_Pci2();
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
