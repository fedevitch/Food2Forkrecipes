package ListDataContainer;

/**
 * Created by Lyubomyr on 24.07.2015.
 */

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

//import javax.annotation.Generated;

//@Generated("org.jsonschema2pojo")
public class RecipesList {

    @Expose
    private Integer count;
    @Expose
    private List<Recipe> recipes = new ArrayList<Recipe>();

    /**
     *
     * @return
     * The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     *
     * @return
     * The recipes
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     *
     * @param recipes
     * The recipes
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<String> getAllTitles(){
        List<String> titlesList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            titlesList.add(this.recipes.get(i).getTitle());
        }
        return  titlesList;
    }

    public List<String> getAllPublishers(){
        List<String> publisherList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            publisherList.add(this.recipes.get(i).getPublisher());
        }
        return  publisherList;
    }

    public List<String> getAllPublishersUrl(){
        List<String> publisherUrlList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            publisherUrlList.add(this.recipes.get(i).getPublisherUrl());
        }
        return  publisherUrlList;
    }

    public List<String> getAllRecipeIds(){
        List<String> idList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            idList.add(this.recipes.get(i).getRecipeId());
        }
        return  idList;
    }

    public List<String> getAllImages(){
        List<String> imageList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            imageList.add(this.recipes.get(i).getImageUrl());
        }
        return  imageList;
    }

    public List<Double> getAllRanks(){
        List<Double> rankList = new ArrayList<Double>();
        for(int i = 0; i < this.count; i++)
        {
            rankList.add(this.recipes.get(i).getSocialRank());
        }
        return  rankList;
    }

    public List<String> getAllsources(){
        List<String> sourceList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            sourceList.add(this.recipes.get(i).getSourceUrl());
        }
        return  sourceList;
    }

    public List<String> getAllf2f_url(){
        List<String> f2fList = new ArrayList<String>();
        for(int i = 0; i < this.count; i++)
        {
            f2fList.add(this.recipes.get(i).getF2fUrl());
        }
        return  f2fList;
    }
}
