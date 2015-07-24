package RecipeDataContainer;

/**
 * Created by Lyubomyr on 24.07.2015.
 */

import com.google.gson.annotations.Expose;

//@Generated("org.jsonschema2pojo")
public class RecipeDetails {

    @Expose
    private Recipe recipe;

    /**
     *
     * @return
     * The recipe
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     *
     * @param recipe
     * The recipe
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

}
