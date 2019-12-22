package NettySelf.NettyServer.SyncServer;

import Sync.Sync_PciSrc;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class SyncPciServer {
    private int port;
    public SyncPciServer(int port){
        this.port = port;
    }
    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        final ByteBuf delimiter = Unpooled.copiedBuffer(";".getBytes());

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,delimiter));
                            ch.pipeline().addLast(new SyncPciServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            //Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            //Wait until the server socket is closed.
            //In this example, this does not happen, but you can do that to gracefully shut down your server.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void startSyncPciServer(){
        port=(port==0)?8081:port;
        new SyncPciServer(port).run();
    }

    public static void main(String[] args){
        int port = 8081;
        new SyncPciServer(port).run();
    }
}
