package NettySelf.NettyClient.SyncClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class SyncRsyncClient {
    private Channel channel;
    ByteBuf bufTemp;
    Bootstrap b;
    EventLoopGroup workerGroup;
    String host = "127.0.0.1";
    int port = 8082;
    public void run(){
        workerGroup = new NioEventLoopGroup();

        try{
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE,true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception{
                    ch.pipeline().addLast(new SyncRsyncClientHandler());
                }
            });

            doConnect();
        }catch (Exception e){
            e.printStackTrace();
            workerGroup.shutdownGracefully();
        }
        finally{
            //workerGroup.shutdownGracefully();
        }
    }

    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = b.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println("Connect to server successfully!");
                } else {
                    System.out.println("Failed to connect to server, try connect after 10000s");

                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10000, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void sendData(String message){
        Charset utf8 = Charset.forName("UTF-8");
        bufTemp = Unpooled.copiedBuffer(message, utf8);
        if(channel==null){
            System.out.println("Is not connected to the server!");
            return ;
        }
        channel.writeAndFlush(bufTemp);
    }
    public void closeClient(){
        if(workerGroup==null){
            System.out.println("Is not connected to the server!");
            return ;
        }
        workerGroup.shutdownGracefully();
        return;
    }
    public static void main(String[] args){
        SyncRsyncClient p = new SyncRsyncClient();
        p.run();
        String s = "sfasdfadf;;;dfaadfeiakdf;13143;;434t35;242;jj2;2;;sfea";
        p.sendData(s);
        return;
    }
}
