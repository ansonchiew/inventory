package anson.inventory.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "sub_category")
public class SubCategory {
	
	@EmbeddedId
	private SubCategoryPk subCategoryPk = new SubCategoryPk();
	
	@Column(name = "name", unique = true)
	private String name;
	
	public SubCategory() {
	}

	public SubCategory(SubCategoryPk subCategoryPk, String name) {
		this.subCategoryPk = subCategoryPk;
		this.name = name;
	}

	public SubCategoryPk getSubCategoryPk() {
		return subCategoryPk;
	}

	public void setSubCategoryPk(SubCategoryPk subCategoryPk) {
		this.subCategoryPk = subCategoryPk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Category getCategory() {
		return subCategoryPk.getCategory();
	}
	
	public void setCategory(Category category) {
		subCategoryPk.setCategory(category);
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(subCategoryPk).toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubCategory)) {
            return false;
        }
        SubCategory other = (SubCategory) obj;
        return new EqualsBuilder().append(this.subCategoryPk, other.subCategoryPk).isEquals();
    }
	
	@Override
    public String toString() {
		return new ToStringBuilder(this)		
				.append("subCategoryPk", subCategoryPk)
	            .append("name", name)
	            .toString();
	}

}