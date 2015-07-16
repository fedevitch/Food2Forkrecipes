package com.example.lyubomyr.food2forkrecipes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyubomyr on 12.07.2015.
 */
public class RecipeData {

    public Integer NumOfRecipes;

    public List<String> title, social_rank, publisher, publisher_url, f2f_url,
                        source_url,image_url, recipe_id;

    public List<String> ingredients;
    String Title, Publisher, PublisherURL, F2FURL, SourceURL, SocialRank, ImgURL;

    RecipeData(int type){                       //recipes list
        if (type == 1){
            title = new ArrayList<>();
            publisher = new ArrayList<>();
            publisher_url = new ArrayList<>();
            f2f_url = new ArrayList<>();
            source_url = new ArrayList<>();
            social_rank = new ArrayList<>();
            image_url = new ArrayList<>();
            recipe_id = new ArrayList<>();

            NumOfRecipes = 0;
        }
        if (type == 2){                         //details of recipe
            ingredients = new ArrayList<>();
            Title = "";
            Publisher = "";
            PublisherURL = "";
            F2FURL = "";
            SourceURL = "";
            SocialRank = "";
            ImgURL = "";
        }
        else{

        }
    }
}
