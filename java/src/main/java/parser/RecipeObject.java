package parser;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sajimathew on 12/5/17.
 */
public class RecipeObject {
    private String name;
    private String url;
    private List<Ingredient> ingredients;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Ingredient> getIngredients() {
        if(ingredients == null)
            ingredients = new LinkedList<>();
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RecipeObject)) return false;

        RecipeObject that = (RecipeObject) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(url, that.url)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(url)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    static class Ingredient{
        private  String fullText;
        private String units;
        private String measurement;
        private String style;
        private String item;
        private String notes;

        public Ingredient() {
        }

        public String getFullText() {
            return fullText;
        }

        public void setFullText(String fullText) {
            this.fullText = fullText;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public String getMeasurement() {
            return measurement;
        }

        public void setMeasurement(String measurement) {
            this.measurement = measurement;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }


}
