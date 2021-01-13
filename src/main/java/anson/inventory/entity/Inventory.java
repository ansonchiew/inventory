package anson.inventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "inventory")
public class Inventory implements Comparable<Inventory> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_id_generator")
	@SequenceGenerator(name="inventory_id_generator", sequenceName = "inventory_id_seq", allocationSize=50)
	@Column(name="inventory_id")
	private Integer id;
		
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "sub_category_id", referencedColumnName = "sub_category_id"),
        @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    })
	private SubCategory subCategory = new SubCategory();
			
	@Column(name = "name", unique = true)
	private String name;
	
	@Column(name = "quantity")
	private int quantity;
	
	public Inventory() {
	}
	
	public Inventory(SubCategory subCategory, String name, int quantity) {
		this.subCategory = subCategory;
		this.name = name;
		this.quantity = quantity;
	}
		
	public Integer getId() {
		return id;
	}

	public void setId(Integer inventoryId) {
		this.id = inventoryId;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}
	
	public Category getCategory() {
		return subCategory.getCategory();
	}
	
	public void setCategory(Category category) {
		subCategory.setCategory(category);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Inventory)) {
            return false;
        }
        Inventory other = (Inventory) obj;
        return new EqualsBuilder().append(this.id, other.id).isEquals();
    }
	
	@Override
    public String toString() {
		return new ToStringBuilder(this)		
				.append("id", id)
	            .append("subCategory", subCategory)
	            .append("name", name)
	            .append("quantity", quantity)
	            .toString();
	}

	@Override
	public int compareTo(Inventory other) {
		return this.name.compareTo(other.name);
	}
	
}