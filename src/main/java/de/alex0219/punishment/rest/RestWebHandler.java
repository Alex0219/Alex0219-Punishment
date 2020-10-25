package de.alex0219.punishment.rest;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.CustomPunishment;
import de.alex0219.punishment.ban.reason.CustomPunishmentReason;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import net.md_5.bungee.BungeeCord;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Created by Alexander on 19.08.2020 03:56
 * © 2020 Alexander Fiedler
 */
public class RestWebHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;

    private StringBuilder payload = new StringBuilder();
    public HashMap<String, Boolean> cache = new HashMap<>();

    public static void sendText(ChannelHandlerContext ctx, String s) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(s, CharsetUtil.UTF_8));

        try {
            response.content().setBytes(s.getBytes("UTF-8").length, s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            response.content().setBytes(s.getBytes().length, s.getBytes());
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response);
    }

    public static void redirect(ChannelHandlerContext ctx, String red) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT,
                Unpooled.copiedBuffer("\n\r", CharsetUtil.UTF_8));

        response.content().setBytes("\n\r".getBytes().length, "\n\r".getBytes());

        response.headers().set(HttpHeaderNames.LOCATION, red);
        response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response);

    }

    public static void redirectTemporary(ChannelHandlerContext ctx, String red) {
        redirect(ctx, red, HttpResponseStatus.TEMPORARY_REDIRECT);
    }

    public static void redirectSeeOther(ChannelHandlerContext ctx, String red) {
        redirect(ctx, red, HttpResponseStatus.SEE_OTHER);
    }

    public static void redirect(ChannelHandlerContext ctx, String red, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("\n\r", CharsetUtil.UTF_8));

        response.content().setBytes("\n\r".getBytes().length, "\n\r".getBytes());

        response.headers().set(HttpHeaderNames.LOCATION, red);
        response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response);
    }

    public static String getIp(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            if (socketAddress.getAddress().toString().equalsIgnoreCase("/0:0:0:0:0:0:0:1")) {
                return "127.0.0.1";
            }
            return socketAddress.getAddress().toString();
        }
        return "";
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            payload = new StringBuilder();
            this.request = (HttpRequest) msg;
        } else if (msg instanceof HttpContent) {
            if (((HttpContent) msg).content().isReadable())
                payload.append(((HttpContent) msg).content().toString(CharsetUtil.UTF_8));
            if (msg instanceof LastHttpContent) {

                if (request.uri().startsWith("/control/banuser&")) {
                    final String[] args = request.uri().split("&");

                    final String username = args[1];
                    final String reason = args[2];
                    final long duration = Long.parseLong(args[3]);
                    final String executor = args[4];

                    DBUser bannedPlayer;
                    if(BungeeCord.getInstance().getPlayer(args[0]) !=null) {
                        bannedPlayer = PunishmentBootstrap.getInstance().getRankManager().getDBUser(username);
                    } else {
                        bannedPlayer = new DBUser(UUIDFetcher.getUUID(username), username);
                    }

                    if (!bannedPlayer.userExists()) {
                        System.out.println("Player is unknown. ");
                        sendText(ctx,"ERROR");
                        return;
                    }
                    System.out.println(username);
                    System.out.println(reason);
                    System.out.println(duration);
                    final CustomPunishmentReason customPunishmentReason = new CustomPunishmentReason(reason);
                    final CustomPunishment customPunishment = new CustomPunishment(executor,bannedPlayer,customPunishmentReason);
                    PunishmentBootstrap.getInstance().getBanManager().customBanPlayer(customPunishment,duration);

                } else if (request.uri().startsWith("/control/kickuser&"))  {
                    final String[] args = request.uri().split("&");
                    final String message = args[1];
                    if(BungeeCord.getInstance().getPlayer(args[0]) !=null) {
                        if(message !=null) {
                            BungeeCord.getInstance().getPlayer(args[0]).disconnect();
                        }
                    } else {
                        sendText(ctx,"ERROR");
                    }
                } else {
                    sendText(ctx,request.uri());
                }

            }



        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}

