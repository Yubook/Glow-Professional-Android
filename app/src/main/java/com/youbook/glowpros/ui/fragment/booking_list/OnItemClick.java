package com.youbook.glowpros.ui.fragment.booking_list;

public interface OnItemClick {
    void onClick (String value, String orderId, String userId);
    void onCompleteOrder(String orderId);
}