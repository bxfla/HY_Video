package com.sdhy.video.client;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientSocketManager extends SocketManager {
    private static ClientSocketManager sockMgr = new ClientSocketManager();

    private byte[] loginMsg = new byte[28];

    private Map<String, Map<String, Long>> lineMap = new LinkedHashMap<String, Map<String, Long>>();

    private Object lock;

    private int frameDelay = ConstParm.frameDelay;

    private long frameBus = 0;

    public Map<String, Map<String, Long>> getLineMap() {
        return lineMap;
    }

    public void setLineMap(Map<String, Map<String, Long>> lineMap) {
        this.lineMap = lineMap;
    }

    public int getFrameDelay() {
        return frameDelay;
    }

    public long getFrameBus() {
        return frameBus;
    }

    private ClientSocketManager() {
        super();
        lock = new Object();
        initLogin();
    }

    public static ClientSocketManager getManager() {
        return sockMgr;
    }

    public void openLogin() {
        checkCode(loginMsg);
        sendMsg(loginMsg);
        //Log.e("ClietnSocket", loginMsg.toString());
    }

    private static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }

        return h;

    }

    @Override
    protected void parseMsg() {
        String ss = byte2hex(workBuf);
        Log.e("---:",ss);
        byte stateCode = workBuf[6];
        if ((workBuf[0] & 0xFF) == 0x80 && (workBuf[6] & 0xFF) == 0x77) {
            //char c0 = (char)workBuf[28];//通道号。
            char c1 = (char) workBuf[29];
            char c2 = (char) workBuf[30];
            String s = String.format("%c%c", c1, c2);
            int rate = Integer.parseInt(s);
            if (rate != 0) {
                frameDelay = 1000 / rate;
            }
            Log.e("ClientSocketManager", String.valueOf(rate));
            frameBus = ((workBuf[13] & 0xFF) << 24) | ((workBuf[14] & 0xFF) << 16) | ((workBuf[15] & 0xFF) << 8) | (workBuf[16] & 0xFF);
//			Log.e("ClientSocketManager","bus = " + String.valueOf(frameBus));
        }
        System.out.println("workBuf=" + workBuf.toString());
        if ((workBuf[0] & 0xFF) != 0x80 && (workBuf[6] & 0xFF) != 0x83 && (workBuf[6] & 0xFF) != 0x89) {
            return;
        }

        //线路号
        long lineNum = ((workBuf[8] & 0xFF) << 24) | ((workBuf[8] & 0xFF) << 16) | ((workBuf[10] & 0xFF) << 8) | (workBuf[11] & 0xFF);
        //车号
        long busNum = ((workBuf[13] & 0xFF) << 24) | ((workBuf[14] & 0xFF) << 16) | ((workBuf[15] & 0xFF) << 8) | (workBuf[16] & 0xFF);
        long c2 = workBuf[17];
        String lineCode = String.valueOf(lineNum);
        String busCode = String.valueOf(busNum);
        if (busCode.equals("9906")||busCode.equals("9908")||busCode.equals("9910")){
            long c3 = workBuf[18];
            Log.e("XXXXX",c3+"");
            Log.e("XXXXX",busCode+"");
        }

        if (lineMap.containsKey(lineCode)) {
            Map<String, Long> busMap = lineMap.get(lineCode);
            busMap.put(busCode, Long.valueOf(System.currentTimeMillis()));
        } else {
            Map<String, Long> busMap = new LinkedHashMap<String, Long>();
            synchronized (lock) {
                busMap.put(busCode, Long.valueOf(System.currentTimeMillis()));
                lineMap.put(lineCode, busMap);
            }
        }
    }

    private void initLogin() {
        //包头
        loginMsg[0] = (byte) 0xB0;

        //包长度
        loginMsg[1] = 0;
        loginMsg[2] = 28;

        //版本号
        loginMsg[3] = 2;

        //包类型
        loginMsg[6] = (byte) 0xA2;

        loginMsg[7] = 1;
        loginMsg[8] = (byte) ((10001 & 0xFF000000) >>> 24);
        loginMsg[9] = (byte) ((10001 & 0xFF0000) >>> 16);
        loginMsg[10] = (byte) ((10001 & 0xFF00) >>> 8);
        loginMsg[11] = (byte) (10001 & 0xFF);

        loginMsg[12] = 2;
        loginMsg[13] = (byte) ((10001 & 0xFF000000) >>> 24);
        loginMsg[14] = (byte) ((10001 & 0xFF0000) >>> 16);
        loginMsg[15] = (byte) ((10001 & 0xFF00) >>> 8);
        loginMsg[16] = (byte) (10001 & 0xFF);

        loginMsg[17] = (byte) 0x1A;
        loginMsg[18] = 1;

        loginMsg[19] = (byte) 0xF1;
        loginMsg[20] = (byte) 0x31;
        loginMsg[21] = (byte) 0x2C;
        loginMsg[22] = (byte) 0x32;

        loginMsg[27] = (byte) 0xB1;
    }

}
