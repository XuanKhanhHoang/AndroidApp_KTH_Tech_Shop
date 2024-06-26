package com.ktm.kthtechshop.listeners;

public interface Cart_CartItem_Checked_Listener {
    void OnCartItemClick(int itemPosition);

    void OnCartItemDelete(int cartId, int sellingPriceItem);

    void OnCartItemAmountChange(int itemPosition, int amountToSet);
}
