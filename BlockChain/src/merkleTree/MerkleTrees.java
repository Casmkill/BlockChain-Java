/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merkleTree;

import java.util.ArrayList;
import java.util.List;
import tools.StringUtil;

/**
 *
 * @author c.saldarriaga
 */
public class MerkleTrees {

    // transaction List
    List<String> txList;

    // Merkle Root
    String root;

    //constructor del árbol de merkle y la clase
    public MerkleTrees(List<String> txList) {

        this.txList = txList;
        root = "";

    }

    public void merkle_tree() {

        List<String> tempTxList = new ArrayList<String>();

        for (int i = 0; i < this.txList.size(); i++) {
            tempTxList.add(this.txList.get(i));
        }

        List<String> newTxList = getNewTxList(tempTxList);
        while (newTxList.size() != 1) {
            newTxList = getNewTxList(newTxList);
        }

        this.root = newTxList.get(0);

    }

    private List<String> getNewTxList(List<String> tempTxList) {

        List<String> newTxList = new ArrayList<String>();
        int index = 0;
        while (index < tempTxList.size()) {
            // left
            String left = tempTxList.get(index);
            index++;

            // right
            String right = "";
            if (index != tempTxList.size()) {
                right = tempTxList.get(index);
            }

            // sha2 hex value
            String sha2HexValue = StringUtil.applySha256(left + right);
            newTxList.add(sha2HexValue);
            index++;

        }

        return newTxList;
    }

    public String getRoot() {
        return root;
    }


    
    

}
