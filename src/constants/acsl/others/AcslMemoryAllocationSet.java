package constants.acsl.others;

/**
 * Name:        AcslMemoryAllocationSet.java
 * Content:	    This enum lists the memory allocation sets specific elements used in ACSL
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public enum AcslMemoryAllocationSet
{
	EMPTY("empty");

	private final String keyword;

	AcslMemoryAllocationSet(final String keyword)
	{
		this.keyword = keyword;
	}

	public String getKeyword()
	{
		return this.keyword;
	}
}
