package com.example.android.saffronfromzr;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class item {
    private String itemKey;
    private String itemName;
    public item()
    {

    }
    public item(String itemKey,String itemName)
    {
        this.itemKey=itemKey;
        this.itemName=itemName;
    }

    public String getItemKey() {
        return itemKey;
    }

    public String getItemName() {
        return itemName;
    }

}
