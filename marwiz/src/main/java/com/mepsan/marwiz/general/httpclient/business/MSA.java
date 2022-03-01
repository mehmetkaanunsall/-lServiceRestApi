/**
 *
 *
 *
 * @author A.Kutay Karakucuk
 *
 * Created on 2:07:44 PM
 */
package com.mepsan.marwiz.general.httpclient.business;

public class MSA {

    public static String encrypt(String input) {

        String c = "";

        for (int i = 0; i < input.length(); i++) {

            int value1 = Integer.valueOf(input.substring(i, i + 1));

            int crypted = value1 ^ 1;

            switch (crypted) {

                case 1:

                    c += "a";

                    break;

                case 3:

                    c += "x";

                    break;

                case 5:

                    c += "b";

                    break;

                case 8:

                    c += "y";

                    break;

                case 9:

                    c += "O";

                    break;

                default:

                    c += crypted;

            }

        }

        return c.toString();

    }

    public static String decrypt(String input) {

        String c = "";

        for (int i = 0; i < input.length(); i++) {

            String value1 = input.substring(i, i + 1);

            int value2 = 0;

            if (value1.equals("a")) {

                value2 = 1;

            } else if (value1.equals("x")) {

                value2 = 3;

            } else if (value1.equals("b")) {

                value2 = 5;

            } else if (value1.equals("y")) {

                value2 = 8;

            } else if (value1.equals("O")) {

                value2 = 9;

            } else {

                value2 = Integer.parseInt(value1);

            }

            int encrypted = value2 ^ 1;

            c += encrypted;

        }

        return c.toString();

    }

}
