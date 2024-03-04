package com.ktm.kthtechshop;

public enum localhostIp {
    LOCALHOST_IP("http://192.168.42.235");
//    LOCALHOST_IP("http://10.0.2.2");//avd
//    LOCALHOST_IP("http://172.16.1.2");//ld

    private final String value;

    localhostIp(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
