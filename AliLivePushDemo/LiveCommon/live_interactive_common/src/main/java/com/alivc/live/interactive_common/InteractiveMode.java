package com.alivc.live.interactive_common;

/**
 * @author keria
 * @date 2023/10/27
 * @brief DEMO互动模式业务类型
 */
public enum InteractiveMode {

    /**
     * 1v1连麦
     */
    INTERACTIVE(0x10),

    /**
     * 多人连麦
     */
    MULTI_INTERACTIVE(0x11),

    /**
     * 1v1 PK
     */
    PK(0x20),

    /**
     * 多人PK
     */
    MULTI_PK(0x21),

    /**
     * 1v1推拉裸流
     */
    BARE_STREAM(0x30),

    /**
     * 多人推拉裸流
     */
    MULTI_BARE_STREAM(0x31),
    ;

    private final int mode;

    InteractiveMode(int mode) {
        this.mode = mode;
    }

    /**
     * 是否是直播连麦（先推后拉）
     *
     * @param interactiveMode 互动模式业务类型
     * @return true->是，false->否
     */
    public static boolean isInteractive(InteractiveMode interactiveMode) {
        return interactiveMode == INTERACTIVE
                || interactiveMode == MULTI_INTERACTIVE
                || interactiveMode == PK
                || interactiveMode == MULTI_PK;
    }

    /**
     * 是否是推拉裸流（直推直拉）
     *
     * @param interactiveMode 互动模式业务类型
     * @return true->是，false->否
     */
    public static boolean isBareStream(InteractiveMode interactiveMode) {
        return interactiveMode == BARE_STREAM
                || interactiveMode == MULTI_BARE_STREAM;
    }
}
