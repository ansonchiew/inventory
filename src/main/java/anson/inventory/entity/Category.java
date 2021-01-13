package anson.inventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "category")
public class Category {
	
	@Id
	private Integer categoryId;
	
	@Column(name = "name", unique = true)
	private String name;

	public Category() {		
	}
	
	public Category(Integer categoryId, String name) {
		this.categoryId = categoryId;
		this.name = name;
	}
		
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(categoryId).toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) {
            return false;
        }
        Category other = (Category) obj;
        return new EqualsBuilder().append(this.categoryId, other.categoryId).isEquals();
    }
	
	@Override
    public String toString() {
		return new ToStringBuilder(this)		
				.append("categoryId", categoryId)
	            .append("name", name)
	            .toString();
	}
	
}