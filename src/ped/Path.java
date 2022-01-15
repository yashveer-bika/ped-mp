/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;

/**
 *
 * @author micha
 */
public class Path extends ArrayList<Link> {
    public boolean isConnected() {
        for (int i = 0; i < size() - 1; i++) {
            if (get(i).getDestinationNode() != get(i + 1).getStart()) {
                return false;
            }
        }

        return true;
    }
}