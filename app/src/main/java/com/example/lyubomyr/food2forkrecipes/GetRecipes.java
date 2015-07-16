package com.example.lyubomyr.food2forkrecipes;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import javax.xml.datatype.Duration;


public class GetRecipes extends Activity {

    final int List_of_recipes = 1;
    final int Detail_of_recipe = 2;
    final String TopRated = "r";
    final String Trending = "t";
    final int Top_or_Trending = 1;
    final int Search = 2;
    public int DisplayType = 1;
    public RecipesQuery Recipes;
    public RecipeData currentRecipe;
    protected String Search_Query;
    LinearLayout llt;
    protected int pageIndex;
    protected String sort;
    Button ChangeSortTypeButton;
    Button SearchButton;
    EditText SearchText;
    Button PreviousButton;
    Button NextButton;
    ScrollView ScrollField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //params for displaying components
        setContentView(R.layout.activity_get_recipes);
        ChangeSortTypeButton = (Button) findViewById(R.id.ChangeSort);
        ChangeSortTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortTypeChanger();
            }
        });
        ChangeSortTypeButton.setEnabled(false);

        SearchText = (EditText) findViewById(R.id.searchField);

        SearchButton = (Button) findViewById(R.id.SearchButton);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement search
                DisplayType = 2;
                Search_Query = SearchText.getText().toString();
                displayRecipes(List_of_recipes, sort, Search, Search_Query);
            }
        });

        PreviousButton = (Button) findViewById(R.id.prev);
        NextButton = (Button) findViewById(R.id.next);
        pageIndex = 1;
        sort = TopRated;
        displayRecipes(pageIndex, sort, Top_or_Trending, TopRated);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displayRecipes(int page, final String sortType, final int displayType, String SearchQuery){
        Toast.makeText(getApplicationContext(), "Downloading list, please wait...", Toast.LENGTH_LONG).show();
        DisplayType = displayType;
        ScrollField = (ScrollView) findViewById(R.id.scrollView);
        ScrollField.scrollTo(0,0);
        llt = (LinearLayout) findViewById(R.id.container);
        PreviousButton.setEnabled(false);
        NextButton.setEnabled(false);
        try{
            llt.removeAllViewsInLayout();
            llt.clearDisappearingChildren();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        LinearLayout.LayoutParams lTitleParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lTextParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lImgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        currentRecipe = new RecipeData(List_of_recipes);
        Recipes = new RecipesQuery();
        if (displayType == Top_or_Trending){                                      //get results for display top/trending
            Search_Query = "";
            Recipes.execute(
                "http://food2fork.com/api/search?key=31a6f30afb8d54d0e8f54b624e200e47&page="
                        + String.valueOf(page) + "&sort=" + sortType);}
        if (displayType == Search){                                                 //get results for search request
            Recipes.execute(
               "http://food2fork.com/api/search?key=31a6f30afb8d54d0e8f54b624e200e47&q="
                       + SearchQuery + "&sort=" + sortType);
        }
        try {
            currentRecipe = jsonParser(Recipes.get(),List_of_recipes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),
                    "Something wrong, please check your Internet connection", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        for(int i = 0; i < currentRecipe.NumOfRecipes; i++){
            //create objects for display top recipes

            TextView Title = new TextView(this);//title
            Title.setLayoutParams(lTitleParams);
            Title.setTextSize(20);
            Title.setText(Html.fromHtml(currentRecipe.title.get(i)));

            TextView RecipeShortInfo = new TextView(this);//short info
            RecipeShortInfo.setLayoutParams(lTextParams);
            RecipeShortInfo.setText("\n Social rank: " + currentRecipe.social_rank.get(i)
                            + "\n Publisher: " + currentRecipe.publisher.get(i)
            );

            ImageView ItemImage = new ImageView(this);//image
            ItemImage.setLayoutParams(lImgParams);

            final Button viewDetails = new Button(this);//button for view details
            viewDetails.setLayoutParams(lButtonParams);
            viewDetails.setText("Details...");
            viewDetails.setId(i);
            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DisplayRecipe(currentRecipe.recipe_id.get(viewDetails.getId()));
                }
            });

            String image = currentRecipe.image_url.get(i);
            ItemImage.setMaxHeight(200);
            ItemImage.setMaxWidth(200);
            new DownloadImageTask(ItemImage).execute(image);
            llt.addView(Title);
            llt.addView(RecipeShortInfo);
            llt.addView(ItemImage);
            llt.addView(viewDetails);
        }
        //set listeners for navigation buttons
        NextButton.setEnabled(true);
        NextButton.setText(String.valueOf(pageIndex + 1) + " >");
        if (pageIndex > 1){
            PreviousButton.setEnabled(true);
            PreviousButton.setText("< " + String.valueOf(pageIndex - 1));
        }
        else{
            PreviousButton.setEnabled(false);
        }
        PreviousButton = (Button) findViewById(R.id.prev);
        PreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecipes(--pageIndex, sort, DisplayType, TopRated);
            }
        });
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecipes(++pageIndex, sort, DisplayType, TopRated);
            }
        });
        ChangeSortTypeButton.setText("Current page: "+String.valueOf(pageIndex));
    }


    public void DisplayRecipe(String id){
        Toast.makeText(getApplicationContext(), "Getting recipe...", Toast.LENGTH_LONG).show();
        ScrollField = (ScrollView) findViewById(R.id.scrollView);
        ScrollField.scrollTo(0,0);
        llt = (LinearLayout) findViewById(R.id.container);
        PreviousButton.setEnabled(false);
        NextButton.setEnabled(false);
        try{
            llt.removeAllViewsInLayout();
            llt.clearDisappearingChildren();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        llt = (LinearLayout) findViewById(R.id.container);
        llt.removeAllViewsInLayout();
        LinearLayout.LayoutParams lTitleParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lTextParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lImgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        RecipeData Recipe = new RecipeData(Detail_of_recipe);
        RecipesQuery Query = new RecipesQuery();
        Query.execute("http://food2fork.com/api/get?key=31a6f30afb8d54d0e8f54b624e200e47&rId="+id);
        try {
            Recipe = jsonParser(Query.get(),Detail_of_recipe);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(),
                    "Something wrong, please check your Internet connection", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //add elements and display info
        TextView Title = new TextView(this);                                            //title
        Title.setLayoutParams(lTitleParams);
        Title.setTextSize(20);
        Title.setText(Html.fromHtml(Recipe.Title));

        TextView RecipeShortInfo = new TextView(this);                              //short info
        RecipeShortInfo.setLayoutParams(lTextParams);
        RecipeShortInfo.setText("\n Social rank: " + Recipe.SocialRank
                        + "\n Publisher: " + Recipe.Publisher
        );

        TextView IngredientsList = new TextView(this);                      //display ingredients
        IngredientsList.setLayoutParams(lTextParams);
        IngredientsList.setTextSize(18);
        String ingredients = "";
        for(int i = 0; i < Recipe.ingredients.size(); i++){
            ingredients += "\n"+Html.fromHtml(Recipe.ingredients.get(i));
        }
        IngredientsList.setText(ingredients);

        TextView ExternalLinkF2F = new TextView(this);                          //F2F link
        ExternalLinkF2F.setLayoutParams(lTextParams);
        ExternalLinkF2F.setTextSize(16);
        ExternalLinkF2F.setClickable(true);
        String F2Flink = "<a href=\""+Recipe.F2FURL +"\">View on Food2Fork</a>";
        ExternalLinkF2F.setText(Html.fromHtml(F2Flink));
        ExternalLinkF2F.setMovementMethod(LinkMovementMethod.getInstance());
        ExternalLinkF2F.setAutoLinkMask(Linkify.WEB_URLS);

        TextView ExternalLinkSource = new TextView(this);                       //Source link
        ExternalLinkSource.setLayoutParams(lTextParams);
        ExternalLinkSource.setClickable(true);
        String Source = "<a href=\""+Recipe.SourceURL+"\">Source</a>";
        ExternalLinkSource.setText(Html.fromHtml(Source));
        ExternalLinkSource.setMovementMethod(LinkMovementMethod.getInstance());
        ExternalLinkSource.setAutoLinkMask(Linkify.WEB_URLS);
        ExternalLinkSource.setTextSize(16);

        TextView ExternalLinkImage = new TextView(this);                        //Image link
        ExternalLinkImage.setLayoutParams(lTextParams);
        ExternalLinkImage.setClickable(true);
        String Image = "<a href=\""+Recipe.ImgURL+"\">Image</a>";
        ExternalLinkImage.setText(Html.fromHtml(Image));
        ExternalLinkImage.setMovementMethod(LinkMovementMethod.getInstance());
        ExternalLinkImage.setAutoLinkMask(Linkify.WEB_URLS);
        ExternalLinkImage.setTextSize(16);

        ImageView ItemImage = new ImageView(this);//image
        ItemImage.setLayoutParams(lImgParams);
        String image = Recipe.ImgURL;
        //ItemImage.setMaxHeight(200);
        //ItemImage.setMaxWidth(200);
        new DownloadImageTask(ItemImage).execute(image);



        llt.addView(Title);
        llt.addView(ItemImage);
        llt.addView(RecipeShortInfo);
        llt.addView(IngredientsList);
        llt.addView(ExternalLinkF2F);
        llt.addView(ExternalLinkSource);
        llt.addView(ExternalLinkImage);

        ChangeSortTypeButton.setText("Recipe");
        NextButton.setEnabled(false);
        NextButton.setText(">");
        PreviousButton.setText("<");
        PreviousButton.setEnabled(true);
        PreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecipes(pageIndex, sort, DisplayType, Search_Query);
            }
        });
    }

    protected RecipeData jsonParser(String strJson, int type){
        JSONObject dataJsonObj;
        RecipeData data = new RecipeData(type);
        if (type == List_of_recipes) {
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray recipes = dataJsonObj.getJSONArray("recipes");

                // get the objects from top rated
                // publisher, url, f2f_url, title, source_url, recipe_id, image_url, social_rank, publisher_url
                data.NumOfRecipes = recipes.length();
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject recipe = recipes.getJSONObject(i);
                    data.title.add(recipe.getString("title"));
                    data.social_rank.add(recipe.getString("social_rank"));
                    data.publisher.add(recipe.getString("publisher"));
                    data.publisher_url.add(recipe.getString("publisher_url"));
                    data.source_url.add(recipe.getString("source_url"));
                    data.image_url.add(recipe.getString("image_url"));
                    data.recipe_id.add(recipe.getString("recipe_id"));
                    data.f2f_url.add(recipe.getString("f2f_url"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (type == Detail_of_recipe){
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONObject recipe = dataJsonObj.getJSONObject("recipe");
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int i = 0; i < ingredients.length(); i++){
                    data.ingredients.add(String.valueOf(ingredients.get(i)));
                }
                data.Title = recipe.getString("title");
                data.SocialRank = recipe.getString("social_rank");
                data.Publisher = recipe.getString("publisher");
                data.PublisherURL = recipe.getString("publisher_url");
                data.SourceURL = recipe.getString("source_url");
                data.ImgURL = recipe.getString("image_url");
                data.F2FURL = recipe.getString("f2f_url");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }



    protected void SortTypeChanger(){
        pageIndex = 1;
        ChangeSortTypeButton.setEnabled(false);
        if (sort.equals(TopRated)){
            sort = Trending;
            ChangeSortTypeButton.setText("sorted by Trending");
            displayRecipes(pageIndex, sort, Top_or_Trending, TopRated);
        }
        if (sort.equals(Trending)){
            sort = TopRated;
            ChangeSortTypeButton.setText("sorted by Top Rated");
            displayRecipes(pageIndex, sort, Top_or_Trending, TopRated);
        }
        else {
            sort = Trending;
            ChangeSortTypeButton.setText("sorted by Trending");
            displayRecipes(pageIndex, sort, Top_or_Trending, Trending);
        }
        ChangeSortTypeButton.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
