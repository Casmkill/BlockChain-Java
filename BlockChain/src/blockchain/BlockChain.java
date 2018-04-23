/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.json.JSONArray;
import tools.StringUtil;

/**
 *
 * @author c.saldarriaga
 */
public class BlockChain {

    private List<Block> blockchain;
    public final static int challenge = 4;



    public BlockChain(int genesis) {
        blockchain = new ArrayList<>();
        if (genesis == 0) {

            int index = 0;
            String previousHash = "0000000000";
            String info
                    = "Republica de Colombia, Registaduria Nacional del Estado "
                    + "     Civil, Documento de Identidad electronico, "
                    + "     Cedula de Ciudadania";
            List<String> txs = new ArrayList<>();
            txs.add(info);

            Pair<String, String> hash_nonce_pair = StringUtil.findHash(index, previousHash, txs);
            Block block = new Block(index, previousHash, hash_nonce_pair.getKey(), hash_nonce_pair.getValue(), txs);
            blockchain.add(block);

        }
    }

    public Block getBlock(int index) {

        for (int i = 0; i < blockchain.size(); i++) {
            if (blockchain.get(i).getIndex() == index) {
                return blockchain.get(i);
            }
        }

        return null;
    }

    // Regresa el número de bloques
    public int getNumOfBlocks() {
        return this.blockchain.size();
    }
    
    
    public int addBlock(int index, String prevHash, String hash, String nonce, List<String> merkle) {
    
        String header = index + prevHash  + StringUtil.getMerkleRoot(merkle) +  nonce;
        
        String challenge = "";
        
        for (int i = 0; i < this.challenge; i++) {
            challenge = challenge + "0";
        }
        
        if(!(StringUtil.applySha256(header).equals(hash)) && (hash.substring(0,this.challenge)).equals(challenge) && index == blockchain.size()) {
            System.out.println("Añadiendo bloque");
            Block block = new Block(index, prevHash, hash, nonce, merkle);
            blockchain.add(block);
            return 1;
        }
        
        
        System.out.println("El hash no concuerda");
        
        
    return 0;
    }
    
    
    public String getLatestBlockhash() {
        return this.blockchain.get(this.blockchain.size()-1).getBlockHash();
    }
    
    
    public JSONArray toJSON() {
        
        JSONArray blockchainJSON = new JSONArray();
        
        for (int i = 0; i < this.blockchain.size(); i++) {
            blockchainJSON.put(this.blockchain.get(i).toJSON());
        }
        
        return blockchainJSON;
    }
    
    
    
    
    int replaceChain(JSONArray chain) {
        
        while(this.blockchain.size() > 1) {
            this.blockchain.remove(this.blockchain.size()-1);
        }
        
        
        for(int a = 1; a < chain.length(); a++) {
            
            
            int index = (int) chain.getJSONObject(a).get("index");
            String prevhash = (String) chain.getJSONObject(a).get("previousHash");
            String hash = (String) chain.getJSONObject(a).get("hash");
            String nonce = (String) chain.getJSONObject(a).get("nonce"); 
            
            
            JSONArray array = new JSONArray(chain.getJSONObject(a).getJSONArray("data").toString());
            
            List <String> data = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                data.add(array.getString(i));
            }

            addBlock(index, prevhash, hash, nonce, data);
        }
        
        return 1;
    }

}
