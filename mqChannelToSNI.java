import java.util.Scanner;
import java.util.regex.Pattern;

/*
  Copyright (c) Rob Parker 2022

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

   Contributors:
     Rob Parker - Initial Contribution
*/
public class mqChannelToSNI {

    private final String IBM_MQ_SNI_SUFFIX = ".chl.mq.ibm.com";

    /**
     * Main entry point, obtains the IBM MQ Channel name to convert 
     * and calls the convert function to action.
     * 
     * @param args Given inputs
     */
    public static void main(String[] args) {
        String mqChl;
        mqChannelToSNI run = new mqChannelToSNI();

        if(args.length >= 1){
            mqChl = args[0];
        } else {
            // Prompt for channel name on stdin
            System.out.print("Enter IBM MQ channel name: ");
            Scanner stdin = new Scanner(System.in);
            mqChl = stdin.nextLine();
            stdin.close();
        }

        run.ConvertChlToSNI(mqChl);
    }

    /**
     * Validates the given string is a valid IBM MQ Channel name,
     * converts it to the SNI format and then prints out the result
     * 
     * @param mqChl The IBM MQ Channel name to convert
     */
    public void ConvertChlToSNI(String mqChl) {
        String sni;
        
        if(!validateChannelName(mqChl)) {
            System.out.println("Error: " + mqChl + " is not valid IBM MQ Channel name");
            return;
        }

        if(IssueURLWarning(mqChl)){
            System.out.println("Warning: " + mqChl + " is a valid IBM MQ Channel name but the SNI generated will not be URL valid.");
        } else {
            System.out.println("Converting IBM MQ Channel name: " + mqChl);
        }

        sni = doConvert(mqChl);

        // Print out result
        System.out.println("SNI format is: " + sni);
    }

    /**
     * Converts the given IBM MQ Channel name string into its SNI 
     * format as per: https://www.ibm.com/docs/en/ibm-mq/latest?topic=requirements-how-mq-provides-multiple-certificates-capability
     * 
     * @param mqChl The IBM MQ  Channel name string to convert
     * @return The SNI version of the given string
     */
    private String doConvert(String mqChl) {
        String SNI = "";
        int len = mqChl.length();

        // Iterate through the characters converting them
        for(int i = 0; i < len; i++) {
            char c = mqChl.charAt(i);
            if(Character.isDigit(c)) {
                SNI += c;
            } else if(Character.isUpperCase(c)) {
                SNI += Character.toLowerCase(c);
            } else {
                SNI += String.format("%02x-", (int)c);
            }
        }

        // Add the suffix
        SNI += IBM_MQ_SNI_SUFFIX;

        return SNI;
    }

    /**
     * As per the note box on https://www.ibm.com/docs/en/ibm-mq/latest?topic=requirements-how-mq-provides-multiple-certificates-capability
     * some environments are strict on the SNI naming scheme and how IBM MQ converts a channel name to SNI does not conform to this in all
     * cases. As such if we detect this case we will output a warning.
     * @param channelname The IBM MQ channel name string to verify
     * @return Whether to issue the warning or not
     */
    private boolean IssueURLWarning(String channelname) {
        char c = channelname.charAt(channelname.length()-1);
        if(Pattern.matches("[a-z_\\/%.]", ""+c)){
            // Found a charcter that will be converted to an invalid URL
            return true;
        }
        return false;
    }

    /**
     * Verifies that a given IBM MQ Channel name is valid per the rules 
     * set out in https://www.ibm.com/docs/en/ibm-mq/9.3?topic=objects-rules-naming-mq
     * 
     * @param channelname The IBM MQ channel name string to verify
     * @return true if the string is valid or false if not.
     */
    private boolean validateChannelName(String channelname) {
        // The maximum length of a channel name is 20 characters
        if(channelname.length() > 20  || channelname.length() == 0) {
            return false;
        }

        // The allowed characters in a channel name are:
        // A-Z a-z 0-9 _ / % .
        if(Pattern.matches("[^A-Za-z0-9_\\/%.]", channelname)){
            // Found a character not in the above set
            return false;
        }

        return true;
    }
}
