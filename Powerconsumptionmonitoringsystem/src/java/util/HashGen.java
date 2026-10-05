/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author hoanh
 */
public class HashGen {
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("123456", BCrypt.gensalt()));
    }
}
