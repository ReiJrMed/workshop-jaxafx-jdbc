package model.entities;

import java.io.Serializable;

public class Department implements Serializable {
	//se implementa a interface Serializable para os objetos criados terem a possibilidade de se transformar em uma sequência de Bytes
   //ou seja, é possível fazer o objeto ser transformado em arquivo ou ser trafegado em rede
	
	private final static long serialVersionUID = 1L;
	
	private Integer Id;
	private String Name;
	
	public Department() {
		
	}

	public Department(Integer Id, String Name) {
		this.Id = Id;
		this.Name = Name;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer Id) {
		this.Id = Id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Department [Id=" + Id + ", Name=" + Name + "]";
	}	
}
