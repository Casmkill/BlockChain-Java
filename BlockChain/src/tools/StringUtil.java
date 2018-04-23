/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author c.saldarriaga
 */
public class StringUtil {

    public static Pair<String, String> findHash(int index, String prevHash, List<String> txs) {

        String string = String.join("", txs);
        String header = index + prevHash + getMerkleRoot(txs);

        int nonce = 0;
        String blockHash = "";
        String chl ="";
        for (int i = 0; i < blockchain.BlockChain.challenge; i++) {
            chl = chl + "0";
        }

        while (!blockHash.startsWith(chl)) {
            blockHash = applySha256(header + nonce);
            nonce++;
        }
        return new Pair<>(blockHash, String.valueOf(nonce));

    }

    //Applies Sha256 to a string and returns the result. 
    public static String applySha256(String input) {

        byte[] cipher_byte;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for (byte b : cipher_byte) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static String getMerkleRoot(List<String> merkel) {

        if (merkel.isEmpty()) {
            return "";
        } else if (merkel.size() == 1) {

            return applySha256(merkel.get(0));

        }

        List<String> new_merkle = merkel;

        while (new_merkle.size() > 1) {

            if (new_merkle.size() % 2 == 1) {

                new_merkle.add(merkel.get(merkel.size() - 1));
            }

            List<String> result = new ArrayList<>();

            for (int i = 0; i < new_merkle.size(); i += 2) {

                String var1 = applySha256(new_merkle.get(i));
                String var2 = applySha256(new_merkle.get(i + 1));

                String hash = applySha256(var1 + var2);

                result.add(hash);

            }
            new_merkle = result;

        }
        return new_merkle.get(0);

    }

}
