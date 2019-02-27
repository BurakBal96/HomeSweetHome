package net.ddns.morigg.homesweethomeapp.utilities;

import java.security.MessageDigest;

/**
 * Created by MoriartyGG on 17.04.2018.
 */

public class Sha512 {
    public static byte[] encryptMD5(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance("SHA-512");
        md5.update(data);
        return md5.digest();

    }
}
