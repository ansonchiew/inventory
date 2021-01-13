package anson.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class SubCategoryPk implements Serializable {
	
	@Column(name = "sub_category_id")
	private Integer subCategoryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
	private Category category;
	
	public SubCategoryPk() {
	}

	public SubCategoryPk(Integer subCategoryId, Category category) {
		this.subCategoryId = subCategoryId;
		this.category = category;
	}

	public Integer getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(Integer subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(subCategoryId).append(category).toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubCategoryPk)) {
            return false;
        }
        SubCategoryPk other = (SubCategoryPk) obj;
        return new EqualsBuilder().append(this.subCategoryId, other.subCategoryId)
                .append(this.category, other.category).isEquals();
    }
	
	@Override
    public String toString() {
		return new ToStringBuilder(this)		
				.append("subCategoryId", subCategoryId)
	            .append("category", category)
	            .toString();
	}
	
}