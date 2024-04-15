package com.ktm.kthtechshop.dto;

import com.ktm.kthtechshop.model.CreateOrderIntentModel;

import java.util.ArrayList;

public class CreateOrderSendToServer {
    public ArrayList<CreateOrderIntentModel> data;
//    @SerializedName("payment_method_id")
//    public int paymentMethodId;
//    @SerializedName("recipient_name")
//    public int recipientName;
//    @SerializedName("phone_number")
//    public int phoneNumber;
//    public int address;

    public CreateOrderSendToServer(ArrayList<CreateOrderIntentModel> data) {
        this.data = data;
    }

//    public CreateOrderSendToServer(ArrayList<CreateOrderIntentModel> data, int paymentMethodId) {
//        this.data = data;
//        this.paymentMethodId = paymentMethodId;
//    }

//    public CreateOrderSendToServer(ArrayList<CreateOrderIntentModel> data, int paymentMethodId, int recipientName, int phoneNumber, int address) {
//        this.data = data;
//        this.paymentMethodId = paymentMethodId;
//        this.recipientName = recipientName;
//        this.phoneNumber = phoneNumber;
//        this.address = address;
//    }
}
