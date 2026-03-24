package misc;

/**
 * Name:        Pair.java
 * Content:     This class provides a simple yet useful definition of a Pair of objects that can be used for many
 * 				purposes.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/03/26
 */

public class Pair<T, U>
{
	private final T firstElement;
	private final U secondElement;

	//Constructors

	public Pair(final T firstElement,
				final U secondElement)
	{
		this.firstElement = firstElement;
		this.secondElement = secondElement;
	}

	//Overrides

	@Override
	public String toString()
	{
		return String.format(
			"(%s, %s)",
			this.firstElement == null ? "null" : this.firstElement.toString(),
			this.secondElement == null ? "null" : this.secondElement.toString()
		);
	}

	//Public methods

	public T getFirstElement()
	{
		return this.firstElement;
	}

	public T getLeftElement()
	{
		return this.getFirstElement();
	}

	public U getSecondElement()
	{
		return this.secondElement;
	}

	public U getRightElement()
	{
		return this.getSecondElement();
	}
}
