package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

/**
 * 获取播放状态回调
 */

public class ClingTransportInfoResponse extends BaseClingResponse<TransportInfo> implements IResponse<TransportInfo> {


    public ClingTransportInfoResponse(ActionInvocation actionInvocation) {
        super(actionInvocation);
    }

    public ClingTransportInfoResponse(ActionInvocation actionInvocation, UpnpResponse operation, String defaultMsg) {
        super(actionInvocation, operation, defaultMsg);
    }

    public ClingTransportInfoResponse(ActionInvocation actionInvocation, TransportInfo info) {
        super(actionInvocation, info);
    }
}
