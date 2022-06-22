package com.adobe.aem.lacounty.dpss.core.beans;

public class ProgramsServicesBean implements Comparable<ProgramsServicesBean> {
	String title;
	String description;
	String link;

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "pgrm [Title=" + title + "]";
	}

	/*
	 * Custom code to sort based on title (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ProgramsServicesBean arg0) {
		if (null != this.getTitle() && null != arg0.getTitle()) {
			return this.getTitle().compareToIgnoreCase(arg0.getTitle());
		} else {
			return -1;
		}
	}

	/*
	 * Auto generated code for hashCode (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/*
	 * Auto generated code for equals (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgramsServicesBean other = (ProgramsServicesBean) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

}
