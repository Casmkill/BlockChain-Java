/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author c.saldarriaga
 */
public class Block {

    private int index;
    private String previousHash;
    private String blockHash;
    private String nonce;
    private List<String> data;
    
    
    

    public Block(int index, String previousHash, String blockHash, String nonce, List<String> data) {
        this.index = index;
        this.previousHash = previousHash;
        this.blockHash = blockHash;
        this.nonce = nonce;
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getNonce() {
        return nonce;
    }

    public List<String> getData() {
        return data;
    }
    
        public JSONObject toJSON() {

        JSONObject item = new JSONObject();
        item.put("index", index);
        item.put("hash", blockHash);
        item.put("previousHash", previousHash);
        item.put("nonce", nonce);
        JSONArray array = new JSONArray(data);
        item.put("data", array);
        return item;

    }

    @Override
    public String toString() {
        return "Block{" + "index=" + index + ", previousHash=" + previousHash + ", blockHash=" + blockHash + ", nonce=" + nonce + ", data=" + data + '}';
    }



}
