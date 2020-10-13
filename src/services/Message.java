package services;

/**
 * Message envoyé aux écouteurs (abonnés)
 * Le mesage est de type générique pour permettre de renvoyer des personnes ou des places,...
 * 
 * @author Didier
 *
 */
public class Message<T> {
	private final TypeOperation op;
	private final T element;

	public Message(TypeOperation op, T element) {
		super();
		this.op = op;
		this.element = element;
	}

	public TypeOperation getOp() {
		return op;
	}

	public T getElement() {
		return element;
	}
	
}
