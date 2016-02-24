package com.rjeady.time;

import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        final String server = "ntp0.cl.cam.ac.uk";
        final int ntpPort = 123;
        final double unixTimeOffset = 2208988800.0;

        DatagramSocket socket = new DatagramSocket();
        InetAddress timeServerAddress = InetAddress.getByName(server);

        byte[] request = new NtpMessage().toByteArray();
        DatagramPacket requestPacket = new DatagramPacket(request, request.length, timeServerAddress, ntpPort);

        byte[] response = new byte[48];
        DatagramPacket responsePacket = new DatagramPacket(response, response.length);

        // update timestamp as late as possible
        NtpMessage.encodeTimestamp(request, 40, System.currentTimeMillis() / 1000 + unixTimeOffset);

        socket.send(requestPacket);
        socket.receive(responsePacket);

        // record incoming timestamp as soon as possible
        double receiptTimestamp = System.currentTimeMillis() / 1000 + unixTimeOffset;

        NtpMessage r = new NtpMessage(response);

        long offsetMS = (long) (500 * ((r.receiveTimestamp - r.originateTimestamp)
                + (r.transmitTimestamp - receiptTimestamp)));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(dateFormat.format(new Date(System.currentTimeMillis() + offsetMS)));
    }
}
