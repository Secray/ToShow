package cn.jarlen.photoedit.filters;

public class NativeFilter {
    static {
        System.loadLibrary("nativefilter");
    }


    public native int[] gray(int[] pixels, int width, int height, float factor);


    public native int[] mosatic(int[] pixels, int width, int height,
                                  int factor);

    public native int[] lomo(int[] pixels, int width, int height, float factor);

    public native int[] nostalgic(int[] pixels, int width, int height,
                                    float factor);

    public native int[] comics(int[] pixels, int width, int height,
                                 float factor);

    public native int[] brown(int[] pixels, int width, int height,
                                float factor);

    public native int[] sketchPencil(int[] pixels, int width, int height,
                                       float factor);
}
