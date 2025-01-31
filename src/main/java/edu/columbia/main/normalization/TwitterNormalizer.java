package edu.columbia.main.normalization;

import com.twitter.Extractor;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.Fitzpatrick;

import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Gideon on 9/7/15.
 */
public class TwitterNormalizer {

    private Extractor extractor;
    public TwitterNormalizer(){
        extractor = new Extractor();
    }
    public String cleanTweet(String tweet){

        if (tweet == null)
             return null;
        if (tweet.equals(""))
            return "";

        ArrayList<String>toRemove = new ArrayList<String>();

        ArrayList<String> hashtags = (ArrayList<String>) extractor.extractHashtags(tweet);
        ArrayList<String> cashtags = (ArrayList<String>) extractor.extractCashtags(tweet);
        ArrayList<String> urls = (ArrayList<String>) extractor.extractURLs(tweet);
        ArrayList<String> mentions = (ArrayList<String>) extractor.extractMentionedScreennames(tweet);


        toRemove.addAll(hashtags);
        toRemove.addAll(cashtags);
        toRemove.addAll(urls);
        toRemove.addAll(mentions);


        for(String remove: toRemove){
            tweet = tweet.replaceAll(remove,"");
        }

        tweet = tweet.replaceAll("@","").replaceAll("RT","").replaceAll(":","");
        tweet = removeEmojis(tweet);

        return tweet;
    }

    public String toUnicode(String str){
        String unicodeString = "";
        for (int i = 0; i < str.length(); i++){
            unicodeString += "\\u" + Integer.toHexString(str.charAt(i) | 0x10000).substring(1);
        }
        return unicodeString;
    }

    /**
     * Removes all emojis and variations from a String
     */
    public String removeEmojis(String str) {
        //remove all fitzpatrick modifiers
        for (Fitzpatrick fitzpatrick : Fitzpatrick.values()) {
            str = str.replaceAll(fitzpatrick.unicode, "");
        }
        //remove all emojis
        for (Emoji emoji : EmojiManager.getAll()) {
            str = str.replaceAll(emoji.getUnicode(), "");
        }

        // added for Elizabeth's jp-en data collection
        // remove all kaomoji (read in from file)
        try{
            File kaomojiFile = new File("/Users/elizabeth/Code/Babler/src/main/resources/kaomoji.txt");
            Scanner scanner = new Scanner(kaomojiFile);
            while(scanner.hasNextLine()){
                String kaomoji = scanner.nextLine();
                kaomoji = kaomoji.trim();
                String unicodeKaomoji = toUnicode(kaomoji);

                // Remove kaomoji
                str = str.replaceAll(unicodeKaomoji, "");
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }

        return str;
    }

}
