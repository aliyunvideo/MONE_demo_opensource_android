package com.aliyun.resample;



import java.nio.ByteBuffer;

/**
 * 音频降采样工具，目前只支持44.1 khz降为16khz
 */

public class AliResample {
    static {
        System.loadLibrary("aliresample");
    }
    public static final int IN_SAMPLE = 441 * 2; //*2是因为java上层的byte和底层的short数据上对齐
    public static final int OUT_SAMPLE = 160 * 2;

    private native void nativeInit();
    private native void nativeRelease();
    private native int resample441To160(byte[] out,int outLength,byte[] in, int inLength);

    private byte[] mSaveBuffer;

    public void init(){
        nativeInit();
    }

    public void release(){
        nativeRelease();
    }

    public byte[] from441To160(byte[] inData,int inLength){
        ByteBuffer outBuffer = ByteBuffer.allocate(inLength);
        ByteBuffer inBuffer;
        if(mSaveBuffer != null){
            inBuffer = ByteBuffer.allocate(inLength + mSaveBuffer.length);
            inBuffer.put(mSaveBuffer,0,mSaveBuffer.length);
            inBuffer.put(inData,0,inLength);
            inBuffer.flip();
            inLength += mSaveBuffer.length;
        }else{
            inBuffer = ByteBuffer.wrap(inData);
        }
        int outLength = 0;
        for(int i = 0; i< inLength ;i+= IN_SAMPLE ){
            byte[] bin = new byte[IN_SAMPLE];
            byte[] bout = new byte[OUT_SAMPLE];
            if(i + IN_SAMPLE > inLength){
                mSaveBuffer = new byte[inLength - i];
                inBuffer.get(mSaveBuffer,0,inLength - i);
                break;
            }else{
                inBuffer.get(bin,0,IN_SAMPLE);
            }
            int length = resample441To160(bout,OUT_SAMPLE,bin,IN_SAMPLE);
            outBuffer.put(bout,0,length);
            outLength += length;
        }
        byte[] out = new byte[outLength];
        outBuffer.flip();
        outBuffer.get(out,0,outLength);
        return out;
    }
}
