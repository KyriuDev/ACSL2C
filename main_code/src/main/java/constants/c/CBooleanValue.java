package constants.c;

/**
 * Name:        CBooleanValue.java
 * Content:     This enum lists the different boolean values in C.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/04/26
 */

public enum CBooleanValue
{
	FALSE(0),
	TRUE(1)
	;

	private final int intValue;

	//Constructors

	CBooleanValue(final int intValue)
	{
		this.intValue = intValue;
	}

	//Public methods

	public int getIntValue()
	{
		return this.intValue;
	}

	public String getIntValueString()
	{
		return String.valueOf(this.intValue);
	}
}
