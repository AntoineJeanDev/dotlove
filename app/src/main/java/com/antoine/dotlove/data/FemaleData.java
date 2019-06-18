package com.antoine.dotlove.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

public class FemaleData {

    public void FemaleData() {
    }

    public static String getRandomGirlName() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Jessica");
        names.add("Alexandra");
        names.add("Marine");
        names.add("Sophie");
        names.add("Olivia");
        names.add("Emily");
        names.add("Charlotte");
        names.add("Daenerys");

        Random random = new Random();
        int index = random.nextInt(names.size());

        return names.get(index);
    }

    public static String getRandomGirlPicture() {
        ArrayList<String> photos = new ArrayList<>();
        photos.add("https://www.telegraph.co.uk/content/dam/beauty/2016/04/27/Jennifer-Aniston_trans_NvBQzQNjv4BqkX3onuOCNa0uuigJZQxtEBOJpD29tp1fMw9A0Cp6c54.jpg?imwidth=1400");
        photos.add("https://imgix.ranker.com/user_node_img/80/1588010/original/mila-kunis-people-in-tv-photo-u243?w=650&q=50&fm=pjpg&fit=crop&crop=faces");
        photos.add("https://imgix.ranker.com/user_node_img/89/1776363/original/pen_lope-cruz-photo-u207?w=650&q=50&fm=pjpg&fit=crop&crop=faces");
        photos.add("https://images.fandango.com/ImageRenderer/0/0/redesign/static/img/default_poster.png/0/images/masterrepository/other/1622M04_JO079_H.JPG");
        photos.add("https://s3.r29static.com//bin/entry/01c/0,0,2000,2400/x/1692607/image.gif");

        Random random = new Random();
        int index = random.nextInt(photos.size());

        return photos.get(index);
    }
}
